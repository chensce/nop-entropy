/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.xlang.xdef;

import io.nop.api.core.json.IJsonString;
import io.nop.commons.type.StdDataType;
import io.nop.commons.util.StringHelper;
import io.nop.core.lang.json.JsonTool;
import io.nop.xlang.xdef.domain.IStdDomainRegistry;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_ATTR_PREFIX;
import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_PREFIX_CP_EXPR;
import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_PREFIX_DEPRECATED;
import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_PREFIX_INTERNAL;
import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_PREFIX_MANDATORY;
import static io.nop.xlang.xdef.XDefConstants.XDEF_TYPE_PREFIX_OPTIONS;

public class XDefTypeDecl implements Serializable, IJsonString {
    private final boolean deprecated;
    private final boolean mandatory;
    private final boolean internal;
    private final boolean allowCpExpr;
    private final String stdDomain;
    private final String domain;
    private final String options;

    /**
     * 从其他属性获取到值作为本属性的缺省值
     */
    private final List<String> defaultAttrNames;

    /**
     * 直接指定缺省值
     */
    private final Object defaultValue;

    public XDefTypeDecl(boolean deprecated, boolean internal, boolean mandatory, boolean allowCpExpr, String stdDomain,
                        String domain, String options, List<String> defaultAttrNames,
                        Object defaultValue) {
        this.deprecated = deprecated;
        this.mandatory = mandatory;
        this.internal = internal;
        this.allowCpExpr = allowCpExpr;
        this.stdDomain = stdDomain;
        this.domain = domain;
        this.options = options;
        this.defaultAttrNames = defaultAttrNames;
        this.defaultValue = defaultValue;
    }

    /**
     * 继承属性时只能修改deprecated/mandatory/domain部分
     */
    public boolean isCompatibleWith(XDefTypeDecl decl) {
        if (!stdDomain.equals(decl.stdDomain))
            return false;
        if (!Objects.equals(options, decl.options))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (deprecated)
            sb.append(XDEF_TYPE_PREFIX_DEPRECATED);
        if (internal)
            sb.append(XDEF_TYPE_PREFIX_INTERNAL);
        if (mandatory) {
            sb.append(XDEF_TYPE_PREFIX_MANDATORY);
        }
        if (allowCpExpr)
            sb.append(XDEF_TYPE_PREFIX_CP_EXPR);

        sb.append(stdDomain);
        if (domain != null) {
            sb.append('[').append(domain).append(']');
        }

        if (options != null)
            sb.append(XDEF_TYPE_PREFIX_OPTIONS).append(options);

        if (defaultAttrNames != null) {
            sb.append('=').append(XDEF_TYPE_ATTR_PREFIX);
            sb.append(StringHelper.join(defaultAttrNames, ","));
        }

        if (defaultValue != null) {
            sb.append('=').append(getDefaultValueAsString());
        }
        return sb.toString();
    }

    private String getDefaultValueAsString() {
        if (defaultValue == null)
            return null;

        if (!(defaultValue instanceof String))
            return JsonTool.instance().stringify(defaultValue);

        return StringHelper.quoteIfNecessary(defaultValue.toString());
    }

    public List<String> getDefaultAttrNames() {
        return defaultAttrNames;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isAllowCpExpr() {
        return allowCpExpr;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getStdDomain() {
        return stdDomain;
    }

    public String getDomain() {
        return domain;
    }

    public String getOptions() {
        return options;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isSupportBody(IStdDomainRegistry stdDomainRegistry) {
        IStdDomainHandler handler = stdDomainRegistry.getStdDomainHandler(stdDomain);
        return handler != null && handler.supportXmlChild();
    }

    public StdDataType getStdDataType(IStdDomainRegistry stdDomainRegistry) {
        IStdDomainHandler handler = stdDomainRegistry.getStdDomainHandler(stdDomain);
        if (handler == null)
            return StdDataType.ANY;
        return handler.getGenericType(mandatory, options).getStdDataType();
    }
}