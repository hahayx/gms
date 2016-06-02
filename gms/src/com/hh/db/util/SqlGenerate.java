package com.hh.db.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hh.db.util.SqlInputData.FieldType;



enum SqlGenerate{
	DELETE{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return new StringBuilder("delete from ")
						.append(sqlInput.getTableName())
						.append(getWherePart(sqlInput))
						.append(getOrderPart(sqlInput))
					    .append(getPagePart(false,sqlInput));
		}
	}
	,UPDATE{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return	new StringBuilder("update ")
						.append(sqlInput.getTableName())
						.append(" set ")
					    .append(getUpdatePart(sqlInput,false))
					    .append(getWherePart(sqlInput))
					    .append(getOrderPart(sqlInput))
					    .append(getPagePart(false,sqlInput));
		}
	}
	,UPDATEIGNORE{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return	new StringBuilder("update ignore ")
			.append(sqlInput.getTableName())
			.append(" set ")
			.append(getUpdatePart(sqlInput,false))
			.append(getWherePart(sqlInput))
			.append(getOrderPart(sqlInput))
			.append(getPagePart(false,sqlInput));
		}
	}
	,INSERT{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return getInsertSql(sqlInput,"insert into  ");
		}
	},INSERTOrUpdate{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return INSERT.genSqlByInput(sqlInput)
						 .append(" on duplicate key update ")
						 .append(getUpdatePart(sqlInput,true));
		}
	},InsertIgnore{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return getInsertSql(sqlInput,"insert ignore into  ");
		}
	},Replace{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return getInsertSql(sqlInput,"replace into  ");
		}
	},SELECT{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return getSelectSql(sqlInput, selectAllField)
				   .append(getOrderPart(sqlInput))
				   .append(getPagePart(true,sqlInput));
		}

	},SelectCountOrSelectOneField{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			List<Field> queryFields = sqlInput.getFields(FieldType.QUERY);
			StringBuilder sql;
			if (queryFields == null || queryFields.size() == 0) {
				sql = getSelectSql(sqlInput, selectCountField);
			} else {
				sql = SELECT.genSqlByInput(sqlInput);
			}
			return sql;
		}

	},SelectExist{
		StringBuilder genSqlByInput(SqlInputData sqlInput) {
			return new StringBuilder(String.format("select 1 from %s",sqlInput.getTableName()))
						.append(getWherePart(sqlInput))
						.append(String.format(" limit %s,1",sqlInput.getOffset()));
		}
	};
	public StringBuilder getInsertSql(SqlInputData sqlInput,String insertSqlPrefix) {
		if(sqlInput.getFields(FieldType.INSERT).isEmpty()){
			throw new IllegalArgumentException("插入字段不能为空！");
		}
		return new StringBuilder(insertSqlPrefix)
				.append(sqlInput.getTableName())
				.append(getInsertPart(sqlInput));
	}
	public StringBuilder getSelectSql(SqlInputData sqlInput,String defaultField) {
		StringBuilder sql=new StringBuilder("select ");
		List<Field> queryFields = sqlInput.getFields(FieldType.QUERY);
		if(queryFields==null||queryFields.size()==0){
			sql.append(defaultField+" from ");
		}else{
			sql.append(getFieldPart(queryFields)+" from ");
		}
		sql.append(sqlInput.getTableName())
		.append(getForceIndexPart(sqlInput))
		   .append(getWherePart(sqlInput))
		   .append(getGroupPart(sqlInput));
		return sql;
	}
	private final static String selectCountField="count(*)";
	private final static String selectAllField="*";
	private final static Logger logger=LoggerFactory.getLogger("sql");
	public String getSql(SqlInputData sqlInput){
		sqlInput.resetParam();
		String sql=genSqlByInput(sqlInput).toString();
		if (logger.isDebugEnabled()) {
			logger.debug("【{}】->【{}】",sql,Arrays.toString(sqlInput.getParam()));
		}
		return sql;
	}
	abstract StringBuilder genSqlByInput(SqlInputData sqlInput);

	private static String getPagePart(boolean withOffset,SqlInputData sqlInput) {
		StringBuilder pageStr=new StringBuilder();
		int limit = sqlInput.getLimit();
		if(limit>0 && limit<Integer.MAX_VALUE){
			pageStr.append(" limit ");
			if(withOffset){
				int offset = sqlInput.getOffset();
				if(offset>0){
					pageStr.append(offset).append(",");
				}
			}
			pageStr.append(sqlInput.getLimit());
		}
		return pageStr.toString();
	}
	private static String getOrderPart(SqlInputData sqlInput) {
		StringBuilder orderStr=new StringBuilder();
		int index=0;
		for (Field field : sqlInput.getFields(FieldType.ORDER)) {
			if(index!=0){
				orderStr.append(",");
			}else{
				index=1;
				orderStr.append(" order by ");
			}
			String fieldVal=(String) field.getValue();
			if(fieldVal==null||!(fieldVal.equals("asc")||fieldVal.equals("desc"))){
				fieldVal="asc";
			}
			orderStr.append(field.getName()).append(" ").append(fieldVal);
		}
		return orderStr.toString();
	}
	private static String getGroupPart(SqlInputData sqlInput) {
		StringBuilder orderStr=new StringBuilder();
		int index=0;
		for (Field field : sqlInput.getFields(FieldType.GROUP)) {
			if(index!=0){
				orderStr.append(",");
			}else{
				index=1;
				orderStr.append(" group by ");
			}
			orderStr.append(field.getName());
		}
		return orderStr.toString();
	}
	private static String getFieldPart(List<Field> filterFields) {
		StringBuilder fields=new StringBuilder();
		int index=0;
		for (Field filed : filterFields) {
			if(index++!=0)fields.append(",");
			fields.append(filed.getName());
		}
		return fields.toString();
	}
	private static String getInsertPart(SqlInputData sqlInput) {
		StringBuilder filedName=new StringBuilder("(");
		StringBuilder valueName=new StringBuilder("values(");
		int index=0;
		for (Field filed : sqlInput.getFields(FieldType.INSERT)) {
			if(index++!=0){
				filedName.append(",");
				valueName.append(",");
			}
			filedName.append(filed.getName());
			valueName.append(getFieldValue(filed.getValue(),sqlInput));
		}
		filedName.append(") ");
		valueName.append(") ");
		return filedName.append(valueName).toString();
	}
	private static String getUpdatePart(SqlInputData sqlInput,boolean useInsertIfNullUpdateFields) {
		StringBuilder setSql=new StringBuilder();
		List<Field> updateFields = sqlInput.getFields(FieldType.UPDATE);
		List<Field> incFields = sqlInput.getFields(FieldType.UPDATE_INC);
		if(useInsertIfNullUpdateFields && updateFields.isEmpty() && incFields.isEmpty()){///如果没有更新字段，则把插入字段当作是更新字段
			updateFields=sqlInput.getFields(FieldType.INSERT);
		}
		int index=0;
		for (Field filed : updateFields) {
			if(index++!=0){setSql.append(",");}
			if(filed instanceof ExpressionField){
				setSql.append(((ExpressionField)filed).getExp());
			}else{
				setSql.append(filed.getName()).append("=").append(getFieldValue(filed.getValue(),sqlInput));
			}
		}
		for (Field filed : incFields) {
			if(index++!=0){setSql.append(",");}
			String filedName = filed.getName();
			setSql.append(filedName).append("=").append(filedName).append("+").append(getFieldValue(filed.getValue(),sqlInput));
		}
		return setSql.toString();
	}
	
	private static final String FORCE_INDEX_SQL = " force index(%s)";
	private static String getForceIndexPart(SqlInputData sqlInput) {
		if (!StringUtils.isBlank(sqlInput.getForceIndexField())) {
			return String.format(FORCE_INDEX_SQL, sqlInput.getForceIndexField());
		} else {
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String getWherePart(SqlInputData sqlInput) {
		StringBuilder whereSql=new StringBuilder();
		int index=0;
		for (Field filed : sqlInput.getFields(FieldType.WHERE)) {
			whereSql.append(" ").append(index++==0?"where ":"and ");
			if(filed instanceof ExpressionField){
				whereSql.append(((ExpressionField)filed).getExp());
				continue;
			}
			String oper = filed.getOperation().trim();
			if(oper.equals("in") || oper.equals("not in")){
				Object val = filed.getValue();
				if(val instanceof Object[] || val instanceof Collection || val instanceof int[]){
					Object[] vals;
					if(val instanceof Collection){
						vals = ((Collection<? extends Object>)val).toArray();
					}else if(val instanceof int[]){
						vals=new Object[((int[])val).length];
						int intIndex=0;
						for (int valItem: (int[])val) {
							vals[intIndex++]=valItem;
						}
					}else{
						vals=(Object[]) val;
					}
					int valSize=vals.length;
					if(vals!=null&&valSize!=0){
						whereSql.append(String.format("%s %s(",filed.getName(),oper));
						for (Object value : vals) {
							whereSql.append(getFieldValue(value, sqlInput));
							whereSql.append(valSize--==1?")":",");
						}
					}else{
						throw new IllegalArgumentException(String.format("作为where in的字段[%s]长度小于1出错!", filed.getName()));
					}
				}else{
					whereSql.append(String.format("%s %s(%s)",filed.getName(),oper,filed.getValue()));
				}
			}else{
				whereSql.append(filed.getName()+" "+oper+" "+getFieldValue(filed.getValue(),sqlInput));
			}
		}
		return whereSql.toString();
	}

	private static String getFieldValue(Object value,SqlInputData sqlInput) {
		sqlInput.addParam(value);
		return "?";
	}
}
