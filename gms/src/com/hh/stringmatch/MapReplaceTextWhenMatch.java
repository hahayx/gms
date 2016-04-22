package com.hh.stringmatch;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapReplaceTextWhenMatch implements TextHandlerWhenMatch {

	
	private final Map<String, String> replaceMap;
	private final Integer maxReplaceCount;
	private final boolean canMatchRepeat;//是否允许相同字符串重复匹配
	private int replaceCount;
	private Set<String> hasMatch=new HashSet<String>();
	
	public MapReplaceTextWhenMatch(Map<String, String> replaceMap) {
		this(replaceMap,null);
	}
	public MapReplaceTextWhenMatch(Map<String, String> replaceMap, Integer maxReplaceCount) {
		this(replaceMap, maxReplaceCount, false);
	}
	public MapReplaceTextWhenMatch(Map<String, String> replaceMap, Integer maxReplaceCount,boolean canMatchRepeat) {
		this.replaceMap = replaceMap;
		this.maxReplaceCount = maxReplaceCount;
		this.canMatchRepeat=canMatchRepeat;
	}
	
	@Override
	public boolean appendTextWhenmatch(StringBuilder filteredText, String text, int lastPrintIndex, int newPrintIndex) {
		String matchWord = text.substring(lastPrintIndex, newPrintIndex);
		String replacement =matchWord;
		if(canMatchRepeat||!hasMatch.contains(matchWord)){
			if(maxReplaceCount != null && replaceCount++ >= maxReplaceCount){
				filteredText.append(text.substring(lastPrintIndex));
				return false;
			}
			replacement=replaceMap.get(matchWord);
		}
		filteredText.append(replacement);// 注:匹配多次(最小匹配+最大匹配)会有多个replacement;
		hasMatch.add(matchWord);
		return true;
	}

}
