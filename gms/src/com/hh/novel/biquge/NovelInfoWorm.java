package com.hh.novel.biquge;


import java.util.HashMap;
import java.util.Map;

import com.hh.common.data.MapData;

public class NovelInfoWorm {
	
	private static final String nameReg = "<span class=\"title\">([^<]+)</span>";
	private static final String autherReg = "<p[^<]*>作者：([^<]+)</p>";
	private static final String typeReg = "类别：([^<]+)</p>";
	private static final String statusReg = "状态：([^<]+)</p>";
	private static final String timeReg = "更新：([^<]+)</p>";
	private static final String imgReg = "<img src=\"([^\"]+\\.jpg)\"";
	private static final String infoReg = "<p class=\"review\">([\\D\\d]+?)</p>";
	
	private static final String idReg = "/(\\d+)+_(\\d+)/";
	
	private static final String idPre = "1";
	private static Map<String, Integer> typeMap = new HashMap<String, Integer>();
	static{
		typeMap.put("玄幻奇幻", 1);
		typeMap.put("武侠仙侠", 2);
		typeMap.put("都市言情", 3);
		typeMap.put("历史军事", 4);
		typeMap.put("科幻灵异", 5);
		typeMap.put("网游竞技", 6);
		typeMap.put("女生频道", 7);
	}

	public static MapData getInfo(String url) {
		try {
			MapData data = new MapData();
			String html = WormUtil.getHtml(url);
			data.set("bookName", WormUtil.getRegVal(html, nameReg, 1));
			data.set("auther", WormUtil.getRegVal(html, autherReg, 1));
			data.set("type", WormUtil.getRegVal(html, typeReg, 1));
			data.set("status", WormUtil.getRegVal(html, statusReg, 1));
			data.set("time", WormUtil.getRegVal(html, timeReg, 1));
			data.set("info", WormUtil.getRegVal(html, infoReg, 1));
			data.set("srcBookId", WormUtil.getRegVal(url, idReg, 2));
			data.set("srcId", WormUtil.getRegVal(url, idReg, 0));
			data.set("bookId", idPre+data.getInt("srcBookId"));
			data.set("img", WormUtil.getRegVal(html, imgReg, 1));	
			if (data.getString("status").contains("连")) {
				data.set("bookStatus", 1);
			}else {
				data.set("bookStatus", 2);
			}
			data.set("time", data.getString("time").replace("/", "-"));
			data.set("bookType", typeMap.get(data.getString("type").trim()));
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("novel info err :"+url + " : "+e.getMessage());
			return null;
		}
	}

	
}
