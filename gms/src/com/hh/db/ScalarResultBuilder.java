package com.hh.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hh.common.data.MapData;



public final class ScalarResultBuilder {

	public final static ResultObjectBuilder<Integer> intResultBuilder = new ResultObjectBuilder<Integer>() {
		public Integer build(ResultSet rs) throws SQLException {
			int value = rs.getInt(1);
			return rs.wasNull() ? null : value;
		}
	};

	public final static ResultObjectBuilder<Long> longResultBuilder = new ResultObjectBuilder<Long>() {

		@Override
		public Long build(ResultSet rs) throws SQLException {
			long value = rs.getLong(1);
			return rs.wasNull() ? null : value;
		}
	};

	public final static ResultObjectBuilder<Float> floatResultBuilder = new ResultObjectBuilder<Float>() {

		@Override
		public Float build(ResultSet rs) throws SQLException {
			float value = rs.getFloat(1);
			return rs.wasNull() ? null : value;
		}
	};

	public final static ResultObjectBuilder<Double> doubleResultBuilder = new ResultObjectBuilder<Double>() {

		@Override
		public Double build(ResultSet rs) throws SQLException {
			double value = rs.getDouble(1);
			return rs.wasNull() ? null : value;
		}
	};

	public final static ResultObjectBuilder<Boolean> booleanResultBuilder = new ResultObjectBuilder<Boolean>() {

		@Override
		public Boolean build(ResultSet rs) throws SQLException {
			boolean value = rs.getBoolean(1);
			return rs.wasNull() ? null : value;
		}
	};

	public final static ResultObjectBuilder<Date> dateResultBuilder = new ResultObjectBuilder<Date>() {
		public Date build(ResultSet rs) throws SQLException {
			return rs.getTimestamp(1);
		}
	};
	public final static ResultObjectBuilder<String> stringResultBuilder = new ResultObjectBuilder<String>() {
		public String build(ResultSet rs) throws SQLException {
			return rs.getString(1);
		}
	};
	public final static ResultObjectBuilder<Map<String, String>> stringMapBuilder = new ResultObjectBuilder<Map<String, String>>() {
		public Map<String, String> build(ResultSet rs) throws SQLException {
			Map<String, String> result = new HashMap<String, String>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i <= columnCount; ++i) {
				result.put(rsmd.getColumnName(i), rs.getString(i));
			}
			return result;
		}
	};
	public final static ResultObjectBuilder<Map<String, Object>> objectMapBuilder = new ResultObjectBuilder<Map<String, Object>>() {
		public Map<String, Object> build(ResultSet rs) throws SQLException {
			Map<String, Object> result = new HashMap<String, Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i <= columnCount; ++i) {
				result.put(rsmd.getColumnName(i), rs.getObject(i));
			}
			return result;
		}
	};
	public final static ResultObjectBuilder<MapData> mapDataBuilder = new ResultObjectBuilder<MapData>() {
		public MapData build(ResultSet rs) throws SQLException {
			Map<String, Object> fieldValues = objectMapBuilder.build(rs);
			return fieldValues == null ? null : new MapData(fieldValues);
		}
	};

}
