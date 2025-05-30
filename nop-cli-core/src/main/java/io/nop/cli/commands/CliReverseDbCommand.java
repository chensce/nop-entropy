/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.cli.commands;

import io.nop.dao.jdbc.datasource.SimpleDataSource;
import io.nop.dbtool.core.DataBaseMeta;
import io.nop.dbtool.core.discovery.jdbc.JdbcMetaDiscovery;
import io.nop.orm.model.OrmModel;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "reverse-db",
        mixinStandardHelpOptions = true,
        description = "对数据库进行逆向工程分析，生成Excel模型文件"
)
public class CliReverseDbCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-c", "--driverClass"}, description = "JDBC驱动类", required = true)
    String driverClassName;

    @CommandLine.Option(names = {"-j", "--jdbcUrl"}, description = "jdbc连接", required = true)
    String jdbcUrl;

    @CommandLine.Option(names = {"-u", "--username"}, description = "数据库用户名", required = true)
    String username;

    @CommandLine.Option(names = {"-p", "--password"}, description = "数据库密码")
    String password;

    @CommandLine.Option(names = {"-t", "--table"}, description = "数据库表模式，例如litemal%%表示匹配litemall为前缀的表")
    String table;

    @CommandLine.Option(names = {"-o", "--output"}, description = "输出文件（缺省输出到命令行窗口中）")
    File outputFile;

    @CommandLine.Option(names = {"-d", "--dump"}, description = "输出文件（缺省输出到命令行窗口中）")
    boolean dump;

    @CommandLine.Option(names = {"-n", "--ignoreUnknownType"}, description = "忽略未知类型")
    boolean ignoreUnknownType;

    @CommandLine.Option(names = {"-k", "--ignoreKey"}, description = "忽略唯一键和索引等信息")
    boolean ignoreKey;

    @CommandLine.Parameters(description = "数据库Catalog", index = "0")
    String catalog;

    @CommandLine.Option(names = {"-s", "--schema"}, description = "数据库Schema")
    String schemaPattern;

    @Override
    public Integer call() {
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setPassword(password);
        dataSource.setUsername(username);
        dataSource.setUrl(jdbcUrl);
        dataSource.setCatalog(catalog);
        dataSource.setDriverClassName(driverClassName);

        JdbcMetaDiscovery discovery = JdbcMetaDiscovery.forDataSource(dataSource);
        discovery.ignoreUnknownType(ignoreUnknownType);
        if (ignoreKey)
            discovery.includeUniqueKeys(false).includeIndexes(false);

        discovery.basePackageName("app");
        DataBaseMeta meta = discovery.discover(catalog, schemaPattern, table == null ? "%" : table);

        OrmModel model = meta.getOrmModel();

        File outputFile = this.outputFile;
        if (outputFile == null)
            outputFile = new File("out.orm.xlsx");

        if (outputFile.getName().endsWith(".xml")) {
            GenOrmHelper.saveOrmXml(model, outputFile);
        } else {
            GenOrmHelper.saveOrmToExcel(model, outputFile, dump);
        }
        return 0;
    }
}
