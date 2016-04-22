package com.hh.stringmatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.hh.common.utils.WebUtil;


public class ACAutomationStringMatcher extends StringMatcher {
	private ACAutomation acAutomation = new ACAutomation();
	private final Set<Character> omitCharSet;

	/**
	 * Builds an trie tree with the given dictionary words.
	 * 
	 * @param dictionaryWords
	 * @param omitCharSet
	 */
	public ACAutomationStringMatcher(Collection<String> dictionaryWords, Set<Character> omitCharSet) {
		omitCharSet.remove(TERMINAL_CHAR);
		for (char c : DEFAULT_OMIT_CHARS) {
			omitCharSet.add(c);
		}
		this.omitCharSet = Collections.unmodifiableSet(omitCharSet);
		this.acAutomation.buildACAutomation(dictionaryWords, this.omitCharSet);
	}

	public ACAutomationStringMatcher(Collection<String> dictionaryWords) {
		this.omitCharSet = new HashSet<Character>();
		this.acAutomation.buildACAutomation(dictionaryWords, this.omitCharSet);
	}

	private static final class FontChangeInit {
		private static final Map<Character, Character> ft2jt=new HashMap<Character, Character>() ;
		static{
			try {
				for (Entry<String, Object> e : WebUtil.unserialize(FontChangeInit.class.getResourceAsStream("/com/hh/resource/ft2jt.sl")).entrySet()) {
					ft2jt.put(e.getKey().charAt(0), e.getValue().toString().charAt(0));
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(FontChangeInit.class).error("",e);
			}
		}
	}
	//全角转换为半角,繁体转简体(这些功能都不抽先，目测别的地方暂时不需要用，有用再抽吧)
	private static final String transferToNormalChar(String src) {  
		char[] cs = src.toCharArray();
        for (int index = 0; index < cs.length; index++) {
            if (cs[index] == 12288) {// 全角空格
                cs[index] = (char) 32;
            } else if (cs[index] > 65280 && cs[index] < 65375) {// 其他全角字符
                cs[index] = (char) (cs[index] - 65248);
            }else{//繁体-》简体
            	Character jtC = FontChangeInit.ft2jt.get(cs[index]);
    			if(jtC!=null){
    				cs[index]=jtC;
    			}
            }
        }
        return String.valueOf(cs);
    } 
	
	@Override
	public boolean containKeyWord(String text, boolean omitChars) {
		if (StringUtils.isBlank(text)) {
			return false;
		}
		text=transferToNormalChar(text);//先把要处理的字符串转换为半角与简体,这样词库就可以只有半角字符与简体
		int index = 0;
		ACTrieTreeNode curState = acAutomation.root;

		while (index < text.length()) {
			char c = Character.toLowerCase(text.charAt(index));
			if (omitChars && omitCharSet.contains(c)) {
				++index;
				continue;
			}

			/**
			 * 在AC自动机上查找串:匹配过程分两种情况： (1)当前字符匹配， 表示从当前节点沿着树边有一条路径可以到达目标字符
			 * ，此时只需沿该路径走向下一个节点继续匹配即可，目标字符串指针移向下个字符继续匹配；
			 * (2)当前字符不匹配，则去当前节点失败指针所指向的字符继续匹配，匹配过程随着指针指向root结束。
			 */
			ACTrieTreeNode node = null;
			while (node == null) {
				node = curState.getChildNode(c);

				if (node == null) { // dismatch
					if (curState == acAutomation.root) {
						break;
					} else {
						curState = curState.fail;
						if (curState.isWord) {// fail之后match
							return true;
						}
					}
				}
			}

			if (node != null) {// match
				curState = node;
				if (curState.isWord) {// 终结状态
					return true;
				}
			}
			++index;
		}
		return false;
	}

	@Override
	public String getContainKeyWord(String text, boolean omitChars) {
		if (StringUtils.isBlank(text)) {
			return null;
		}

		int index = 0;
		ACTrieTreeNode curState = acAutomation.root;

		while (index < text.length()) {
			char c = Character.toLowerCase(text.charAt(index));
			if (omitChars && omitCharSet.contains(c)) {
				++index;
				continue;
			}

			/**
			 * 在AC自动机上查找串:匹配过程分两种情况： (1)当前字符匹配， 表示从当前节点沿着树边有一条路径可以到达目标字符
			 * ，此时只需沿该路径走向下一个节点继续匹配即可，目标字符串指针移向下个字符继续匹配；
			 * (2)当前字符不匹配，则去当前节点失败指针所指向的字符继续匹配，匹配过程随着指针指向root结束。
			 */
			ACTrieTreeNode node = null;
			while (node == null) {
				node = curState.getChildNode(c);

				if (node == null) { // dismatch
					if (curState == acAutomation.root) {
						break;
					} else {
						curState = curState.fail;
						if (curState.isWord) {// fail之后match
							return getMatcherKey(curState);
						}
					}
				}
			}

			if (node != null) {// match
				curState = node;
				if (curState.isWord) {// 终结状态
					return getMatcherKey(curState);
				}
			}
			++index;
		}
		return null;
	}
	
	//通过匹配节点往父节点查找，获得匹配脏字
	private String getMatcherKey(ACTrieTreeNode node){
		char[] arr = new char[node.height]; 
		while (node.height != 0) {
			arr[node.height - 1] = node.character;
			node = node.parentNodes;
		}
		return String.valueOf(arr);
	}
	
	
	@Override
	public String filterTextWithReplacement(String inputText, boolean omitChars, String replacer) {
		return filterText(inputText, omitChars, new ReplaceTextWhenMatch(replacer));
	}

	/**
	 * 必须begin >= end, end<=text.length()内部不验证
	 * 
	 * @param text
	 * @param begin
	 *            Inclusive
	 * @param end
	 *            Inclusive
	 * @return
	 */
	private int getCharCountWithOmitChar(String text, int begin, int end, boolean omitChars) {
		// begin and end are all inclusive index
		int count = 0;
		while (begin <= end) {
			if (!omitChars || !omitCharSet.contains(text.charAt(begin))) {
				++count;
			}
			++begin;
		}
		return count;
	}

	private int backwardCharIndexWithOmitChars(String text, int index, boolean omitChars) {
		while (0 <= --index) {
			if (!omitChars || !omitCharSet.contains(text.charAt(index))) {
				break;
			}
		}
		return index;
	}

	private int appendNextCharWithOmitChar(String text, int index, StringBuilder filteredText,
			boolean omitChars) {
		while (index < text.length()) {
			char c = text.charAt(index);
			if (omitChars && omitCharSet.contains(c)) {
				filteredText.append(c);
			} else {
				break;
			}
			++index;
		}
		filteredText.append(text.charAt(index++));
		return index;
	}

	@Override
	public List<String> findKeyWords(String text) {
		List<String> keyWords = new ArrayList<String>();
		if (StringUtils.isBlank(text)) {
			return keyWords;
		}

		int printIndex = 0;
		int index = 0;
		int carry = 0;// 进位,用来表示被替换成*号的字符数
		ACTrieTreeNode curState = acAutomation.root;

		while (index < text.length()) {
			char c = Character.toLowerCase(text.charAt(index));

			/**
			 * 在AC自动机上查找串:匹配过程分两种情况： (1)当前字符匹配， 表示从当前节点沿着树边有一条路径可以到达目标字符
			 * ，此时只需沿该路径走向下一个节点继续匹配即可，目标字符串指针移向下个字符继续匹配；
			 * (2)当前字符不匹配，则去当前节点失败指针所指向的字符继续匹配，匹配过程随着指针指向root结束。
			 */
			ACTrieTreeNode node = null;
			while (node == null) {
				node = curState.getChildNode(c);

				if (node == null) { // dismatch
					if (curState == acAutomation.root) {
						// filteredText.append(text.charAt(printIndex++));
						++printIndex;
						break;
					} else {
						// curState.height - curState.fail.height就是跳过的字符数,类似KMP
						int loopSize = curState.height - curState.fail.height + carry;
						carry = (loopSize) >= 0 ? 0 : loopSize;
						// for (int j = 0; j < loopSize; ++j) {
						// filteredText.append(text.charAt(printIndex++));
						// }
						if (loopSize > 0) {
							printIndex += loopSize;
						}
						curState = curState.fail;

						if (curState.isWord && printIndex < index) {// fail之后match
							// filteredText.append(replacer);
							keyWords.add(text.substring(index - curState.height, index));
							// 匹配多次(最小匹配，最大匹配)会有多个*号
							carry -= index - printIndex;
							printIndex = index;
						}
					}
				}
			}

			if (node != null) {// match
				curState = node;
				if (curState.isWord) {// 终结状态
					// filteredText.append(replacer);// 匹配多次(最小匹配，最大匹配)会有多个*号
					keyWords.add(text.substring(index - curState.height + 1, index + 1));
					carry -= index - printIndex + 1;
					printIndex = index + 1;
				}
			}
			++index;
		}

		return keyWords;
	}

	@Override
	public String filterText(String inputText, boolean omitChars, TextHandlerWhenMatch handler) {
		if (StringUtils.isBlank(inputText)) {
			return inputText;
		}

		String text = inputText + TERMINAL_CHAR;// 加终结符$,这样是为了text最后的omitchars能正确输出到filteredText中
		StringBuilder filteredText = new StringBuilder();
		int printIndex = 0;
		int index = 0;
		int carry = 0;// 进位,用来表示被替换成*号的字符数
		ACTrieTreeNode curState = acAutomation.root;

		while (index < text.length()) {
			char c = Character.toLowerCase(text.charAt(index));
			if (omitChars && omitCharSet.contains(c)) {
				++index;
				continue;
			}

			/**
			 * 在AC自动机上查找串:匹配过程分两种情况： (1)当前字符匹配， 表示从当前节点沿着树边有一条路径可以到达目标字符
			 * ，此时只需沿该路径走向下一个节点继续匹配即可，目标字符串指针移向下个字符继续匹配；
			 * (2)当前字符不匹配，则去当前节点失败指针所指向的字符继续匹配，匹配过程随着指针指向root结束。
			 */
			ACTrieTreeNode node = null;
			while (node == null) {
				node = curState.getChildNode(c);

				if (node == null) { // dismatch
					if (curState == acAutomation.root) {
						// filteredText.append(text.charAt(printIndex++));
						printIndex = appendNextCharWithOmitChar(text, printIndex, filteredText, omitChars);
						break;
					} else {
						// curState.height - curState.fail.height就是跳过的字符数,类似KMP
						int loopSize = curState.height - curState.fail.height + carry;
						carry = (loopSize) >= 0 ? 0 : loopSize;
						for (int j = 0; j < loopSize; ++j) {
							// filteredText.append(text.charAt(printIndex++));
							printIndex = appendNextCharWithOmitChar(text, printIndex, filteredText, omitChars);
						}
						curState = curState.fail;

						// if (curState.isWord && printIndex < index) {//
						// fail之后match
						int lastCharIndex = backwardCharIndexWithOmitChars(text, index, omitChars);
						if (curState.isWord && printIndex <= lastCharIndex) {// fail之后match
							carry -= getCharCountWithOmitChar(text, printIndex, lastCharIndex, omitChars);
							int newPrintIndex = lastCharIndex + 1;

							if (newPrintIndex - printIndex < curState.height) {
								printIndex = newPrintIndex - curState.height;
							}
							if (!handler.appendTextWhenmatch(filteredText, text, printIndex, newPrintIndex)) {
								return getReturnText(filteredText);
							}
							printIndex = newPrintIndex;

						}
					}
				}
			}

			if (node != null) {// match
				curState = node;
				if (curState.isWord) {// 终结状态
					carry -= getCharCountWithOmitChar(text, printIndex, index, omitChars);
					int newPrintIndex = index + 1;
					if (newPrintIndex - printIndex < curState.height) {
						printIndex = newPrintIndex - curState.height;
					}
					if (!handler.appendTextWhenmatch(filteredText, text, printIndex, newPrintIndex)) {
						return getReturnText(filteredText);
					}
					printIndex = newPrintIndex;
				}
			}
			++index;
		}
		return getReturnText(filteredText);
	}

	private String getReturnText(StringBuilder filteredText) {
		return filteredText.toString().substring(0, filteredText.length() - 1);// 去掉最后的$
	}

	@Override
	public String filterTextWithHighlighting(String text, boolean omitChars, String preTag, String postTag) {
		return filterText(text, omitChars, new HighlightingTextWhenMatch(preTag, postTag));
	}

}
