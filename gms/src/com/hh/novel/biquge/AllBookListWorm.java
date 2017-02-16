package com.hh.novel.biquge;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllBookListWorm {

	private static final String urlReg = "<div class=\"bookimg\"><a href=\"/((\\d+)_(\\d+))/\">";
	
	public static List<String> allUrlList() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 7; i++) {
			for (int j = 1; j <= 10; j++) {
				try {
					String url = String.format("http://m.biquge.com/fenlei%s_%s.html", i,j);
					String html = WormUtil.getHtml(url);
					Pattern p = Pattern.compile(urlReg);
					Matcher m = p.matcher(html);
					while (m.find()) {
						list.add(String.format("http://m.biquge.com/%s/", m.group(1)));
					}	
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		return list;
	}
	
}
