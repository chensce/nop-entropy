/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.xlang.xpl.tags;

import io.nop.api.core.exceptions.NopEvalException;
import io.nop.api.core.exceptions.NopException;
import io.nop.commons.util.StringHelper;
import io.nop.core.lang.eval.IEvalFunction;
import io.nop.core.lang.xml.XNode;
import io.nop.core.reflect.IFunctionArgument;
import io.nop.core.reflect.impl.FunctionArgument;
import io.nop.core.reflect.impl.FunctionModel;
import io.nop.core.type.IGenericType;
import io.nop.xlang.api.IXLangCompileScope;
import io.nop.xlang.ast.CallExpression;
import io.nop.xlang.ast.Expression;
import io.nop.xlang.ast.Identifier;
import io.nop.xlang.ast.Literal;
import io.nop.xlang.ast.Program;
import io.nop.xlang.ast.definition.ResolvedFuncDefinition;
import io.nop.xlang.xdsl.XDslParseHelper;
import io.nop.xlang.xpl.IXplCompiler;
import io.nop.xlang.xpl.IXplTagCompiler;
import io.nop.xlang.xpl.XplConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.nop.xlang.XLangErrors.ARG_NODE;
import static io.nop.xlang.XLangErrors.ERR_XPL_SCRIPT_NOT_ALLOW_CHILD;
import static io.nop.xlang.xpl.XplConstants.ARGS_NAME;
import static io.nop.xlang.xpl.XplConstants.LANG_NAME;
import static io.nop.xlang.xpl.XplConstants.RETURN_TYPE_NAME;
import static io.nop.xlang.xpl.utils.XplParseHelper.checkArgNames;
import static io.nop.xlang.xpl.utils.XplParseHelper.getAttrLiteral;

public class ScriptTagCompiler implements IXplTagCompiler {
    public static ScriptTagCompiler INSTANCE = new ScriptTagCompiler();

    static final List<String> ATTR_NAMES = Arrays.asList(LANG_NAME, ARGS_NAME, RETURN_TYPE_NAME);

    @Override
    public Expression parseTag(XNode node, IXplCompiler cp, IXLangCompileScope scope) {
        checkArgNames(node, ATTR_NAMES);

        Literal literal = getAttrLiteral(node, LANG_NAME, cp, scope);
        if (node.hasChild())
            throw new NopEvalException(ERR_XPL_SCRIPT_NOT_ALLOW_CHILD).param(ARG_NODE, node);

        String source = node.contentText();
        String lang = literal == null ? null : literal.getStringValue();
        if (StringHelper.isEmpty(lang) || lang.equals(XplConstants.LANG_XPL)) {
            try {
                Program prog = cp.parseFullExpr(node.content().getLocation(), source, scope);
                if (prog == null)
                    return null;
                prog.setShareScope(true);
                return prog;
            } catch (NopException e) {
                e.addXplStack(node);
                throw e;
            }
        }

        IGenericType genericType = XDslParseHelper.parseAttrGenericType(node, RETURN_TYPE_NAME, cp.getRawTypeResolver());

        List<FunctionArgument> args = XDslParseHelper.parseAttrArgs(node, ARGS_NAME, cp.getRawTypeResolver());
        if (args == null)
            args = Collections.emptyList();

        IEvalFunction func = cp.compileScript(node.content().getLocation(), lang, source,
                args, genericType, scope);
        if (func == null)
            return null;

        CallExpression expr = new CallExpression();
        expr.setLocation(node.getLocation());

        Identifier id = Identifier.valueOf(node.getLocation(), "<c:script>");
        FunctionModel funcModel = new FunctionModel();
        funcModel.setName("<c:script>");
        funcModel.setLocation(node.getLocation());
        funcModel.setInvoker(func);
        funcModel.setArgs(args);

        ResolvedFuncDefinition def = new ResolvedFuncDefinition(funcModel);
        id.setResolvedDefinition(def);
        expr.setCallee(id);


        List<Expression> argExprs = new ArrayList<>(args.size());
        for (IFunctionArgument arg : args) {
            argExprs.add(Identifier.valueOf(node.getLocation(), arg.getName()));
        }
        expr.setArguments(argExprs);

        return expr;
    }
}