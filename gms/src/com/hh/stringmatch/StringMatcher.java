package com.hh.stringmatch;

import java.util.List;

public abstract class StringMatcher {
	public static final String DEFAULT_KEY_WORD_REPLACEMENT = "*";

	public static final String DEFAULT_PRE_TAG = "<em>";

	public static final String DEFAULT_POST_TAG = "</em>";

	static final Character TERMINAL_CHAR = '$';

	static final char[] DEFAULT_OMIT_CHARS = { ' ', '\t', '\n', '\r' };

	/**
	 * @see #filterTextWithReplacer(String, boolean, String)
	 */
	public abstract boolean containKeyWord(String text, boolean omitChars);

	/**
	 * 过滤文本
	 * 
	 * @param text
	 *            需要过滤的文本
	 * @param omitChars
	 *            True表示要忽略omitCharSet中包含的字符
	 * @param replacement
	 *            用于替换匹配到的关键字的字符串
	 * @return 过滤后的文本
	 */
	public abstract String filterTextWithReplacement(String text, boolean omitChars, String replacement);

	/**
	 * 过滤文本
	 * 
	 * @param text
	 *            需要过滤的文本
	 * @param omitChars
	 *            True表示要忽略omitCharSet中包含的字符
	 * @param preTag
	 *            match后keyword前面要加的标签
	 * @param postTag
	 *            match后keyword后面要加的标签
	 * @return
	 */
	public abstract String filterTextWithHighlighting(String text, boolean omitChars, String preTag, String postTag);

	public abstract String filterText(String inputText, boolean omitChars, TextHandlerWhenMatch handler);
	
	public abstract List<String> findKeyWords(String text);
	
	public abstract String getContainKeyWord(String text, boolean omitChars);

}
