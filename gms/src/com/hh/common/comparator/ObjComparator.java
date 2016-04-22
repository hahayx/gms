package com.hh.common.comparator;

import java.util.Comparator;
import java.util.Date;

import com.hh.common.utils.ELUtil;
import com.hh.common.utils.WebUtil;


public class ObjComparator implements Comparator<Object>{
	private String elExp;//el的表达式
	private Comparator<Object> comparatorImp;
	private int res;//如果是降序会变为-1
	public ObjComparator(String field, Class<?> valueClass, boolean isDesc) {
		this(field,valueClass==int.class?IntComparator:valueClass==Date.class?DateComparator:valueClass==float.class?FloatComparator:StringComparator,isDesc);
	}
	protected ObjComparator(String field, Comparator<Object> cmp, boolean isDesc) {
		this.elExp = ELUtil.genElExp(field);//把字段变成一个el表达式
		this.comparatorImp = cmp;
		this.res = isDesc ? -1 : 1;
	}
	public int compare(Object o1, Object o2) {
		Object value1= o1==null?null:ELUtil.getValueAsJsp(o1, elExp);
		Object value2= o2==null?null:ELUtil.getValueAsJsp(o2, elExp);
		if(value1==null && value2==null) {return 0;}//保证排序稳定性
		if(value1==null){return -1*res;}
		if(value2==null){return res;}
		return comparatorImp.compare(value1, value2)* res;
	}
	private static final Comparator<Object> IntComparator= new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			return (WebUtil.toInt(o1,0) -WebUtil.toInt(o2,0));
		}
	};
	private static final Comparator<Object> DateComparator= new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			return ((Date)o1).compareTo((Date)o2);
		}
	};
	private static final Comparator<Object> StringComparator= new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			return WebUtil.getString(o1,"").compareTo(WebUtil.getString(o2,""));
		}
	};
	private static final Comparator<Object> FloatComparator= new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			return WebUtil.toFloat(o1, 0f).compareTo(WebUtil.toFloat(o2, 0f));
		}
	};
	
	public String toString() {
		return String.format("elExp=%s,desc=%s",elExp,res==-1);
	};
}