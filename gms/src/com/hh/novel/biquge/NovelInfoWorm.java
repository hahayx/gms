package com.hh.novel.biquge;


import com.hh.common.data.MapData;

public class NovelInfoWorm {
	
	private static final String nameReg = "<h2>([^<]+)</h2>";
	private static final String autherReg = "<p>作者：([^<]+)</p>";
	private static final String typeReg = "<p>分类：([^<]+)</p>";
	private static final String statusReg = "<p>状态：([^<]+)</p>";
	private static final String timeReg = "<p>更新：([^<]+)</p>";
	private static final String imgReg = "<div class=\"block_img2\"><img src=\"([^\"]+)\"";
	
	private static final String idReg = "/(\\d+)+_(\\d+)/";

	public static MapData getInfo(String url) {
		try {
			MapData data = new MapData();
			String html = WormUtil.getHtml(url);
			data.set("name", WormUtil.getRegVal(html, nameReg, 1));
			data.set("auther", WormUtil.getRegVal(html, autherReg, 1));
			data.set("type", WormUtil.getRegVal(html, typeReg, 1));
			data.set("status", WormUtil.getRegVal(html, statusReg, 1));
			data.set("time", WormUtil.getRegVal(html, timeReg, 1));
			data.set("img", WormUtil.getRegVal(html, imgReg, 1));
			data.set("bookId", WormUtil.getRegVal(url, idReg, 2));
			return data;
		} catch (Exception e) {
			return null;
		}
	}

	
}
