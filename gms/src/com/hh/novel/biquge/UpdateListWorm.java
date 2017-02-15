package com.hh.novel.biquge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class UpdateListWorm {
	
	private static final String url ="http://www.biquge.com";
	private static final String beginTag = "最近更新小说列表";
	private static final String endTag = "最新入库小说";
	private static final String urlReg = "<a href=\"/((\\d+)_(\\d+))/\"";
	
	public static List<String> getUpdateList()  {
		try {
			List<String> list = new ArrayList<String>();
			String html = WormUtil.getHtml(url);
			html = html.substring(html.indexOf(beginTag), html.indexOf(endTag));
			Pattern p = Pattern.compile(urlReg);
			Matcher m = p.matcher(html);
			while (m.find()) {
				list.add(String.format("http://m.biquge.com/%s/", m.group(1)));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	
}
