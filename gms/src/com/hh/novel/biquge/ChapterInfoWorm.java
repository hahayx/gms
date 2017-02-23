package com.hh.novel.biquge;

import com.hh.common.data.MapData;

public class ChapterInfoWorm {

	private static final String contentReg = "<div id=\"content\">([\\D\\d]+?)<script";
	private static final String nameReg = "<h1>([\\D\\d]+?)</h1>";
	
	public static MapData getChapterInfo(String url) {
		try {
			MapData data = new MapData();
			String html = WormUtil.getHtml(url);
			data.set("content", WormUtil.getRegVal(html, contentReg, 1));
			data.set("chapterName", WormUtil.getRegVal(html, nameReg, 1));
			return data;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
