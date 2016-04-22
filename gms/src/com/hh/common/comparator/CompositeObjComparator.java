package com.hh.common.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/*
 * 复合比较器,会自动组装多个比较器
 * 按顺序执行比较器，前面的比较器如果不为0立刻返回比较结果，后面比较器就没有必要比。
 * 这是一种队列优先比较器列表
 */
public class CompositeObjComparator implements Comparator<Object>{
	private List<Comparator<Object>> comparators=new ArrayList<Comparator<Object>>();
	public int compare(Object o1, Object o2) {
		int res=0;
		for (Comparator<Object> comparator : comparators) {
			res=comparator.compare(o1, o2);
			if(res!=0){
				return res;
			}
		}
		return res;
	}
	public void add(Comparator<Object> comparator) {
		comparators.add(comparator);
	}
	public boolean isEmpty() {
		return comparators.isEmpty();
	}
}