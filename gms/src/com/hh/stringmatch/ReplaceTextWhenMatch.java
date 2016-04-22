package com.hh.stringmatch;

/**
 * 
 * keyword match的时候将keyword替换
 */
class ReplaceTextWhenMatch implements TextHandlerWhenMatch {

	final String keywordReplacer;

	public ReplaceTextWhenMatch(String keywordReplacer) {
		this.keywordReplacer = keywordReplacer;
	}

	@Override
	public boolean appendTextWhenmatch(StringBuilder filteredText, String text, int lastPrintIndex, int newPrintIndex) {
		filteredText.append(keywordReplacer);// 注:匹配多次(最小匹配+最大匹配)会有多个*号;
		return true;
	}

}
