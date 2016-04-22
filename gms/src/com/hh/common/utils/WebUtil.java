package com.hh.common.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;

import com.hh.common.data.MapData;




public class WebUtil {
	private static final String[] DatePatterns = { "yyyy", "yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd HH",
		"yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS" };
	public static String firstToLowerCase(String key) {
		return key.substring(0, 1).toLowerCase()+key.substring(1);
	}
	
	///判断obj是否空字符串，如果obj不是字符类型，直接返回false
	public static boolean isBlankStr(Object obj) {
		return obj==null || (obj instanceof String && StringUtils.isBlank(obj.toString()));
	}
	///只有同类型并且equal时候才会返回true
	public static boolean equal(Object obj1, Object obj2) {
		if(obj1!=null&&obj2!=null){
			if(obj1.getClass()==obj2.getClass()){
				return obj1.equals(obj2);
			}
		}
		return false;
	}
	public static Object get(Object key,Map<?,?> param){
		return param.get(key);
	}
	@SuppressWarnings("unchecked")
	private static Object getSimpleObj(Object value) {
		Object rs=null;
		if(value!=null){
			//value是数组与集合类型默认返回第一个元素
			if(value instanceof Object[]){
				Object[] vals = (Object[])value;
				rs=vals.length>0?vals[0]:rs;
			}else if(value instanceof Collection){
				Collection<? extends Object> vals = ((Collection<? extends Object>)value);
				rs=vals.isEmpty()?rs:vals.iterator().next();
			}else{
				rs=value;
			}
		}
		return rs;
	}
	public static String getString(Object value) {
		return objToString(value, null);
	}
	public static String getString(Object value,String defaultVal) {
		return objToString(value, defaultVal);
	}
	private static String objToString(Object value,String defaultVal) {
		if(value instanceof String){
			return (String) value;
		}
		Object rs=getSimpleObj(value);
		return rs==null?defaultVal:rs.toString();
	}
	public static String getString(Object key,Map<?,?> param) {
		return getString(get(key, param));
	}
	public static String getString(Object key,Map<?,?> param,String defaultVal) {
		return getString(get(key, param), defaultVal);
	}
	public static String[] getArray(Object key,Map<?,?> param) {
		Object val = get(key, param);
		if(val instanceof String[]){
			return (String[])val; 
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T>  getList(Object key, Map<?,?> param) {
		Object val = get(key, param);
		if(val instanceof List){
			return (List<T>)val; 
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> getCollection(Object key, Map<?,?> param) {
		Object val = get(key, param);
		if(val instanceof Collection){
			return (Collection<T>)val; 
		}
		return null;
	}
	public static int[] getIntArr(Object key,Map<?,?> param) {
		Object objValue = get(key, param);
		if(objValue instanceof int[]){
			return (int[]) objValue;
		}
		String[] values = getArray(key, param);
		if(values==null||values.length==0){return null;}
		int[] valueInts=new int[values.length];
		int index=0;
		for (String value : values) {
			Integer intValue = toInt(value);
			if(intValue!=null){
				valueInts[index++]=intValue;
			}
		}
		return valueInts;
	}
	public static Integer getInt(Object key,Map<?,?> param) {
		return getInt(key, param, null);
	}
	public static Integer getInt(Object key,Map<?,?> param,Integer defaultVal) {
		return toInt(get(key, param), defaultVal);
	}
	public static Integer toInt(Object obj) {
		return toInt(obj, null);
	}
	public static Integer toInt(Object obj,Integer defaultVal) {
		if(obj==null){return defaultVal;}
		if(obj instanceof Integer){
			return (Integer) obj;
		}
		if(obj instanceof Boolean){
			return BooleanUtils.toIntegerObject((Boolean) obj);
		}
		String value = getString(obj);
		if(isInt(value)){
			return (int)NumberUtils.toDouble(value);
		}
		if(value.equals("true")){return 1;}//字符串为true刚返回1
		if(value.equals("false")){return 0;}//字符串为false刚返回0
		return defaultVal;//不是以下三种情况返回默认值
	}
	public static Long getLong(Object key,Map<?,?> param) {
		return getLong(key, param, null);
	}
	public static Long getLong(Object key,Map<?,?> param,Long defaultVal) {
		return toLong(get(key, param), defaultVal);
	}
	public static Long toLong(Object obj) {
		return toLong(obj, null);
	}
	public static Long toLong(Object obj,Long defaultVal) {
		if(obj==null){return defaultVal;}
		if(obj instanceof Long){
			return (Long) obj;
		}
		if(obj instanceof Integer){
			return (Integer)obj*1L;
		}
		if(obj instanceof Boolean){
			return BooleanUtils.toIntegerObject((Boolean) obj)*1L;
		}
		String value = getString(obj);
		if(isLong(value)){
			return (long)NumberUtils.toDouble(value);
		}
		if(value.equals("true")){return 1L;}//字符串为true刚返回1
		if(value.equals("false")){return 0L;}//字符串为false刚返回0
		return defaultVal;//不是以下三种情况返回默认值
	}
	public static Float getFloat(String key,Map<?,?> param) {
		return getFloat(key, param, null);
	}
	public static Float getFloat(String key,Map<?,?> param,Float defaultVal) {
		return toFloat(get(key, param),defaultVal);
	}
	public static Float toFloat(Object obj, Float defaultVal) {
		String value = getString(obj);
		if(value==null){return defaultVal;}
		if(isNumer(value)){return Float.valueOf(value);}//整型字符串，刚作转换
		return defaultVal;//不是以下三种情况返回默认值
	}

	public static Double getDouble(String key,Map<?,?> param) {
		return getDouble(key,param, null);
	}
	public static Double getDouble(String key,Map<?,?> param,Double dafaultVal) {
		return toDouble(get(key, param),dafaultVal);
	}
	public static Double toDouble(Object obj, Double dafaultVal) {
		String value = getString(obj);
		if(value==null){return dafaultVal;}
		if(isNumer(value)){return Double.valueOf(value);}//整型字符串，刚作转换
		return dafaultVal;//不是以下三种情况返回默认值
	}
	public static Boolean getBoolean(Object key, Map<?,?> param) {
		return getBoolean(key, param, null);
	}
	public static Boolean getBoolean(Object key, Map<?,?> param, Boolean defaultVal) {
		return toBoolean(get(key, param),defaultVal);
	}
	public static Boolean toBoolean(Object obj, Boolean defaultVal) {
		String value = getString(obj);
		if(value==null){return defaultVal;}
		if(isInt(value)){return Integer.valueOf(value)==1;}
		return Boolean.valueOf(value);
	}
	 public static boolean isInt(String str) {
         if(isNumer(str)){
                  Double d = Double.valueOf(str);
                  return d<=Integer.MAX_VALUE&&d>=Integer.MIN_VALUE;
         }
         return false;
	 }
	 public static boolean isLong(String str) {
         if(isNumer(str)){
                  Double d = Double.valueOf(str);
                  return d<=Long.MAX_VALUE&&d>=Long.MIN_VALUE;
         }
         return false;
	 }
	public static boolean isNumer(String str) {
		return NumberUtils.isNumber(str);
	}
	public static Date getDate(String key, Map<?,?> map) {
		Object object = map.get(key);
		Date value;
		if (object == null) {
			value = null;
		} else if (object instanceof Date) {
			value = (Date) object;
		}else if (object instanceof Long) {
			value = new Date((Long)object);
		} else {
			try {
				value = org.apache.commons.lang3.time.DateUtils.parseDate(getString(key,map), DatePatterns);
			} catch (ParseException e) {
				return null;
			}
		}
		return value;
	}
	public static void setDate(String key,Map<String,Object> map, String dateStr){
		try {
			map.put(key, org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, DatePatterns));
		} catch (ParseException e) {
		}
	}
	
	public static boolean listEmpty(Collection<?> list){
		return list==null||list.size()==0;
	}
	public static boolean arrEmpty(Object[] arr){
		return arr==null||arr.length==0;
	}
	public static String toString(int[] objArr){
		return toString(toArr(objArr));
	}
	public static String toString(Collection<?> list){
		return toString(list.toArray());
	}
	public static String toString(byte[] objArr){
		return toString(toArr(objArr));
	}
	public static String toString(long[] objArr){
		return toString(toArr(objArr));
	}
	public static String toString(Object[] objArr){
		return toString(objArr, ",");
	}
	public static String toString(Object[] objArr,String split){
		StringBuilder sb=new StringBuilder();
		int splitLen=split.length();
		for (Object obj : objArr) {
			sb.append(obj).append(split);
		}
		if (sb.length() == 0) {
			return sb.toString();
		}
		return sb.substring(0,sb.length()-splitLen);
	}
	public static Object[] toArr(Object obj) {
		return toList(obj).toArray();
	}
	@SuppressWarnings("unchecked")
	public static List<? extends Object> toList(Object obj) {
		if(obj==null){return Collections.emptyList();}
		if(obj instanceof Object[]){
			return Arrays.asList((Object[])obj);
		}
		List<Object> objList=new ArrayList<Object>();
		if(obj instanceof Collection){
			objList.addAll((Collection<? extends Object>) obj);
		}else if(obj instanceof byte[]){
			for(byte i : (byte[])obj) {objList.add(i);}
		}else if(obj instanceof int[]){
			for(int i : (int[])obj) {objList.add(i);}
		}else if(obj instanceof long[]){
			for(long i : (long[])obj) {objList.add(i);}
		}else if(obj instanceof float[]){
			for(float i : (float[])obj) {objList.add(i);}
		}else if(obj instanceof double[]){
			for(double i : (double[])obj) {objList.add(i);}
		}else{
			objList.add(obj);
		}
		return objList;
	}
	public static String getCurTime() {
		return DateUtils.formatDateTime(new Date());
	}
	public static Date addDate(int field,int amount){
		return addDate(field, amount, new Date());
	}
	public static Date addDate(int field,int amount,Date source){
		Calendar c=Calendar.getInstance();
		c.setTime(source);
		c.add(field, amount);
		return c.getTime();
	}
	///忽略时分秒计算
	public static long betweenDay(Date start,Date end){
		start=DateUtils.parseDateYDM(DateUtils.formatDateYDM(start));
		end=DateUtils.parseDateYDM(DateUtils.formatDateYDM(end));
		return betweenDate(TimeUnit.DAYS,start,end);
	}
	///忽略分秒计算
	public static long betweenHour(Date start,Date end){
		SimpleDateFormat ymdHm = new SimpleDateFormat("yyyy-MM-dd HH");
		start=parse(ymdHm,ymdHm.format(start));
		end=parse(ymdHm,ymdHm.format(end));
		return betweenDate(TimeUnit.HOURS,start,end);
	}
	public static long betweenSecond(Date start,Date end){
		return betweenDate(TimeUnit.SECONDS, start, end);
	}
	private static Date parse(SimpleDateFormat sdf,String dateStr) {
		try{
			return sdf.parse(dateStr);
		}catch(ParseException e){
			throw new IllegalArgumentException(e);
		}
	}
	private static long betweenDate(TimeUnit unit,Date start,Date end){
		return Math.abs((end.getTime()/unit.toMillis(1)-start.getTime()/unit.toMillis(1)));
	}
	////清除掉时分秒的日期对象
	public static Date clearHmsDate(Date date){
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	public static int randIntBetweent(int min, int max) {
		return randInt(max-min+1)+min-1;
	}
	public static int randInt(int n){
		return ((int)(Math.random()*n)+1);
	}
	public static int randInt(int n, int exclude) {
		while(true){
			int i=randInt(n);
			if(i!=exclude){return i;}
		}
	}
	public static Map<String, Object> toMap(String key,Object value) { 
		Map<String, Object> m=new HashMap<String, Object>();
		m.put(key,value);
		return m;
	}
	
	private final static ObjectMapper objMapper=new ObjectMapper();
	static{
		objMapper.configure(Feature.ALLOW_COMMENTS, true);
		objMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		objMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true); /*see: http://jira.codehaus.org/browse/JACKSON-208 */
	}
	public static String serialize(Object obj) {
		try {
			return objMapper.writeValueAsString(obj);
		} catch (Exception e) {
			return "{}";
		}
	}
	public static MapData unserialize(String jsonStr) {
		try{
			return objMapper.readValue(jsonStr, MapData.class);
		}catch (Exception e) {
			return new MapData();
		}
	}
	public static MapData unserialize(InputStream io) {
		try{
			return objMapper.readValue(io, MapData.class);
		}catch (Exception e) {
			return new MapData();
		}
	}
	public static MapData unserializeByJsonFile(String file) {
		try{
			
			return objMapper.readValue(new FileInputStream(file), MapData.class);
		}catch (Exception e) {
			e.printStackTrace();
			return new MapData();
		}
	}
	@SuppressWarnings("unchecked")
	public static Collection<Map<String, Object>> unserializeArr(String jsonStr)  {
		try {
			return objMapper.readValue(jsonStr, Collection.class);
		} catch (Exception e) {
			return new ArrayList<Map<String, Object>>();
		}
	}
	public static String replace(String source,String regexStr,String replacement) {
		return replace(source, regexStr, replacement,true);
	}
	public static String replace(String source,String regexStr,String replacement,boolean isLiteral) {
		Pattern regex;
		if(isLiteral){
			regex=Pattern.compile(regexStr,Pattern.LITERAL);
		}else{
			regex=Pattern.compile(regexStr);
		}
		return regex.matcher(source).replaceAll(replacement==null?"":replacement);
	}
	///内存分页
	public static <T> List<T> getCachePager(int offset,int limit,List<T> all) {
		if(listEmpty(all)){return new ArrayList<T>();}
		return getCachePager(offset, limit, all, all.size());
	}
	////这个函数比上面多加一个totalCount参数是因为避免重复计算all.size()
	public static <T> List<T> getCachePager(int offset,int limit,List<T> all,int totalCount) {
		if(totalCount==0){return new ArrayList<T>();}
		int fromIndex=offset;
		if(fromIndex>=totalCount){return new ArrayList<T>();}
		int toIndex=fromIndex+limit;
		toIndex=toIndex>=totalCount?totalCount:toIndex;
		List<T> list = all.subList(fromIndex, toIndex);
		//拷贝一份，因为subList返回的list是原list的一个视图，外面修改会导致里面的变动
		List<T> copyList = new ArrayList<T>();
		for (T t : list) {
			copyList.add(t);
		}
		return copyList;
	}
	public static String[] str2Arr(String strWithSplit) {
		return str2Arr(strWithSplit, ",");
	}
	public static List<Integer> strArr2ListInt(String[] valueStrArr) {
		List<Integer> values=new ArrayList<Integer>();
		for (String value : valueStrArr) {
			Integer valueInt = toInt(value);
			if(valueInt!=null){
				values.add(valueInt);
			}
		}
		return values;
	}
	public static String[] str2Arr(String strWithSplit,String split) {
		return StringUtils.splitByWholeSeparator(strWithSplit, split);
	}
	public static void mapValue2SimpleObj(Map<?, Object> m) {
		for (Entry<?, Object> e : m.entrySet()) {
			e.setValue(getSimpleObj(e.getValue()));
		}
	}
	public static Set<String> genSet(String... strs) {
		Set<String> ts=new HashSet<String>();
		for (String str : strs) {
			ts.add(str);
		}
		return ts;
	}
	public static List<String> genList(String... strs) {
		List<String> ts=new ArrayList<String>();
		for (String str : strs) {
			ts.add(str);
		}
		return ts;
	}
	public static Set<Integer> genIntSet(String str) {
		Set<Integer> ts=new HashSet<Integer>();
		if(StringUtils.isBlank(str)){return ts;}
		for (String strId : str.split(",")) {
			Integer strIntId = WebUtil.toInt(strId.trim());
			if(strIntId!=null){
				ts.add(strIntId);
			}
		}
		return ts;
	}
	public static boolean isContainBlank(String ... arr) {
		for (String string : arr) {
			if (StringUtils.isBlank(string)) {
				return true;
			}
		}
		return false;
	}
	
}
