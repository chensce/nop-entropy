package io.nop.ai.core.model._gen;

import io.nop.commons.collections.KeyedList; //NOPMD NOSONAR - suppressed UnusedImports - Used for List Prop
import io.nop.core.lang.json.IJsonHandler;
import io.nop.ai.core.model.PromptOutputModel;
import io.nop.commons.util.ClassHelper;



// tell cpd to start ignoring code - CPD-OFF
/**
 * generate from /nop/schema/ai/prompt.xdef <p>
 * 解析响应消息，得到结果变量保存到AiResultMessage上
 */
@SuppressWarnings({"PMD.UselessOverridingMethod","PMD.UnusedLocalVariable",
    "PMD.UnnecessaryFullyQualifiedName","PMD.EmptyControlStatement","java:S116","java:S101","java:S1128","java:S1161"})
public abstract class _PromptOutputModel extends io.nop.core.resource.component.AbstractComponentModel {
    
    /**
     *  
     * xml name: codeLang
     * 
     */
    private java.lang.String _codeLang ;
    
    /**
     *  
     * xml name: defaultExpr
     * 当不满足解析条件时，执行defaultExpr返回缺省值
     */
    private io.nop.core.lang.eval.IEvalFunction _defaultExpr ;
    
    /**
     *  
     * xml name: description
     * 
     */
    private java.lang.String _description ;
    
    /**
     *  
     * xml name: displayName
     * 
     */
    private java.lang.String _displayName ;
    
    /**
     *  
     * xml name: format
     * 如果是xml，则尝试从content中解析得到XML节点。如果是json，则尝试解析得到json数据。解析中会自动忽略一些无关的输出信息。
     */
    private io.nop.ai.core.model.PromptOutputFormat _format ;
    
    /**
     *  
     * xml name: mandatory
     * 
     */
    private boolean _mandatory  = false;
    
    /**
     *  
     * xml name: markdownPath
     * 
     */
    private java.lang.String _markdownPath ;
    
    /**
     *  
     * xml name: name
     * 
     */
    private java.lang.String _name ;
    
    /**
     *  
     * xml name: optional
     * 
     */
    private boolean _optional  = false;
    
    /**
     *  
     * xml name: outputBuilder
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _outputBuilder ;
    
    /**
     *  
     * xml name: parseAfterPostProcess
     * 是否在postProcess调用之前解析
     */
    private boolean _parseAfterPostProcess  = false;
    
    /**
     *  
     * xml name: parseFromResponse
     * 没有指定format的情况下才会使用parseFromResponse配置
     * 如果指定了source，则执行代码来解析变量。如果没有指定source，但是指定了startMarker和endMarker，则从响应消息中截取相关信息。
     * 如果以上配置都没有，但是配置了contains，则只要响应消息中包含此字符串，就设置为true。
     */
    private io.nop.ai.core.model.PromptOutputParseModel _parseFromResponse ;
    
    /**
     *  
     * xml name: schema
     * schema包含如下几种情况：1. 简单数据类型 2. Map（命名属性集合） 3. List（顺序结构，重复结构） 4. Union（switch选择结构）
     * Map对应props配置,  List对应item配置, Union对应oneOf配置
     */
    private io.nop.xlang.xmeta.ISchema _schema ;
    
    /**
     *  
     * xml name: skipWhenResponseInvalid
     * 当AichatExchange为invalid状态时，跳过此输出变量的解析
     */
    private boolean _skipWhenResponseInvalid  = false;
    
    /**
     *  
     * xml name: type
     * 
     */
    private io.nop.core.type.IGenericType _type ;
    
    /**
     *  
     * xml name: valueNormalizer
     * 对解析得到的value进行后处理。处理之后再执行XDef元模型验证
     */
    private io.nop.core.lang.eval.IEvalFunction _valueNormalizer ;
    
    /**
     *  
     * xml name: when
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _when ;
    
    /**
     *  
     * xml name: xdefPath
     * 
     */
    private java.lang.String _xdefPath ;
    
    /**
     * 
     * xml name: codeLang
     *  
     */
    
    public java.lang.String getCodeLang(){
      return _codeLang;
    }

    
    public void setCodeLang(java.lang.String value){
        checkAllowChange();
        
        this._codeLang = value;
           
    }

    
    /**
     * 
     * xml name: defaultExpr
     *  当不满足解析条件时，执行defaultExpr返回缺省值
     */
    
    public io.nop.core.lang.eval.IEvalFunction getDefaultExpr(){
      return _defaultExpr;
    }

    
    public void setDefaultExpr(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._defaultExpr = value;
           
    }

    
    /**
     * 
     * xml name: description
     *  
     */
    
    public java.lang.String getDescription(){
      return _description;
    }

    
    public void setDescription(java.lang.String value){
        checkAllowChange();
        
        this._description = value;
           
    }

    
    /**
     * 
     * xml name: displayName
     *  
     */
    
    public java.lang.String getDisplayName(){
      return _displayName;
    }

    
    public void setDisplayName(java.lang.String value){
        checkAllowChange();
        
        this._displayName = value;
           
    }

    
    /**
     * 
     * xml name: format
     *  如果是xml，则尝试从content中解析得到XML节点。如果是json，则尝试解析得到json数据。解析中会自动忽略一些无关的输出信息。
     */
    
    public io.nop.ai.core.model.PromptOutputFormat getFormat(){
      return _format;
    }

    
    public void setFormat(io.nop.ai.core.model.PromptOutputFormat value){
        checkAllowChange();
        
        this._format = value;
           
    }

    
    /**
     * 
     * xml name: mandatory
     *  
     */
    
    public boolean isMandatory(){
      return _mandatory;
    }

    
    public void setMandatory(boolean value){
        checkAllowChange();
        
        this._mandatory = value;
           
    }

    
    /**
     * 
     * xml name: markdownPath
     *  
     */
    
    public java.lang.String getMarkdownPath(){
      return _markdownPath;
    }

    
    public void setMarkdownPath(java.lang.String value){
        checkAllowChange();
        
        this._markdownPath = value;
           
    }

    
    /**
     * 
     * xml name: name
     *  
     */
    
    public java.lang.String getName(){
      return _name;
    }

    
    public void setName(java.lang.String value){
        checkAllowChange();
        
        this._name = value;
           
    }

    
    /**
     * 
     * xml name: optional
     *  
     */
    
    public boolean isOptional(){
      return _optional;
    }

    
    public void setOptional(boolean value){
        checkAllowChange();
        
        this._optional = value;
           
    }

    
    /**
     * 
     * xml name: outputBuilder
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getOutputBuilder(){
      return _outputBuilder;
    }

    
    public void setOutputBuilder(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._outputBuilder = value;
           
    }

    
    /**
     * 
     * xml name: parseAfterPostProcess
     *  是否在postProcess调用之前解析
     */
    
    public boolean isParseAfterPostProcess(){
      return _parseAfterPostProcess;
    }

    
    public void setParseAfterPostProcess(boolean value){
        checkAllowChange();
        
        this._parseAfterPostProcess = value;
           
    }

    
    /**
     * 
     * xml name: parseFromResponse
     *  没有指定format的情况下才会使用parseFromResponse配置
     * 如果指定了source，则执行代码来解析变量。如果没有指定source，但是指定了startMarker和endMarker，则从响应消息中截取相关信息。
     * 如果以上配置都没有，但是配置了contains，则只要响应消息中包含此字符串，就设置为true。
     */
    
    public io.nop.ai.core.model.PromptOutputParseModel getParseFromResponse(){
      return _parseFromResponse;
    }

    
    public void setParseFromResponse(io.nop.ai.core.model.PromptOutputParseModel value){
        checkAllowChange();
        
        this._parseFromResponse = value;
           
    }

    
    /**
     * 
     * xml name: schema
     *  schema包含如下几种情况：1. 简单数据类型 2. Map（命名属性集合） 3. List（顺序结构，重复结构） 4. Union（switch选择结构）
     * Map对应props配置,  List对应item配置, Union对应oneOf配置
     */
    
    public io.nop.xlang.xmeta.ISchema getSchema(){
      return _schema;
    }

    
    public void setSchema(io.nop.xlang.xmeta.ISchema value){
        checkAllowChange();
        
        this._schema = value;
           
    }

    
    /**
     * 
     * xml name: skipWhenResponseInvalid
     *  当AichatExchange为invalid状态时，跳过此输出变量的解析
     */
    
    public boolean isSkipWhenResponseInvalid(){
      return _skipWhenResponseInvalid;
    }

    
    public void setSkipWhenResponseInvalid(boolean value){
        checkAllowChange();
        
        this._skipWhenResponseInvalid = value;
           
    }

    
    /**
     * 
     * xml name: type
     *  
     */
    
    public io.nop.core.type.IGenericType getType(){
      return _type;
    }

    
    public void setType(io.nop.core.type.IGenericType value){
        checkAllowChange();
        
        this._type = value;
           
    }

    
    /**
     * 
     * xml name: valueNormalizer
     *  对解析得到的value进行后处理。处理之后再执行XDef元模型验证
     */
    
    public io.nop.core.lang.eval.IEvalFunction getValueNormalizer(){
      return _valueNormalizer;
    }

    
    public void setValueNormalizer(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._valueNormalizer = value;
           
    }

    
    /**
     * 
     * xml name: when
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getWhen(){
      return _when;
    }

    
    public void setWhen(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._when = value;
           
    }

    
    /**
     * 
     * xml name: xdefPath
     *  
     */
    
    public java.lang.String getXdefPath(){
      return _xdefPath;
    }

    
    public void setXdefPath(java.lang.String value){
        checkAllowChange();
        
        this._xdefPath = value;
           
    }

    

    @Override
    public void freeze(boolean cascade){
        if(frozen()) return;
        super.freeze(cascade);

        if(cascade){ //NOPMD - suppressed EmptyControlStatement - Auto Gen Code
        
           this._parseFromResponse = io.nop.api.core.util.FreezeHelper.deepFreeze(this._parseFromResponse);
            
           this._schema = io.nop.api.core.util.FreezeHelper.deepFreeze(this._schema);
            
        }
    }

    @Override
    protected void outputJson(IJsonHandler out){
        super.outputJson(out);
        
        out.putNotNull("codeLang",this.getCodeLang());
        out.putNotNull("defaultExpr",this.getDefaultExpr());
        out.putNotNull("description",this.getDescription());
        out.putNotNull("displayName",this.getDisplayName());
        out.putNotNull("format",this.getFormat());
        out.putNotNull("mandatory",this.isMandatory());
        out.putNotNull("markdownPath",this.getMarkdownPath());
        out.putNotNull("name",this.getName());
        out.putNotNull("optional",this.isOptional());
        out.putNotNull("outputBuilder",this.getOutputBuilder());
        out.putNotNull("parseAfterPostProcess",this.isParseAfterPostProcess());
        out.putNotNull("parseFromResponse",this.getParseFromResponse());
        out.putNotNull("schema",this.getSchema());
        out.putNotNull("skipWhenResponseInvalid",this.isSkipWhenResponseInvalid());
        out.putNotNull("type",this.getType());
        out.putNotNull("valueNormalizer",this.getValueNormalizer());
        out.putNotNull("when",this.getWhen());
        out.putNotNull("xdefPath",this.getXdefPath());
    }

    public PromptOutputModel cloneInstance(){
        PromptOutputModel instance = newInstance();
        this.copyTo(instance);
        return instance;
    }

    protected void copyTo(PromptOutputModel instance){
        super.copyTo(instance);
        
        instance.setCodeLang(this.getCodeLang());
        instance.setDefaultExpr(this.getDefaultExpr());
        instance.setDescription(this.getDescription());
        instance.setDisplayName(this.getDisplayName());
        instance.setFormat(this.getFormat());
        instance.setMandatory(this.isMandatory());
        instance.setMarkdownPath(this.getMarkdownPath());
        instance.setName(this.getName());
        instance.setOptional(this.isOptional());
        instance.setOutputBuilder(this.getOutputBuilder());
        instance.setParseAfterPostProcess(this.isParseAfterPostProcess());
        instance.setParseFromResponse(this.getParseFromResponse());
        instance.setSchema(this.getSchema());
        instance.setSkipWhenResponseInvalid(this.isSkipWhenResponseInvalid());
        instance.setType(this.getType());
        instance.setValueNormalizer(this.getValueNormalizer());
        instance.setWhen(this.getWhen());
        instance.setXdefPath(this.getXdefPath());
    }

    protected PromptOutputModel newInstance(){
        return (PromptOutputModel) ClassHelper.newInstance(getClass());
    }
}
 // resume CPD analysis - CPD-ON
