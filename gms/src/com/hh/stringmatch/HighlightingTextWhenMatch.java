package com.hh.stringmatch;

/**
 * 
 * keyword match的时候做高亮
 */
class HighlightingTextWhenMatch implements TextHandlerWhenMatch {

	private final String preTag;
	private final String postTag;

	private int previousMatchBeginIndex;
	private int previousMatchEndIndex;
	private String previousMatchedWord;

	public HighlightingTextWhenMatch(String preTag, String postTag) {
		this.preTag = preTag;
		this.postTag = postTag;
	}

	@Override
	public boolean appendTextWhenmatch(StringBuilder filteredText, String text, int matchBeginIndex,
			int matchEndIndex) {
		String matchedWord = text.substring(matchBeginIndex, matchEndIndex);
		if (previousMatchedWord != null && matchBeginIndex <= previousMatchBeginIndex
				&& matchEndIndex > previousMatchEndIndex) {
			// matchedWord包含了previousMatchedWord
			deletePreviousHighlightingContent(filteredText, matchedWord);
		}

		filteredText.append(preTag).append(matchedWord).append(postTag);

		this.previousMatchBeginIndex = matchBeginIndex;
		this.previousMatchEndIndex = matchEndIndex;
		this.previousMatchedWord = matchedWord;
		return true;
	}

	private StringBuilder deletePreviousHighlightingContent(StringBuilder filteredText, String matchedWord) {
		int previousHighlightingContentLength = preTag.length() + previousMatchedWord.length()
				+ postTag.length();
		int filteredTextLength = filteredText.length();
		filteredText.delete(filteredTextLength - previousHighlightingContentLength, filteredTextLength);
		return filteredText;
	}

}
