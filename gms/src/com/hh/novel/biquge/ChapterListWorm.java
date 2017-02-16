package com.hh.novel.biquge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hh.common.data.MapData;

public class ChapterListWorm {

	private static final String beginTag = "<ul class=\"chapter\">";
	private static final String endTag = "列表结束";
	private static final String urlReg = "<a href=\"(/\\d+_\\d+/\\d+.html)\">([^<]+)</a>";
	
	public static List<MapData> getChapterList(int bookId) {
		try {
			List<MapData> list = new ArrayList<MapData>();
			String url = String.format("http://m.biquge.com/booklist/%s.html", bookId);
			String html = WormUtil.getHtml(url);
			html = html.substring(html.indexOf(beginTag), html.indexOf(endTag));
			Pattern p = Pattern.compile(urlReg);
			Matcher m = p.matcher(html);
			while (m.find()) {
				MapData chapter = new MapData();
				chapter.set("url", String.format("http://m.biquge.com/%s", m.group(1)));
				chapter.set("name", m.group(2));
				list.add(chapter);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
		
	}

}
