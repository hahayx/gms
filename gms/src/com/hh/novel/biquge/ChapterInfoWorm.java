package com.hh.novel.biquge;

import java.util.regex.Pattern;

import com.hh.common.data.MapData;

public class ChapterInfoWorm {

	private static final Pattern contentReg = Pattern.compile("<div id=\"content\">([\\D\\d]+?)<script");
	private static final Pattern nameReg = Pattern.compile("<h1>([\\D\\d]+?)</h1>");
	private static final Pattern p1 = Pattern.compile("\\w{1,3}\\.\\w{2,9}\\.\\w{2,4}");
	
	public static MapData getChapterInfo(String url) {
		try {
			MapData data = new MapData();
			String html = WormUtil.getHtml(url);
			String content = WormUtil.getRegVal(html, contentReg, 1);
			content = content.replace("readx()", "");
			content = content.replace("笔趣阁", "");
			content= p1.matcher(content).replaceAll("www.hahayx.com");
			data.set("content",content);
			data.set("chapterName", WormUtil.getRegVal(html, nameReg, 1));
			return data;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
