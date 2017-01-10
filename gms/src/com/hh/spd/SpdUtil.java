package com.hh.spd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;


import com.hh.common.data.MapData;
import com.hh.common.utils.ELUtil;
import com.hh.common.utils.JadeUtil;
import com.hh.common.utils.WebUtil;

public class SpdUtil {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		MapData data = getData();
		System.out.println(data.get("meta"));
		List<Object> list = data.getList("objects");
		List<MapData> all = new ArrayList<MapData>();
		Map<Integer, String> tMap = new HashMap<Integer, String>();
		Map<Integer, List<MapData>> tagMap = new HashMap<Integer, List<MapData>>();
		for (Object obj : list) {
			MapData d = new MapData((Map)obj);
			JadeUtil.generateStaticFile("G:/jiang/code/git2/wxyx/wxyx/jade/content.jade", String.format("/home/spd/xcx-%s.html", d.getInt("id")), d);
			List<Object> tags = d.getList("tag");
			for (Object t : tags) {
				int tid = WebUtil.toInt(ELUtil.getValue(t, "id"));
				String name = ELUtil.getValue(t, "name").toString();
				List<MapData> list2 = tagMap.get(tid);
				if (list2 == null) {
					list2 = new ArrayList<MapData>();
					tagMap.put(tid, list2);
				}
				list2.add(d);
				tMap.put(tid, name);
			}
			all.add(d);
		}
		tMap.put(0, "");
		tagMap.put(0, all);
		for (Integer tid : tMap.keySet()) {
			List<MapData> tagList = tagMap.get(tagMap);
			int limit = 12;
			for (int pn = 0; pn*limit < tagList.size(); pn++) {
				MapData model = new MapData();
				model.set("pn", pn+1);
				model.set("tid", tid);
				model.set("tagName", tMap.get(tid));
				model.set("list", WebUtil.getCachePager(pn*limit, limit, tagList));
			}
			
		}
		
	}
	
	public static MapData getData() throws Exception {
		HttpClient httpClient = new SSLClient();
		HttpGet http = new HttpGet("https://minapp.com/api/v3/trochili/miniapp/?tag=&offset=0&limit=1000");
		HttpResponse response = httpClient.execute(http);
		System.out.println(response.getStatusLine().getStatusCode());
		return WebUtil.unserialize(response.getEntity().getContent());
	}
	
	
	
}
