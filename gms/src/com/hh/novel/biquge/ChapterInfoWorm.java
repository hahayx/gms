package com.hh.novel.biquge;

import com.hh.common.data.MapData;

public class ChapterInfoWorm {

	private static final String contentReg = "<div id=\"nr1\">([\\D\\d]+?)</div>";
	private static final String nameReg = "id=\"nr_title\"[^>]*>([\\D\\d]+?)</div>";
	
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
