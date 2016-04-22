package com.hh.common.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinYinUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PinYinUtil.class);
	public static final char bigenCode = '^';
	public static final int transformLimit = 6;

	
	public static String transformToPingYin(char c) {
		try {
			if ((c >= '\u4E00' && c<= '\u9FA5') || c >='\uF900'&& c<='\uFA2D') {
				HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
				format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
				format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
				format.setVCharType(HanyuPinyinVCharType.WITH_V);
				String[] pinYins =PinyinHelper.toHanyuPinyinStringArray(c, format);
				if (pinYins.length > 0) {
					return pinYins[0];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		return String.valueOf(c);
	}
	
	public static String transformWordToAllPossibleInput(String w) {
		if (StringUtils.isBlank(w)) {
			return "";
		}
		char[] chars = w.trim().toCharArray();
		int limit = 0;
		List<List<String>> pinYingList = new ArrayList<List<String>>();
		for (char c : chars) {
			String pinYin = transformToPingYin(c);
			List<String> pinYings = new ArrayList<String>();
			if (pinYin.charAt(0)!= c && limit < transformLimit) {
				pinYings.add(String.valueOf(c));
				pinYings.add(pinYin);
				pinYings.add(pinYin.substring(0, 1));
				pinYings.add("");	
				limit ++;
			}else {
				pinYings.add(String.valueOf(c));
				pinYings.add("");	
			}
			pinYingList.add(pinYings);
		}
		Set<String> set = new LinkedHashSet<String>();
		takeAllPossible(pinYingList, 0, 0,false, new StringBuilder(), set);
		System.out.println(set.size());
		StringBuilder sb = new StringBuilder();
		for (String string : set) {
			sb.append(string);
		}
		return sb.toString();
	}
	
	private static void takeAllPossible(List<List<String>> pinYingList,int i,int j,boolean hasWord,StringBuilder sb,Set<String> set) {
		StringBuilder nextSb = new StringBuilder(sb.toString());
		boolean isEnd = false;//后面是否继续匹配
		if (i==0) {
			sb.append(bigenCode);
		}
		String str = pinYingList.get(i).get(j);
		if (hasWord && str.length() == 0) {
			isEnd = true;
		}
		sb.append(str);
		if (i == pinYingList.size() - 1 ) {		
			if (sb.length() > 2) {
				set.add(sb.toString());
			}
		}
		if (i== pinYingList.size() -1 && j >= pinYingList.get(i).size() -2) {
			return;
		}
		
		if ( i < pinYingList.size() -1 && !isEnd) {		
			//向后生成匹配
			takeAllPossible(pinYingList, i+1, 0,(str.length() > 0 || hasWord), sb,set);
		}
		if (j < pinYingList.get(i).size() -1) {
			//向下生成匹配
			takeAllPossible(pinYingList, i, j+1, hasWord,nextSb,set);
		}
	}
	
	public static String transformToPingYin(String word) {
		if (StringUtils.isBlank(word)) {
			return word;
		}
		StringBuilder sb = new StringBuilder();
		char[] chars = word.toCharArray();
		for (char c : chars) {
			sb.append(transformToPingYin(c));
		}
		return sb.toString();
	}
	
	//获取首字母
	public static String getInitial(String name){
        name = name.toUpperCase().trim();
       if(StringUtils.isBlank(name)){
             return "1";
        }
       char c = name.charAt(0);
       if (isChinese(c)) {
            return String.valueOf(transformToPingYin(c).charAt(0));
       }else{
           if(c>= 'A' && c<='Z'){
               return  String.valueOf(c);
           }else if(c >= '0' && c<= '9'){
               return  "1";
           }else{
               return getInitial(name.substring(1));
           }
       }
	}
  

	public static boolean isChinese(char c) {
		if ((c >= '\u4E00' && c <= '\u9FA5') || c >= '\uF900' && c <= '\uFA2D') {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(transformWordToAllPossibleInput("窗长矿光熊量黄双"));
	}
	
}
