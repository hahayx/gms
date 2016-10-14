package com.hh.common.utils;

public class Taglib {

	public static String getPageUrl(String urlPattern,Object pageNumOrOffset) {
		return String.format(urlPattern, pageNumOrOffset);
	}
	public static int getPageNum(int total,int limit) {
		return total/limit+(total%limit>0?1:0);
	}
	public static int getCurPage(int offset,int limit) {
		return offset/limit+1;
	}
	
}
