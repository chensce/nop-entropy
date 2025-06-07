package io.nop.orm.sql_lib._gen;

import io.nop.commons.collections.KeyedList; //NOPMD NOSONAR - suppressed UnusedImports - Used for List Prop
import io.nop.core.lang.json.IJsonHandler;
import io.nop.orm.sql_lib.SqlItemModel;
import io.nop.commons.util.ClassHelper;



// tell cpd to start ignoring code - CPD-OFF
/**
 * generate from /nop/schema/orm/sql-lib.xdef <p>
 * 
 */
@SuppressWarnings({"PMD.UselessOverridingMethod","PMD.UnusedLocalVariable",
    "PMD.UnnecessaryFullyQualifiedName","PMD.EmptyControlStatement","java:S116","java:S101","java:S1128","java:S1161"})
public abstract class _SqlItemModel extends io.nop.core.resource.component.AbstractComponentModel {
    
    /**
     *  
     * xml name: arg
     * 
     */
    private KeyedList<io.nop.orm.sql_lib.SqlItemArgModel> _args = KeyedList.emptyList();
    
    /**
     *  
     * xml name: auth
     * 
     */
    private io.nop.api.core.auth.ActionAuthMeta _auth ;
    
    /**
     *  
     * xml name: batchLoadSelection
     * 对应eql查询，获取到结果数据之后，会按照这里的配置自动批量加载结果对象上的关联属性
     */
    private io.nop.api.core.beans.FieldSelectionBean _batchLoadSelection ;
    
    /**
     *  
     * xml name: buildResult
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _buildResult ;
    
    /**
     *  
     * xml name: buildRowMapper
     * 
     */
    private io.nop.core.lang.eval.IEvalFunction _buildRowMapper ;
    
    /**
     *  
     * xml name: cacheKeyExpr
     * 生成缓存key的表达式, 运行时可以根据传入的参数构造出缓存key，例如cacheKeyExpr="concat(x,'-',y)"
     */
    private io.nop.core.lang.eval.IEvalAction _cacheKeyExpr ;
    
    /**
     *  
     * xml name: cacheName
     * 
     */
    private java.lang.String _cacheName ;
    
    /**
     *  
     * xml name: description
     * 
     */
    private java.lang.String _description ;
    
    /**
     *  
     * xml name: disableLogicalDelete
     * 
     */
    private boolean _disableLogicalDelete  = false;
    
    /**
     *  
     * xml name: disableSmartRowMapper
     * 禁用SmartRowMapper机制。缺省情况下，如果只返回一列数据，比如String类型，会自动转换为单列的List<String>。
     * 禁用SmartRowMapper之后会始终按照Map读取到行数据，然后再转型到rowType等。
     */
    private boolean _disableSmartRowMapper  = false;
    
    /**
     *  
     * xml name: displayName
     * 
     */
    private java.lang.String _displayName ;
    
    /**
     *  
     * xml name: fetchSize
     * 设置jdbc的fetchSize为指定值
     */
    private java.lang.Integer _fetchSize ;
    
    /**
     *  
     * xml name: fields
     * 为sql查询语句补充列的类型信息，便于从ResultSet中抽取指定类型的数据，避免返回数据库引擎内部的数据类型
     */
    private KeyedList<io.nop.orm.sql_lib.SqlFieldModel> _fields = KeyedList.emptyList();
    
    /**
     *  
     * xml name: name
     * 
     */
    private java.lang.String _name ;
    
    /**
     *  
     * xml name: ormEntityRefreshBehavior
     * 当SQL语句返回实体数据，并且使用rowType来构建实体对象时所使用的更新策略
     */
    private io.nop.orm.sql_lib.OrmEntityRefreshBehavior _ormEntityRefreshBehavior ;
    
    /**
     *  
     * xml name: querySpace
     * 指定查询空间，一般一个querySpace对应一个数据库。
     */
    private java.lang.String _querySpace  = "default";
    
    /**
     *  
     * xml name: rowType
     * 可以指定返回结果对应的包装类名称。会按照字段别名映射到对象的属性上，
     */
    private java.lang.String _rowType ;
    
    /**
     *  
     * xml name: sqlMethod
     * 指定使用ISqlExecutor接口上的什么方法去执行此sql，具体可选项在SqlMethod枚举类中定义，
     */
    private io.nop.orm.sql_lib.SqlMethod _sqlMethod ;
    
    /**
     *  
     * xml name: timeout
     * sql执行的超时时间。单位为毫秒
     */
    private java.lang.Integer _timeout ;
    
    /**
     *  
     * xml name: 
     * 
     */
    private java.lang.String _type ;
    
    /**
     *  
     * xml name: validate-input
     * 验证sql正确性时采用的输入数据。当AppConfig.isDebugMode()为true时（调试模式），sql-lib加载后会自动验证sql语句语法正确。
     */
    private io.nop.core.lang.eval.IEvalAction _validateInput ;
    
    /**
     * 
     * xml name: arg
     *  
     */
    
    public java.util.List<io.nop.orm.sql_lib.SqlItemArgModel> getArgs(){
      return _args;
    }

    
    public void setArgs(java.util.List<io.nop.orm.sql_lib.SqlItemArgModel> value){
        checkAllowChange();
        
        this._args = KeyedList.fromList(value, io.nop.orm.sql_lib.SqlItemArgModel::getName);
           
    }

    
    public io.nop.orm.sql_lib.SqlItemArgModel getArg(String name){
        return this._args.getByKey(name);
    }

    public boolean hasArg(String name){
        return this._args.containsKey(name);
    }

    public void addArg(io.nop.orm.sql_lib.SqlItemArgModel item) {
        checkAllowChange();
        java.util.List<io.nop.orm.sql_lib.SqlItemArgModel> list = this.getArgs();
        if (list == null || list.isEmpty()) {
            list = new KeyedList<>(io.nop.orm.sql_lib.SqlItemArgModel::getName);
            setArgs(list);
        }
        list.add(item);
    }
    
    public java.util.Set<String> keySet_args(){
        return this._args.keySet();
    }

    public boolean hasArgs(){
        return !this._args.isEmpty();
    }
    
    /**
     * 
     * xml name: auth
     *  
     */
    
    public io.nop.api.core.auth.ActionAuthMeta getAuth(){
      return _auth;
    }

    
    public void setAuth(io.nop.api.core.auth.ActionAuthMeta value){
        checkAllowChange();
        
        this._auth = value;
           
    }

    
    /**
     * 
     * xml name: batchLoadSelection
     *  对应eql查询，获取到结果数据之后，会按照这里的配置自动批量加载结果对象上的关联属性
     */
    
    public io.nop.api.core.beans.FieldSelectionBean getBatchLoadSelection(){
      return _batchLoadSelection;
    }

    
    public void setBatchLoadSelection(io.nop.api.core.beans.FieldSelectionBean value){
        checkAllowChange();
        
        this._batchLoadSelection = value;
           
    }

    
    /**
     * 
     * xml name: buildResult
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getBuildResult(){
      return _buildResult;
    }

    
    public void setBuildResult(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._buildResult = value;
           
    }

    
    /**
     * 
     * xml name: buildRowMapper
     *  
     */
    
    public io.nop.core.lang.eval.IEvalFunction getBuildRowMapper(){
      return _buildRowMapper;
    }

    
    public void setBuildRowMapper(io.nop.core.lang.eval.IEvalFunction value){
        checkAllowChange();
        
        this._buildRowMapper = value;
           
    }

    
    /**
     * 
     * xml name: cacheKeyExpr
     *  生成缓存key的表达式, 运行时可以根据传入的参数构造出缓存key，例如cacheKeyExpr="concat(x,'-',y)"
     */
    
    public io.nop.core.lang.eval.IEvalAction getCacheKeyExpr(){
      return _cacheKeyExpr;
    }

    
    public void setCacheKeyExpr(io.nop.core.lang.eval.IEvalAction value){
        checkAllowChange();
        
        this._cacheKeyExpr = value;
           
    }

    
    /**
     * 
     * xml name: cacheName
     *  
     */
    
    public java.lang.String getCacheName(){
      return _cacheName;
    }

    
    public void setCacheName(java.lang.String value){
        checkAllowChange();
        
        this._cacheName = value;
           
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
     * xml name: disableLogicalDelete
     *  
     */
    
    public boolean isDisableLogicalDelete(){
      return _disableLogicalDelete;
    }

    
    public void setDisableLogicalDelete(boolean value){
        checkAllowChange();
        
        this._disableLogicalDelete = value;
           
    }

    
    /**
     * 
     * xml name: disableSmartRowMapper
     *  禁用SmartRowMapper机制。缺省情况下，如果只返回一列数据，比如String类型，会自动转换为单列的List<String>。
     * 禁用SmartRowMapper之后会始终按照Map读取到行数据，然后再转型到rowType等。
     */
    
    public boolean isDisableSmartRowMapper(){
      return _disableSmartRowMapper;
    }

    
    public void setDisableSmartRowMapper(boolean value){
        checkAllowChange();
        
        this._disableSmartRowMapper = value;
           
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
     * xml name: fetchSize
     *  设置jdbc的fetchSize为指定值
     */
    
    public java.lang.Integer getFetchSize(){
      return _fetchSize;
    }

    
    public void setFetchSize(java.lang.Integer value){
        checkAllowChange();
        
        this._fetchSize = value;
           
    }

    
    /**
     * 
     * xml name: fields
     *  为sql查询语句补充列的类型信息，便于从ResultSet中抽取指定类型的数据，避免返回数据库引擎内部的数据类型
     */
    
    public java.util.List<io.nop.orm.sql_lib.SqlFieldModel> getFields(){
      return _fields;
    }

    
    public void setFields(java.util.List<io.nop.orm.sql_lib.SqlFieldModel> value){
        checkAllowChange();
        
        this._fields = KeyedList.fromList(value, io.nop.orm.sql_lib.SqlFieldModel::getName);
           
    }

    
    public io.nop.orm.sql_lib.SqlFieldModel getField(String name){
        return this._fields.getByKey(name);
    }

    public boolean hasField(String name){
        return this._fields.containsKey(name);
    }

    public void addField(io.nop.orm.sql_lib.SqlFieldModel item) {
        checkAllowChange();
        java.util.List<io.nop.orm.sql_lib.SqlFieldModel> list = this.getFields();
        if (list == null || list.isEmpty()) {
            list = new KeyedList<>(io.nop.orm.sql_lib.SqlFieldModel::getName);
            setFields(list);
        }
        list.add(item);
    }
    
    public java.util.Set<String> keySet_fields(){
        return this._fields.keySet();
    }

    public boolean hasFields(){
        return !this._fields.isEmpty();
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
     * xml name: ormEntityRefreshBehavior
     *  当SQL语句返回实体数据，并且使用rowType来构建实体对象时所使用的更新策略
     */
    
    public io.nop.orm.sql_lib.OrmEntityRefreshBehavior getOrmEntityRefreshBehavior(){
      return _ormEntityRefreshBehavior;
    }

    
    public void setOrmEntityRefreshBehavior(io.nop.orm.sql_lib.OrmEntityRefreshBehavior value){
        checkAllowChange();
        
        this._ormEntityRefreshBehavior = value;
           
    }

    
    /**
     * 
     * xml name: querySpace
     *  指定查询空间，一般一个querySpace对应一个数据库。
     */
    
    public java.lang.String getQuerySpace(){
      return _querySpace;
    }

    
    public void setQuerySpace(java.lang.String value){
        checkAllowChange();
        
        this._querySpace = value;
           
    }

    
    /**
     * 
     * xml name: rowType
     *  可以指定返回结果对应的包装类名称。会按照字段别名映射到对象的属性上，
     */
    
    public java.lang.String getRowType(){
      return _rowType;
    }

    
    public void setRowType(java.lang.String value){
        checkAllowChange();
        
        this._rowType = value;
           
    }

    
    /**
     * 
     * xml name: sqlMethod
     *  指定使用ISqlExecutor接口上的什么方法去执行此sql，具体可选项在SqlMethod枚举类中定义，
     */
    
    public io.nop.orm.sql_lib.SqlMethod getSqlMethod(){
      return _sqlMethod;
    }

    
    public void setSqlMethod(io.nop.orm.sql_lib.SqlMethod value){
        checkAllowChange();
        
        this._sqlMethod = value;
           
    }

    
    /**
     * 
     * xml name: timeout
     *  sql执行的超时时间。单位为毫秒
     */
    
    public java.lang.Integer getTimeout(){
      return _timeout;
    }

    
    public void setTimeout(java.lang.Integer value){
        checkAllowChange();
        
        this._timeout = value;
           
    }

    
    /**
     * 
     * xml name: 
     *  
     */
    
    public java.lang.String getType(){
      return _type;
    }

    
    public void setType(java.lang.String value){
        checkAllowChange();
        
        this._type = value;
           
    }

    
    /**
     * 
     * xml name: validate-input
     *  验证sql正确性时采用的输入数据。当AppConfig.isDebugMode()为true时（调试模式），sql-lib加载后会自动验证sql语句语法正确。
     */
    
    public io.nop.core.lang.eval.IEvalAction getValidateInput(){
      return _validateInput;
    }

    
    public void setValidateInput(io.nop.core.lang.eval.IEvalAction value){
        checkAllowChange();
        
        this._validateInput = value;
           
    }

    

    @Override
    public void freeze(boolean cascade){
        if(frozen()) return;
        super.freeze(cascade);

        if(cascade){ //NOPMD - suppressed EmptyControlStatement - Auto Gen Code
        
           this._args = io.nop.api.core.util.FreezeHelper.deepFreeze(this._args);
            
           this._auth = io.nop.api.core.util.FreezeHelper.deepFreeze(this._auth);
            
           this._fields = io.nop.api.core.util.FreezeHelper.deepFreeze(this._fields);
            
        }
    }

    @Override
    protected void outputJson(IJsonHandler out){
        super.outputJson(out);
        
        out.putNotNull("args",this.getArgs());
        out.putNotNull("auth",this.getAuth());
        out.putNotNull("batchLoadSelection",this.getBatchLoadSelection());
        out.putNotNull("buildResult",this.getBuildResult());
        out.putNotNull("buildRowMapper",this.getBuildRowMapper());
        out.putNotNull("cacheKeyExpr",this.getCacheKeyExpr());
        out.putNotNull("cacheName",this.getCacheName());
        out.putNotNull("description",this.getDescription());
        out.putNotNull("disableLogicalDelete",this.isDisableLogicalDelete());
        out.putNotNull("disableSmartRowMapper",this.isDisableSmartRowMapper());
        out.putNotNull("displayName",this.getDisplayName());
        out.putNotNull("fetchSize",this.getFetchSize());
        out.putNotNull("fields",this.getFields());
        out.putNotNull("name",this.getName());
        out.putNotNull("ormEntityRefreshBehavior",this.getOrmEntityRefreshBehavior());
        out.putNotNull("querySpace",this.getQuerySpace());
        out.putNotNull("rowType",this.getRowType());
        out.putNotNull("sqlMethod",this.getSqlMethod());
        out.putNotNull("timeout",this.getTimeout());
        out.putNotNull("type",this.getType());
        out.putNotNull("validateInput",this.getValidateInput());
    }

    public SqlItemModel cloneInstance(){
        SqlItemModel instance = newInstance();
        this.copyTo(instance);
        return instance;
    }

    protected void copyTo(SqlItemModel instance){
        super.copyTo(instance);
        
        instance.setArgs(this.getArgs());
        instance.setAuth(this.getAuth());
        instance.setBatchLoadSelection(this.getBatchLoadSelection());
        instance.setBuildResult(this.getBuildResult());
        instance.setBuildRowMapper(this.getBuildRowMapper());
        instance.setCacheKeyExpr(this.getCacheKeyExpr());
        instance.setCacheName(this.getCacheName());
        instance.setDescription(this.getDescription());
        instance.setDisableLogicalDelete(this.isDisableLogicalDelete());
        instance.setDisableSmartRowMapper(this.isDisableSmartRowMapper());
        instance.setDisplayName(this.getDisplayName());
        instance.setFetchSize(this.getFetchSize());
        instance.setFields(this.getFields());
        instance.setName(this.getName());
        instance.setOrmEntityRefreshBehavior(this.getOrmEntityRefreshBehavior());
        instance.setQuerySpace(this.getQuerySpace());
        instance.setRowType(this.getRowType());
        instance.setSqlMethod(this.getSqlMethod());
        instance.setTimeout(this.getTimeout());
        instance.setType(this.getType());
        instance.setValidateInput(this.getValidateInput());
    }

    protected SqlItemModel newInstance(){
        return (SqlItemModel) ClassHelper.newInstance(getClass());
    }
}
 // resume CPD analysis - CPD-ON
