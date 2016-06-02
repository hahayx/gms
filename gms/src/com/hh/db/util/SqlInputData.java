package com.hh.db.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SqlInputData {
	static enum FieldType {
		QUERY,//查询字段
		WHERE,//where条件字段
		UPDATE,//更新字段
		UPDATE_INC,//更新而且是增量更新字段
		INSERT,//插入字段
		ORDER, //排序字段
		GROUP;//分组字段
	}
	private String tableName;//表名或者表前缀
	private Object partDBKey;//如果是分库则是分库单元
	private int offset;
	private int limit;
	private Map<FieldType, List<Field>> typeToField=new HashMap<FieldType, List<Field>>();
	private String forceIndexField;//强制使用索引
	
	private List<Object> param;//填充sql占位符的参数
	public SqlInputData clone() {//对象克隆，不克隆param
		SqlInputData newSql=new SqlInputData(tableName,offset,limit,partDBKey);
		for (Entry<FieldType, List<Field>> e : typeToField.entrySet()) {
			newSql.typeToField.put(e.getKey(), new ArrayList<Field>(e.getValue()));
		}
		newSql.forceIndexField=this.forceIndexField;
		return newSql;
	}
	public SqlInputData(String tableName) {
		this(tableName,0,0);
	}
	public SqlInputData(String tableName,int offset,int limit) {
		this(tableName,offset,limit,null);
	}
	private SqlInputData(String tableName,int offset,int limit,Object partDBKey) {
		this.offset=offset;
		this.limit=limit;
		this.tableName = tableName;
		this.partDBKey=partDBKey;
	}
	//query field
	public SqlInputData addQueryField(String...fields) {
		return addField(FieldType.QUERY, fields);
	}
	
	//update field
	
	///添加表达式update字段
	public SqlInputData addExpUpdateField(String expression) {
		return addUpdateField(new ExpressionField(expression));
	}
	public SqlInputData addUpdateField(String field,Object value) {
		return addField(FieldType.UPDATE,new Field(field,value));
	}
	public SqlInputData addUpdateField(Map<String, ?> keyValues) {
		return addField(FieldType.UPDATE,keyValues);
	}
	public SqlInputData addUpdateField(Field field) {
		return addField(FieldType.UPDATE, field);
	}
	public SqlInputData addUpdateField(List<Field> fields) {
		return addField(FieldType.UPDATE, fields);
	}
	
	///inc filed
	public SqlInputData addIncField(String field,Object value) {
		return addField(FieldType.UPDATE_INC,new Field(field,value));
	}
	public SqlInputData addIncField(Map<String, ?> keyValues) {
		return addField(FieldType.UPDATE_INC,keyValues);
	}
	public SqlInputData addIncField(Field field) {
		return addField(FieldType.UPDATE_INC, field);
	}
	public SqlInputData addIncField(List<Field> fields) {
		return addField(FieldType.UPDATE_INC, fields);
	}
	
	//where field
	
	///添加表达式where条件
	public SqlInputData addExpWhereField(String expression) {
		return addWhereField(new ExpressionField(expression));
	}
	public SqlInputData addWhereField(String name,String operation, Object value) {
		return addField(FieldType.WHERE,new Field(name,operation,value));
	}
	public SqlInputData addWhereField(String field,Object value) {
		return addField(FieldType.WHERE,new Field(field,value));
	}
	public SqlInputData addWhereField(Map<String, ?> keyValues) {
		return addField(FieldType.WHERE,keyValues);
	}
	public SqlInputData addWhereField(Field field) {
		return addField(FieldType.WHERE, field);
	}
	public SqlInputData addWhereField(List<Field> fields) {
		return addField(FieldType.WHERE, fields);
	}
	
	//insert field
	public SqlInputData addInsertField(String field,Object value) {
		return addField(FieldType.INSERT,new Field(field,value));
	}
	public SqlInputData addInsertField(Map<String, ?> keyValues) {
		return addField(FieldType.INSERT,keyValues);
	}
	public SqlInputData addInsertField(Field field) {
		return addField(FieldType.INSERT, field);
	}
	public SqlInputData addInsertField(List<Field> fields) {
		return addField(FieldType.INSERT, fields);
	}
	
	//order field
	public SqlInputData addOrderField(String field,Object value) {
		return addField(FieldType.ORDER, new Field(field,value));
	}
	public SqlInputData addOrderField(String...fields) {
		return addField(FieldType.ORDER, fields);
	}
	public SqlInputData addOrderField(List<Field> fields) {
		return addField(FieldType.ORDER, fields);
	}
	public SqlInputData addGroupField(String...fields) {
		return addField(FieldType.GROUP, fields);
	}
	public SqlInputData setFenye(int offset,int limit) {
		this.offset = offset;
		this.limit = limit;
		return this;
	}
	public SqlInputData setPartDBKey(Object partDBKey) {
		this.partDBKey = partDBKey;
		return this;
	}
	public SqlInputData setForceIndexField(String forceIndexField) {
		this.forceIndexField = forceIndexField;
		return this;
	}
	
	///private method
	private SqlInputData addField(FieldType fieldType, Map<String, ?> keyValues) {
		if(keyValues!=null){
			for (Entry<String, ?> e : keyValues.entrySet()) {
				addField(fieldType, new Field(e.getKey(),e.getValue()));
			}
		}
		return this;
	}
	private SqlInputData addField(FieldType fieldType,String...fields) {
		if(fields!=null){
			for (String field : fields) {
				addField(fieldType, new Field(field));
			}
		}
		return this;
	}
	private SqlInputData addField(FieldType fieldType,List<Field> fields) {
		if(fields!=null){
			for (Field field : fields) {
				addField(fieldType, field);
			}
		}
		return this;
	}
	private SqlInputData addField(FieldType fieldType,Field field) {
		List<Field> fields = typeToField.get(fieldType);
		if(fields==null){
			fields=new ArrayList<Field>();
			typeToField.put(fieldType, fields);
		}
		fields.add(field);
		return this;
	}
	
	
	///包访问权限开始,只允许框架底层调用
	List<Field> getFields(FieldType fieldType) {
		List<Field> fileds = typeToField.get(fieldType);
		if(fileds==null){
			return Collections.emptyList();
		}
		return fileds;
	}
	int getOffset() {
		return offset;
	}
	int getLimit() {
		return limit;
	}
	String getTableName() {
		return tableName;
	}
	void setTableName(String tableName) {
		this.tableName = tableName;
	}
	Object getPartDBKey() {
		return partDBKey;
	}
	String getForceIndexField() {
		return forceIndexField;
	}
	///方法作用：重新初始化参数
	void resetParam() {
		param=null;
	}
	Object[] getParam() {
		return param==null?new Object[]{}:param.toArray();
	}
	void addParam(Object paramItem) {
		if(param==null){
			param=new ArrayList<Object>();
		}
		param.add(paramItem);
	}


	
}
