/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.orm.eql;

import io.nop.api.core.exceptions.NopException;
import io.nop.api.core.json.JSON;
import io.nop.core.lang.sql.SQL;
import io.nop.dao.dialect.DialectManager;
import io.nop.dao.dialect.IDialect;
import io.nop.dao.dialect.function.ISQLFunction;
import io.nop.orm.AbstractOrmTestCase;
import io.nop.orm.eql.ast.SqlProgram;
import io.nop.orm.eql.compile.ISqlCompileContext;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.nop.orm.eql.OrmEqlErrors.ERR_EQL_UNKNOWN_ENTITY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestEqlCompiler extends AbstractOrmTestCase {

    @Test
    public void testCountStar() {
        SQL sql = SQL.begin().sql("select count(*) from SimsClass").end();
        long value = orm().findLong(sql, 0L);
        assertTrue(value > 0);
    }

    SQL compile(String sql) {
        ICompiledSql compiledSql = orm().getSessionFactory().compileSql("test", sql, true,
                null, true, false, true);
        return compiledSql.getSql();
    }

    List<Object> getParams(String sql) {
        ICompiledSql compiledSql = orm().getSessionFactory().compileSql("test", sql, true);
        return compiledSql.buildParams(Collections.emptyList());
    }

    @Test
    public void testEntityInLogicExpr() {
        String sqlText = "select o, (select count(*) from SimsCollege c where o.simsCollege = c) from SimsClass o";
        SQL sql = compile(sqlText);
        sql.dump();

        orm().findAll(SQL.begin().sql(sqlText).end());
    }

    @Test
    public void testAstTransform() {
        SQL sql = SQL.begin().allowUnderscoreName().sql("select o.class_id, o.studentNumber from sims_class o").end();

        IEqlAstTransformer astTransformer = new TestTransformer();
        ICompiledSql compiled = orm().getSessionFactory().compileSql("test", sql.getText(), false, astTransformer,
                false, true, false);
        System.out.println(compiled.getSql().getFormattedText());

        List<Map<String, Object>> list = orm().findAll(sql);
        System.out.println(JSON.serialize(list, true));

        Map<String, Object> item = list.get(0);
        assertTrue(item.containsKey("class_id"));
        assertTrue(item.containsKey("studentNumber"));
    }

    static class TestTransformer implements IEqlAstTransformer {
        @Override
        public void transformBeforeAnalyze(SqlProgram ast, String name, String sql, ISqlCompileContext context) {

        }

        @Override
        public void transformAfterAnalyze(SqlProgram ast, String name, String sql, ISqlCompileContext context) {

        }
    }

    @Test
    public void testWith() {
        String sqlText = "with MyClass as (select o.simsCollege, count(o.simsCollege) count " +
                "from SimsClass o group by o.simsCollege) select o.count from MyClass o";
        SQL sql = compile(sqlText);
        sql.dump();

        List<Object> params = getParams(sqlText);
        assertEquals("[123]", params.toString());
    }


    @Test
    public void testWith2() {
        try {
            String sqlText = "with MyClass as (select o.simsCollege, count(o.simsCollege) count " +
                    "from SimsClass o group by o.simsCollege) select o from MyClass o";
            SQL sql = compile(sqlText);
            sql.dump();
            assertTrue(false);
        } catch (NopException e) {
            assertEquals(OrmEqlErrors.ERR_EQL_ONLY_SUPPORT_SINGLE_TABLE_SOURCE.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void testWith3() {
        String sqlText = "with ClassCount as(\n" +
                "select t.collegeId,count(1) as classCount from SimsClass t group by t.collegeId)\n" +
                "select o,coalesce(itc.classCount,2) from SimsCollege o\n" +
                "left join ClassCount itc on o.id = itc.collegeId";

        orm().findAll(SQL.begin().sql(sqlText).end());
    }

    @Test
    public void testWith4() {
        String sqlText = "with ClassCount as(\n" +
                "select t.collegeId,count(1) as classCount from SimsClass t group by t.collegeId),\n" +
                "AvgCount as (select count(1) from ClassCount u)" +
                "select o,coalesce(itc.classCount,2) from SimsCollege o\n" +
                "left join ClassCount itc on o.id = itc.collegeId";

        orm().findAll(SQL.begin().sql(sqlText).end());
    }

    @Test
    public void testLowerFunc() {
        String sqlText = "select o from SimsClass o where lower(o.className) like 'a'";
        SQL sql = compile(sqlText);
        sql.dump();
    }

    @Test
    public void testAliasInOrderBy() {
        String sqlText = "select o,o.classId as cid from SimsClass o order by cid desc";
        SQL sql = compile(sqlText);
        sql.dump();
        assertTrue(sql.getText().contains("cid desc"));
    }

    @Test
    public void testExcept() {
        String sqlText = "select o.classId as cid from SimsClass o except select u.collegeId from SimsCollege u";
        SQL sql = compile(sqlText);
        sql.dump();
        assertTrue(sql.getText().contains("except"));

        jdbc().findAll(sql);
    }


    @Test
    public void testIntersect() {
        String sqlText = "select o.classId as cid from SimsClass o intersect select u.collegeId from SimsCollege u";
        SQL sql = compile(sqlText);
        sql.dump();
        assertTrue(sql.getText().contains("intersect"));

        jdbc().findAll(sql);
    }

    @Test
    public void testSome() {
        String sqlText = "select o.classId as cid from SimsClass o where o.classId > some(select u.collegeId from SimsCollege u)";
        SQL sql = compile(sqlText);
        sql.dump();
        assertTrue(sql.getText().contains("ANY"));

        jdbc().findAll(sql);
    }

    @Test
    public void testWindowExpr() {
        String sqlText = "SELECT o.classId,\n" +
                "       RANK() OVER (PARTITION BY o.classId ORDER BY o.classId) as rank\n" +
                "FROM SimsClass o";
        SQL sql = compile(sqlText);
        sql.dump();

        jdbc().findAll(sql);
    }

    @Test
    public void testUserKeyword() {
        String sqlText = "select o.user.name from NopAuthUser o";
        try {
            compile(sqlText);
            fail();
        } catch (NopException e) {
            assertEquals(ERR_EQL_UNKNOWN_ENTITY_NAME.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void testTimestampKeyword(){
        String sqlText = "select o.timestamp from NopAuthUser o";
        try {
            compile(sqlText);
            fail();
        } catch (NopException e) {
            assertEquals(ERR_EQL_UNKNOWN_ENTITY_NAME.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void testUpdateReturning() {
        String sqlText = "update SimsClass o set o.collegeId = 1 returning *";

        SQL sql = compile(sqlText);
        sql.dump();
    }

    @Test
    public void testFunctions() {
        IDialect dialect = DialectManager.instance().getDialect("h2");
        for (String funcName : dialect.getFunctionNames()) {
            ISQLFunction func = dialect.getFunction(funcName);
            String sqlText = dialect.getSelectFromDualSql(getFuncSql(func));
            try {
                compile(sqlText);
            } catch (NopException e) {
                e.addXplStack(sqlText);
                throw e;
            }
        }
    }

    String getFuncSql(ISQLFunction func) {
        StringBuilder sb = new StringBuilder();
        sb.append(func.getName());
        sb.append('(');
        for (int i = 0, n = func.getMinArgCount(); i < n; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append("NULL");
        }
        sb.append(')');

        if (func.isOnlyForWindowExpr()) {
            sb.append(" OVER (PARTITION BY 1 ORDER BY 1) \n");
        }
        return sb.toString();
    }

    @Test
    public void testAndOrSql(){
        String sql = "select o from SimsClass o where (o.collegeId = 1 and o.classId = 2) or (o.collegeId = 3 and o.classId = 4) or (o.collegeId = 5 and o.classId = 6) or (o.collegeId = 15 and o.classId = 16)";
        SQL sqlObj = compile(sql);
        sqlObj.dump();
    }
}
