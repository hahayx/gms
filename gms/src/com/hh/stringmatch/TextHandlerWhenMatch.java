package com.hh.stringmatch;

public interface TextHandlerWhenMatch {
	/**
	 * 
	 * @param filteredText 匹配字符串之后结果字符串
	 * @param text  匹配的源字符串
	 * @param matchBeginIndex 匹配到text的开始索引，
	 * @param matchEndIndex   匹配到text的结束索引(不包括)
	 * @return
	 */
	public boolean appendTextWhenmatch(StringBuilder filteredText, String text, int matchBeginIndex,
			int matchEndIndex);
}
