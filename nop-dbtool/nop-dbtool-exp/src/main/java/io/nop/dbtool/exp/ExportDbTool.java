/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.dbtool.exp;

import io.nop.api.core.convert.ConvertHelper;
import io.nop.api.core.util.FutureHelper;
import io.nop.api.core.util.Guard;
import io.nop.batch.core.BatchTaskBuilder;
import io.nop.batch.core.IBatchConsumerProvider;
import io.nop.batch.core.IBatchLoaderProvider;
import io.nop.batch.core.IBatchProcessorProvider;
import io.nop.batch.core.IBatchTask;
import io.nop.batch.core.IBatchTaskContext;
import io.nop.batch.core.consumer.MultiBatchConsumerProvider;
import io.nop.batch.core.consumer.ResourceRecordConsumerProvider;
import io.nop.batch.core.impl.BatchTaskContextImpl;
import io.nop.batch.jdbc.consumer.GenInsertSqlRecordIO;
import io.nop.batch.jdbc.loader.JdbcBatchLoaderProvider;
import io.nop.commons.concurrent.executor.DefaultThreadPoolExecutor;
import io.nop.commons.concurrent.executor.GlobalExecutors;
import io.nop.commons.concurrent.executor.IThreadPoolExecutor;
import io.nop.commons.concurrent.executor.SyncThreadPoolExecutor;
import io.nop.commons.util.StringHelper;
import io.nop.core.lang.json.JsonTool;
import io.nop.core.resource.IResourceLoader;
import io.nop.core.resource.component.ResourceComponentManager;
import io.nop.core.resource.impl.FileResource;
import io.nop.core.resource.record.IResourceRecordIO;
import io.nop.core.resource.record.csv.CsvResourceRecordIO;
import io.nop.dao.dialect.DialectManager;
import io.nop.dao.dialect.IDialect;
import io.nop.dao.jdbc.impl.JdbcFactory;
import io.nop.dbtool.core.DataBaseMeta;
import io.nop.dbtool.core.discovery.jdbc.JdbcMetaDiscovery;
import io.nop.dbtool.exp.config.ExportDbConfig;
import io.nop.dbtool.exp.config.ExportTableConfig;
import io.nop.dbtool.exp.config.JdbcConnectionConfig;
import io.nop.dbtool.exp.config.TableFieldConfig;
import io.nop.dbtool.exp.state.EtlTaskStateStore;
import io.nop.orm.model.IColumnModel;
import io.nop.orm.model.OrmEntityModel;
import io.nop.xlang.api.XLang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ExportDbTool {
    static final Logger LOG = LoggerFactory.getLogger(ExportDbTool.class);

    private ExportDbConfig config;
    private Map<String, Object> args;

    private IResourceLoader outputResourceLoader;

    private IDialect dialect;

    private DataSource dataSource;

    private File stateFile;

    private EtlTaskStateStore stateStore;

    public void setConfig(ExportDbConfig config) {
        this.config = config;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public void addArg(String name, Object value) {
        if (args == null)
            args = new LinkedHashMap<>();
        args.put(name, value);
    }

    public File getStateFile() {
        return stateFile;
    }

    public void setStateFile(File stateFile) {
        this.stateFile = stateFile;
    }

    public ExportDbConfig getConfig() {
        return config;
    }

    public void setConfigPath(String configPath) {
        this.setConfig((ExportDbConfig) ResourceComponentManager.instance().loadComponentModel(configPath));
    }

    public void syncConfigWithDb() {
        JdbcConnectionConfig conn = config.getJdbcConnection();
        this.dataSource = conn.buildDataSource();
        if (conn.getDialect() == null) {
            this.dialect = DialectManager.instance().getDialectForDataSource(dataSource);
        } else {
            this.dialect = DialectManager.instance().getDialect(conn.getDialect());
        }
        readTableMetas();

        LOG.info("nop.export-db.config=\n{}", JsonTool.serialize(config, true));
    }

    public void execute() {
        Guard.notEmpty(config.getJdbcConnection(), "jdbc-connection");

        syncConfigWithDb();

        if (stateFile != null) {
            stateStore = new EtlTaskStateStore(stateFile);
            if (stateStore.isCompleted()) {
                LOG.info("nop.export-db.skip-since-already-completed");
                return;
            }
        }

        this.outputResourceLoader = new FileResource(new File(config.getOutputDir()));

        IThreadPoolExecutor executor;
        if (config.getThreadCount() > 1) {
            executor = DefaultThreadPoolExecutor.newExecutor("export-db", config.getThreadCount(), Integer.MAX_VALUE);
        } else {
            executor = SyncThreadPoolExecutor.INSTANCE;
        }

        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (ExportTableConfig tableConfig : config.getTables()) {
                if (!isExportable(tableConfig))
                    continue;

                if (stateStore != null && stateStore.isTableCompleted(tableConfig.getName())) {
                    LOG.info("nop.export-db.skip-table-when-already-completed:tableName={}", tableConfig.getName());
                    continue;
                }

                if (stateStore != null)
                    stateStore.resetTableState(tableConfig.getName());

                futures.add(executor.submit(() -> runTask(tableConfig, dataSource), null));
            }
            FutureHelper.getFromFuture(FutureHelper.waitAll(futures));

            if (stateStore != null)
                stateStore.complete();
        } finally {
            executor.destroy();
        }
    }

    private boolean isExportable(ExportTableConfig tableConfig) {
        if (config.getCheckExportable() == null)
            return true;
        return ConvertHelper.toTruthy(config.getCheckExportable()
                .call1(null, tableConfig, XLang.newEvalScope()));
    }

    private void readTableMetas() {
        Map<String, List<ExportTableConfig>> map = new HashMap<>();
        if (config.getTables() != null) {
            config.getTables().forEach(table -> {
                String name = StringHelper.lowerCase(table.getSourceTableName());
                map.computeIfAbsent(name, k -> new ArrayList<>()).add(table);
            });
        }

        if (config.getExcludeTableNames() != null) {
            map.keySet().removeAll(config.getExcludeTableNames());
            config.getTables().removeIf(table -> {
                return config.getExcludeTableNames().contains(table.getName());
            });
        }

        JdbcConnectionConfig conn = config.getJdbcConnection();

        String tableNamePattern = config.getTableNamePattern();

        if (config.isNeedDatabaseMeta()) {
            DataBaseMeta meta = JdbcMetaDiscovery.forDataSource(dataSource)
                    .discover(conn.getCatalog(), config.getSchemaPattern(), tableNamePattern);

            for (OrmEntityModel table : meta.getTables().values()) {
                String name = StringHelper.lowerCase(table.getTableName());

                if (config.getExcludeTableNames() != null && config.getExcludeTableNames().contains(name))
                    continue;

                if (!config.isExportAllTables()) {
                    if (!map.containsKey(name))
                        continue;
                }

                mergeTableConfig(map, name, table);
            }
        }
    }

    private void mergeTableConfig(Map<String, List<ExportTableConfig>> map, String name, OrmEntityModel table) {
        List<ExportTableConfig> list = map.get(name);
        if (list == null) {
            ExportTableConfig tableConfig = new ExportTableConfig();
            tableConfig.setName(name);
            for (IColumnModel col : table.getColumns()) {
                TableFieldConfig field = new TableFieldConfig();
                field.setName(col.getCode());
                field.setStdDataType(col.getStdDataType());
                field.setStdSqlType(col.getStdSqlType());
                tableConfig.addField(field);
            }
            config.addTable(tableConfig);
            map.put(name, Collections.singletonList(tableConfig));
        } else {
            for (ExportTableConfig old : list) {
                if (!old.isExportAllFields())
                    continue;

                Set<String> sourceNames = old.getSourceFieldNames();
                for (IColumnModel col : table.getColumns()) {
                    if (sourceNames.contains(col.getCode()) || old.hasField(col.getCode()))
                        continue;

                    TableFieldConfig field = new TableFieldConfig();
                    field.setName(col.getCode());
                    field.setStdDataType(col.getStdDataType());
                    field.setStdSqlType(col.getStdSqlType());
                    old.addField(field);
                }
            }
        }
    }

    private void runTask(ExportTableConfig tableConfig, DataSource ds) {
        BatchTaskBuilder builder = new BatchTaskBuilder();
        builder.taskName(tableConfig.getName());
        builder.loader(newLoader(tableConfig, ds));
        builder.batchSize(config.getBatchSize());
        builder.processor(newProcessor(tableConfig));
        builder.consumer(newConsumer(tableConfig));

        int concurrency = getConcurrency(tableConfig);
        builder.concurrency(concurrency);
        if (concurrency > 1)
            builder.executor(GlobalExecutors.cachedThreadPool());

        if (stateStore != null)
            builder.stateStore(stateStore.getTableStore(tableConfig.getName()));

        IBatchTaskContext context = new BatchTaskContextImpl();
        if (args != null)
            context.getEvalScope().setLocalValues(args);
        IBatchTask task = builder.buildTask();


        LOG.info("nop.export-db.begin-export-table:tableName={}", tableConfig.getName());
        context.onAfterComplete(err -> {
            LOG.info("nop.export-db.end-export-table:tableName={}", tableConfig.getName());
        });

        task.execute(context);
    }

    protected int getConcurrency(ExportTableConfig tableConfig) {
        if (tableConfig.getConcurrency() != null)
            return tableConfig.getConcurrency();
        if (config.getConcurrencyPerTable() != null)
            return config.getConcurrencyPerTable();
        return 1;
    }

    private IBatchProcessorProvider<Map<String, Object>, Map<String, Object>> newProcessor(ExportTableConfig tableConfig) {
        return new FieldsProcessor(tableConfig.getFields(), tableConfig.getTransformExpr());
    }

    private IBatchLoaderProvider<Map<String, Object>> newLoader(ExportTableConfig tableConfig,
                                                                DataSource ds) {
        JdbcBatchLoaderProvider<Map<String, Object>> loader = new JdbcBatchLoaderProvider<>();
        loader.setStreaming(config.isStreaming());
        loader.setJdbcTemplate(JdbcFactory.newJdbcTemplateFor(ds));
        loader.setSqlGenerator(ctx -> tableConfig.buildSQL(config.getFetchSize(), ctx));
        return loader;
    }

    private IBatchConsumerProvider<Map<String, Object>> newConsumer(ExportTableConfig tableConfig) {
        List<IBatchConsumerProvider<Map<String, Object>>> list = new ArrayList<>();
        List<String> fields = tableConfig.getTargetFieldNames();

        if (config.getExportFormats() != null) {
            for (String format : config.getExportFormats()) {
                String fileName = tableConfig.getExportFileName(format);
                if ("sql".equals(format)) {
                    list.add(newGenSqlConsumer(fileName, fields));
                } else if ("csv".equals(format) || "csv.gz".equals(format)) {
                    list.add(newCsvConsumer(fileName, fields));
                } else {
                    throw new IllegalArgumentException("nop.err.dbtool.invalid-exp-format:" + format);
                }
            }
        }
        if (list.isEmpty())
            return newCsvConsumer(tableConfig.getExportFileName("csv"), fields);
        return MultiBatchConsumerProvider.fromList(list);
    }

    private IBatchConsumerProvider<Map<String, Object>> newCsvConsumer(String resourcePath, List<String> fields) {
        CsvResourceRecordIO<Map<String, Object>> recordIO = new CsvResourceRecordIO<>();
        recordIO.setRecordType(Map.class);
        recordIO.setSupportZip(true);
        recordIO.setHeaders(fields);

        return newResourceConsumer(recordIO, resourcePath);
    }

    private IBatchConsumerProvider<Map<String, Object>> newGenSqlConsumer(String resourcePath,
                                                                          List<String> fields) {
        GenInsertSqlRecordIO recordIO = new GenInsertSqlRecordIO();
        recordIO.setDialect(dialect.getName());
        recordIO.setFields(fields);
        return newResourceConsumer(recordIO, resourcePath);
    }

    private <S> ResourceRecordConsumerProvider<S> newResourceConsumer(IResourceRecordIO<S> recordIO,
                                                                      String resourcePath) {
        ResourceRecordConsumerProvider<S> consumer = new ResourceRecordConsumerProvider<>();
        consumer.setRecordIO(recordIO);
        consumer.setResourcePath(resourcePath);
        consumer.setResourceLocator(outputResourceLoader);
        return consumer;
    }
}