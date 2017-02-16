package com.hh.novel.biquge;


import java.util.HashMap;
import java.util.Map;

import com.hh.common.data.MapData;

public class NovelInfoWorm {
	
	private static final String nameReg = "<h2>([^<]+)</h2>";
	private static final String autherReg = "<p>作者：([^<]+)</p>";
	private static final String typeReg = "<p>分类：([^<]+)</p>";
	private static final String statusReg = "<p>状态：([^<]+)</p>";
	private static final String timeReg = "<p>更新：([^<]+)</p>";
	private static final String imgReg = "<div class=\"block_img2\"><img src=\"([^\"]+)\"";
	private static final String infoReg = "<div class=\"intro_info\">([\\D\\d]+?)</div>";
	
	private static final String idReg = "/(\\d+)+_(\\d+)/";
	
	private static final String idPre = "1";
	private static Map<String, Integer> typeMap = new HashMap<String, Integer>();
	static{
		typeMap.put("玄幻小说", 1);
		typeMap.put("修真小说", 2);
		typeMap.put("都市小说", 3);
		typeMap.put("历史小说", 4);
		typeMap.put("网游小说", 5);
		typeMap.put("科幻小说", 6);
		typeMap.put("言情小说", 7);
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
			data.set("img", WormUtil.getRegVal(html, imgReg, 1));
			data.set("info", WormUtil.getRegVal(html, infoReg, 1));
			data.set("srcBookId", WormUtil.getRegVal(url, idReg, 2));
			data.set("bookId", idPre+data.getInt("srcBookId"));
			
			if (data.getString("status").contains("连")) {
				data.set("bookStatus", 1);
			}else {
				data.set("bookStatus", 2);
			}

			data.set("bookType", typeMap.get(data.getString("type").trim()));
			return data;
		} catch (Exception e) {
			System.out.println("novel info err :"+url + " : "+e.getMessage());
			return null;
		}
	}

	
}
