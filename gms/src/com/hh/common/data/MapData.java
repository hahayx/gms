package com.hh.common.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hh.common.utils.WebUtil;



public class MapData implements Map<String, Object>, Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Object> map;

	public MapData() {
		this(new HashMap<String, Object>());
	}

	public MapData(MapData entity) {
		this(new HashMap<String, Object>(entity));
	}

	public MapData(Map<String, ?> map) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		this.map = new HashMap<String, Object>(map);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}
	public int getInt(String key,int defalutVal) {
		return WebUtil.getInt(key, this, defalutVal);
	}
	public Integer getInteger(String key) {
		return WebUtil.getInt(key, this);
	}

	public long getlong(String key) {
		return getlong(key, 0);
	}
	public long getlong(String key,long defalutVal) {
		return WebUtil.getLong(key, this, defalutVal);
	}
	public Long getLong(String key) {
		return WebUtil.getLong(key, this);
	}

	public boolean getboolean(String key) {
		return getboolean(key, false);
	}
	public boolean getboolean(String key,boolean defaultVal) {
		return WebUtil.getBoolean(key, this, defaultVal);
	}
	public Boolean getBoolean(String key) {
		return WebUtil.getBoolean(key, this);
	}

	public String getString(String key) {
		return getString(key, null);
	}
	public String getString(String key, String defaultVal) {
		return WebUtil.getString(key, this, defaultVal);
	}

	public Date getDate(String key) {
		return WebUtil.getDate(key, this);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getArr(String key) {
		return (T[]) map.get(key);
	}

	public <T> Collection<T> getCollection(String key) {
		return WebUtil.getCollection(key, this);
	}

	public <T> List<T> getList(String key) {
		return WebUtil.getList(key, this);
	}

	public Map<String, Object> getKeyValues() {
		return map;
	}

	public void setKeyValues(Map<String, Object> map) {
		this.map = map;
	}
	public float getfloat(String key) {
		return getfloat(key, 0.0F);
	}
	public float getfloat(String key,float defaultVal) {
		return WebUtil.getFloat(key, this,defaultVal);
	}
	public Float getFloat(String key) {
		return WebUtil.getFloat(key, this);
	}


	public double getdouble(String key) {
		return getdouble(key, 0.0);
	}
	public double getdouble(String key,double dafaultVal) {
		return WebUtil.getDouble(key, this, dafaultVal);
	}
	public Double getDouble(String key) {
		return WebUtil.getDouble(key, this);
	}

	public void setDate(String key, String dateStr){
		WebUtil.setDate(key, this, dateStr);
	}

	public Map<String, String> toStringMap() {
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		for (Entry<String, Object> entry : map.entrySet()) {
			fieldValueMap.put(entry.getKey(), WebUtil.getString(entry.getValue()));
		}
		return fieldValueMap;
	}

	public Map<String, String> toStringMap(Collection<String> keys) {
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		for (String key : keys) {
			if (map.containsKey(key)) {
				fieldValueMap.put(key, WebUtil.getString(map.get(key)));
			}
		}
		return fieldValueMap;
	}

	public Map<String, Object> toObjectMap(Collection<String> keys) {
		Map<String, Object> fieldValueMap = new HashMap<String, Object>();
		for (String key : keys) {
			if (map.containsKey(key)) {
				fieldValueMap.put(key, map.get(key));
			}
		}
		return fieldValueMap;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	// ---------------------------------------------------
	// MAP interface
	// ---------------------------------------------------
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return map.put(key, value);
	}
	///put的链式版本
	public MapData set(String key,Object value){
		put(key, value);
		return this;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> keyValues) {
		map.putAll(keyValues);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}
	///remove的链式版本
	public MapData delete(Object key) {
		remove(key);
		return this;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}

}