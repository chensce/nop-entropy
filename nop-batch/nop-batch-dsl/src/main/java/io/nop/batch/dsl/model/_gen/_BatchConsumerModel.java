package io.nop.batch.dsl.model._gen;

import io.nop.commons.collections.KeyedList; //NOPMD NOSONAR - suppressed UnusedImports - Used for List Prop
import io.nop.core.lang.json.IJsonHandler;
import io.nop.batch.dsl.model.BatchConsumerModel;
import io.nop.commons.util.ClassHelper;



// tell cpd to start ignoring code - CPD-OFF
/**
 * generate from /nop/schema/task/batch.xdef <p>
 * 
 */
@SuppressWarnings({"PMD.UselessOverridingMethod","PMD.UnusedLocalVariable",
    "PMD.UnnecessaryFullyQualifiedName","PMD.EmptyControlStatement","java:S116","java:S101","java:S1128","java:S1161"})
public abstract class _BatchConsumerModel extends io.nop.batch.dsl.model.BatchListenersModel {
    
    /**
     *  
     * xml name: adapter
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _adapter ;
    
    /**
     *  
     * xml name: aggregator
     * 
     */
    private java.lang.String _aggregator ;
    
    /**
     *  
     * xml name: bean
     * 
     */
    private java.lang.String _bean ;
    
    /**
     *  
     * xml name: excel-writer
     * 
     */
    private io.nop.batch.dsl.model.BatchExcelWriterModel _excelWriter ;
    
    /**
     *  
     * xml name: file-writer
     * 
     */
    private io.nop.batch.dsl.model.BatchFileWriterModel _fileWriter ;
    
    /**
     *  
     * xml name: filter
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _filter ;
    
    /**
     *  
     * xml name: forTag
     * 用于匹配tagger所返回的标签。如果没有设置，则表示不受tagger匹配影响，总是消费item
     */
    private java.lang.String _forTag ;
    
    /**
     *  
     * xml name: jdbc-writer
     * 
     */
    private io.nop.batch.dsl.model.BatchJdbcWriterModel _jdbcWriter ;
    
    /**
     *  
     * xml name: metaProvider
     * 
     */
    private java.lang.String _metaProvider ;
    
    /**
     *  
     * xml name: name
     * 
     */
    private java.lang.String _name ;
    
    /**
     *  
     * xml name: order
     * 
     */
    private int _order  = 0;
    
    /**
     *  
     * xml name: orm-writer
     * 
     */
    private io.nop.batch.dsl.model.BatchOrmWriterModel _ormWriter ;
    
    /**
     *  
     * xml name: provider
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _provider ;
    
    /**
     *  
     * xml name: source
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _source ;
    
    /**
     *  
     * xml name: transformer
     * 实际写入之前执行transformer进行结构转换。实际保存的是返回的结果对象。如果transformer的返回值是null，则忽略这个条目
     */
    private io.nop.core.lang.eval.IEvalFunction _transformer ;
    
    /**
     * 
     * xml name: adapter
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getAdapter(){
      return _adapter;
    }

    
    public void setAdapter(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._adapter = value;
           
    }

    
    /**
     * 
     * xml name: aggregator
     *  
     */
    
    public java.lang.String getAggregator(){
      return _aggregator;
    }

    
    public void setAggregator(java.lang.String value){
        checkAllowChange();
        
        this._aggregator = value;
           
    }

    
    /**
     * 
     * xml name: bean
     *  
     */
    
    public java.lang.String getBean(){
      return _bean;
    }

    
    public void setBean(java.lang.String value){
        checkAllowChange();
        
        this._bean = value;
           
    }

    
    /**
     * 
     * xml name: excel-writer
     *  
     */
    
    public io.nop.batch.dsl.model.BatchExcelWriterModel getExcelWriter(){
      return _excelWriter;
    }

    
    public void setExcelWriter(io.nop.batch.dsl.model.BatchExcelWriterModel value){
        checkAllowChange();
        
        this._excelWriter = value;
           
    }

    
    /**
     * 
     * xml name: file-writer
     *  
     */
    
    public io.nop.batch.dsl.model.BatchFileWriterModel getFileWriter(){
      return _fileWriter;
    }

    
    public void setFileWriter(io.nop.batch.dsl.model.BatchFileWriterModel value){
        checkAllowChange();
        
        this._fileWriter = value;
           
    }

    
    /**
     * 
     * xml name: filter
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getFilter(){
      return _filter;
    }

    
    public void setFilter(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._filter = value;
           
    }

    
    /**
     * 
     * xml name: forTag
     *  用于匹配tagger所返回的标签。如果没有设置，则表示不受tagger匹配影响，总是消费item
     */
    
    public java.lang.String getForTag(){
      return _forTag;
    }

    
    public void setForTag(java.lang.String value){
        checkAllowChange();
        
        this._forTag = value;
           
    }

    
    /**
     * 
     * xml name: jdbc-writer
     *  
     */
    
    public io.nop.batch.dsl.model.BatchJdbcWriterModel getJdbcWriter(){
      return _jdbcWriter;
    }

    
    public void setJdbcWriter(io.nop.batch.dsl.model.BatchJdbcWriterModel value){
        checkAllowChange();
        
        this._jdbcWriter = value;
           
    }

    
    /**
     * 
     * xml name: metaProvider
     *  
     */
    
    public java.lang.String getMetaProvider(){
      return _metaProvider;
    }

    
    public void setMetaProvider(java.lang.String value){
        checkAllowChange();
        
        this._metaProvider = value;
           
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
     * xml name: order
     *  
     */
    
    public int getOrder(){
      return _order;
    }

    
    public void setOrder(int value){
        checkAllowChange();
        
        this._order = value;
           
    }

    
    /**
     * 
     * xml name: orm-writer
     *  
     */
    
    public io.nop.batch.dsl.model.BatchOrmWriterModel getOrmWriter(){
      return _ormWriter;
    }

    
    public void setOrmWriter(io.nop.batch.dsl.model.BatchOrmWriterModel value){
        checkAllowChange();
        
        this._ormWriter = value;
           
    }

    
    /**
     * 
     * xml name: provider
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getProvider(){
      return _provider;
    }

    
    public void setProvider(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._provider = value;
           
    }

    
    /**
     * 
     * xml name: source
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getSource(){
      return _source;
    }

    
    public void setSource(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._source = value;
           
    }

    
    /**
     * 
     * xml name: transformer
     *  实际写入之前执行transformer进行结构转换。实际保存的是返回的结果对象。如果transformer的返回值是null，则忽略这个条目
     */
    
    public io.nop.core.lang.eval.IEvalFunction getTransformer(){
      return _transformer;
    }

    
    public void setTransformer(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._transformer = value;
           
    }

    

    @Override
    public void freeze(boolean cascade){
        if(frozen()) return;
        super.freeze(cascade);

        if(cascade){ //NOPMD - suppressed EmptyControlStatement - Auto Gen Code
        
           this._excelWriter = io.nop.api.core.util.FreezeHelper.deepFreeze(this._excelWriter);
            
           this._fileWriter = io.nop.api.core.util.FreezeHelper.deepFreeze(this._fileWriter);
            
           this._jdbcWriter = io.nop.api.core.util.FreezeHelper.deepFreeze(this._jdbcWriter);
            
           this._ormWriter = io.nop.api.core.util.FreezeHelper.deepFreeze(this._ormWriter);
            
        }
    }

    @Override
    protected void outputJson(IJsonHandler out){
        super.outputJson(out);
        
        out.putNotNull("adapter",this.getAdapter());
        out.putNotNull("aggregator",this.getAggregator());
        out.putNotNull("bean",this.getBean());
        out.putNotNull("excelWriter",this.getExcelWriter());
        out.putNotNull("fileWriter",this.getFileWriter());
        out.putNotNull("filter",this.getFilter());
        out.putNotNull("forTag",this.getForTag());
        out.putNotNull("jdbcWriter",this.getJdbcWriter());
        out.putNotNull("metaProvider",this.getMetaProvider());
        out.putNotNull("name",this.getName());
        out.putNotNull("order",this.getOrder());
        out.putNotNull("ormWriter",this.getOrmWriter());
        out.putNotNull("provider",this.getProvider());
        out.putNotNull("source",this.getSource());
        out.putNotNull("transformer",this.getTransformer());
    }

    public BatchConsumerModel cloneInstance(){
        BatchConsumerModel instance = newInstance();
        this.copyTo(instance);
        return instance;
    }

    protected void copyTo(BatchConsumerModel instance){
        super.copyTo(instance);
        
        instance.setAdapter(this.getAdapter());
        instance.setAggregator(this.getAggregator());
        instance.setBean(this.getBean());
        instance.setExcelWriter(this.getExcelWriter());
        instance.setFileWriter(this.getFileWriter());
        instance.setFilter(this.getFilter());
        instance.setForTag(this.getForTag());
        instance.setJdbcWriter(this.getJdbcWriter());
        instance.setMetaProvider(this.getMetaProvider());
        instance.setName(this.getName());
        instance.setOrder(this.getOrder());
        instance.setOrmWriter(this.getOrmWriter());
        instance.setProvider(this.getProvider());
        instance.setSource(this.getSource());
        instance.setTransformer(this.getTransformer());
    }

    protected BatchConsumerModel newInstance(){
        return (BatchConsumerModel) ClassHelper.newInstance(getClass());
    }
}
 // resume CPD analysis - CPD-ON
