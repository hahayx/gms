package com.hh.db.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hh.common.data.MapData;
import com.hh.db.DbException;
import com.hh.db.DbManager;
import com.hh.db.ResultObjectBuilder;
import com.hh.db.ScalarResultBuilder;



public class DbUtil {
	private DbManager db;
	private final static Map<String, DbUtil> Instances = new HashMap<String, DbUtil>();
	public DbUtil(String dbName) throws DbException {
		db = DbManager.getDbManager(dbName);
	}
	public final static DbUtil getInstance(String dbName) throws DbException{
		DbUtil instance = Instances.get(dbName);
		if(instance==null){
			synchronized (DbUtil.class) { 
				if(instance==null){
					instance=new DbUtil(dbName);
				}
				Instances.put(dbName, instance);
			}
		}
		return instance;
	}
	public DbManager getDbManager(){
		return db;
	}
	public int delete(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.DELETE.getSql(sqlInput), sqlInput.getParam());
	}
	public int insert(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.INSERT.getSql(sqlInput), sqlInput.getParam());
	}
	///如果没有设置更新字段，默认就是插入的字段就是要更新的字段
	public int insertOrUpdate(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.INSERTOrUpdate.getSql(sqlInput), sqlInput.getParam());
	}
	public int insertIgnore(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.InsertIgnore.getSql(sqlInput), sqlInput.getParam());
	}

	public int replace(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.Replace.getSql(sqlInput), sqlInput.getParam());
	}

	public int update(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.UPDATE.getSql(sqlInput), sqlInput.getParam());
	}
	
	public int updateIgnore(SqlInputData sqlInput) throws DbException {
		return db.executeCommand(SqlGenerate.UPDATEIGNORE.getSql(sqlInput), sqlInput.getParam());
	}

	public MapData select(SqlInputData sqlInput) throws DbException {
		initSelectOneFenye(sqlInput);
		return db.executeScalarObject(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(),
				ScalarResultBuilder.mapDataBuilder);
	}
	public <T> T select(SqlInputData sqlInput, ResultObjectBuilder<T> builder) throws DbException {
		initSelectOneFenye(sqlInput);
		return db.executeScalarObject(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(), builder);
	}

	// /如果没有查询字段默认字段是select Count(*) from *****8
	public Integer selectInt(SqlInputData sqlInput) throws DbException {
		return db.executeScalarInt(SqlGenerate.SelectCountOrSelectOneField.getSql(sqlInput), sqlInput.getParam());
	}
	public Long selectLong(SqlInputData sqlInput) throws DbException {
		return db.executeScalarLong(SqlGenerate.SelectCountOrSelectOneField.getSql(sqlInput), sqlInput.getParam());
	}

	public String selectString(final SqlInputData sqlInput) throws DbException {
		initSelectOneFenye(sqlInput);
		return db.executeScalarString(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam());
	}
	public Date selectDate(final SqlInputData sqlInput) throws DbException {
		initSelectOneFenye(sqlInput);
		return db.executeScalarDate(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam());
	}
	private void initSelectOneFenye(SqlInputData sqlInput) {
		///对于上面select one都会初始化limit为1
		sqlInput.setFenye(sqlInput.getOffset(),1);
	}
	public List<Date> selectDateList(final SqlInputData sqlInput) throws DbException {
		return db.executeQuery_ObjectList(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(),
				ScalarResultBuilder.dateResultBuilder);
	}
	public boolean exist(SqlInputData sqlInput) throws DbException {
		return db.isRecordExist(SqlGenerate.SelectExist.getSql(sqlInput), sqlInput.getParam());
	}

	public List<MapData> selectList(final SqlInputData sqlInput) throws DbException {
		return db.executeQuery_ObjectList(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(),
				ScalarResultBuilder.mapDataBuilder);
	}

	public <T> List<T> selectList(final SqlInputData sqlInput, ResultObjectBuilder<T> builder)
			throws DbException {
		return db.executeQuery_ObjectList(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(), builder);
	}

	public List<Integer> selectIntList(final SqlInputData sqlInput) throws DbException {
		return db.executeQuery_ObjectList(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(),
				ScalarResultBuilder.intResultBuilder);
	}

	public List<String> selectStringList(final SqlInputData sqlInput) throws DbException {
		return db.executeQuery_ObjectList(SqlGenerate.SELECT.getSql(sqlInput), sqlInput.getParam(),
				ScalarResultBuilder.stringResultBuilder);
	}

	public int[] deleteBatch(List<SqlInputData> sqlInputs) throws Exception {
		return executeBatch(sqlInputs, SqlGenerate.DELETE);
	}

	public int[] insertBatch(List<SqlInputData> sqlInputs) throws Exception {
		return executeBatch(sqlInputs, SqlGenerate.INSERT);
	}

	public int[] insertIgnoreBatch(List<SqlInputData> sqlInputs) throws Exception {
		return executeBatch(sqlInputs, SqlGenerate.InsertIgnore);
	}

	public int[] replaceBatch(List<SqlInputData> sqlInputs) throws Exception {
		return executeBatch(sqlInputs, SqlGenerate.Replace);
	}

	public int[] updateBatch(List<SqlInputData> sqlInputs) throws Exception {
		return executeBatch(sqlInputs, SqlGenerate.UPDATE);
	}

	/**
	 * 批量操作，如果sql是一样的，那么就是真正的批量操作 如果sql不一样，那么实际上是多次操作，而不是批量
	 * 
	 * @param sqlInputs
	 * @param sqlGenerate
	 * @return
	 * @throws Exception
	 */
	public int[] executeBatch(List<SqlInputData> sqlInputs, SqlGenerate sqlGenerate) throws Exception {
		if (sqlInputs == null || sqlInputs.size() == 0) {
			return null;
		}
		Collection<Object[]> paramsList = new ArrayList<Object[]>();
		Collection<String> sqls = new ArrayList<String>();
		boolean hasDifferentSql = false;
		String commonSql = null;
		for (SqlInputData sqlInput : sqlInputs) {
			String tempSql = sqlGenerate.getSql(sqlInput);
			if (!hasDifferentSql && commonSql != null && !commonSql.equals(tempSql)) {
				hasDifferentSql = true;
			}
			commonSql = tempSql;
			sqls.add(commonSql);
			paramsList.add(sqlInput.getParam());
		}
		int[] rs;
		if (hasDifferentSql) {
			rs = new int[sqls.size()];
			Iterator<String> sqlItr = sqls.iterator();
			Iterator<Object[]> paramsItr = paramsList.iterator();
			int index = 0;
			while (sqlItr.hasNext()) {
				rs[index++] = db.executeCommand(sqlItr.next(), paramsItr.next());
			}
		} else {
			rs = db.executeBatchCommand(commonSql, paramsList);
		}

		return rs;
	}
}
