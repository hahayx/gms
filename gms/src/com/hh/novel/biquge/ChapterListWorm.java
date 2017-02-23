package com.hh.novel.biquge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hh.common.data.MapData;

public class ChapterListWorm {

	private static final String beginTag = "chapterlist";
	private static final String endTag = "Readpage";
	private static final String urlReg = "<a[^>]*href=\"(/\\d+_\\d+/\\d+.html)\">([^<]+)</a>";
	
	public static List<MapData> getChapterList(String srcId) {
		try {
			List<MapData> list = new ArrayList<MapData>();
			String url = String.format("http://m.biquge.tw%sall.html", srcId);
			String html = WormUtil.getHtml(url);
			html = html.substring(html.indexOf(beginTag), html.indexOf(endTag));
			Pattern p = Pattern.compile(urlReg);
			Matcher m = p.matcher(html);
			while (m.find()) {
				MapData chapter = new MapData();
				chapter.set("url", String.format("http://www.biquge.tw/%s", m.group(1)));
				chapter.set("name", m.group(2));
				list.add(chapter);
			}
			return list;
		} catch (Exception e) {
			System.out.println(srcId);
			e.printStackTrace();
			return Collections.emptyList();
		}
		
	}

}
