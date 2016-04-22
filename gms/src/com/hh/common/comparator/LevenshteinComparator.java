package com.hh.common.comparator;

import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;

import com.hh.common.utils.WebUtil;


/**
 * 相关度比较器，比较两个字符的编辑距离，编辑距离越小表示越相似
 * 
 * 编辑距离，又称Levenshtein距离
 */
public class LevenshteinComparator implements Comparator<Object>{
	private String targetStr;
	private LevenshteinComparator(String targetStr) {
		this.targetStr=targetStr;
	}

	/**
	 * 
	 * @param field	
	 * @param targetStr 进行相关度判别的目标字符串
	 * @param isDesc 当idDesc = false时，表示按相关度从近到远
	 * @return
	 */
	public static ObjComparator getInstance(String field,String targetStr, boolean isDesc) {
		return new ObjComparator(field, new LevenshteinComparator(targetStr), isDesc);
		
	}
	
	public int compare(Object o1, Object o2) {
		int length1 = StringUtils.getLevenshteinDistance(WebUtil.getString(o1,""), targetStr);
		int length2 = StringUtils.getLevenshteinDistance(WebUtil.getString(o2,""),targetStr);
		return length1 - length2;
	}

}
