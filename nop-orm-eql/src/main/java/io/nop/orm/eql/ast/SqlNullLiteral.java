/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.orm.eql.ast;

import io.nop.commons.type.StdSqlType;
import io.nop.orm.eql.ast._gen._SqlNullLiteral;

public class SqlNullLiteral extends _SqlNullLiteral {
    @Override
    public StdSqlType getSqlType() {
        return StdSqlType.ANY;
    }

    @Override
    public Object getLiteralValue() {
        return null;
    }
}
