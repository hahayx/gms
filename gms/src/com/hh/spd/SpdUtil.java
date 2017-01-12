package com.hh.spd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import com.hh.common.data.MapData;
import com.hh.common.utils.ELUtil;
import com.hh.common.utils.IOUtil;
import com.hh.common.utils.JadeUtil;
import com.hh.common.utils.WebUtil;
import com.hh.db.DbManager;
import com.hh.db.util.DbUtil;
import com.hh.db.util.SqlInputData;

public class SpdUtil {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		DbManager.init("/tmp/config.xml");
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
		tMap.put(0, "全部");
		tagMap.put(0, all);
		for (Integer tid : tMap.keySet()) {
			List<MapData> tagList = tagMap.get(tid);
			int limit = 40;
			int[] arr = new int[1+(tagList.size()-1)/40];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = i+1;
			}
			for (int pn = 0; pn*limit < tagList.size(); pn++) {
				MapData model = new MapData();
				model.set("pn", pn+1);
				model.set("tid", tid);
				model.set("tagName", tMap.get(tid));
				model.set("list", WebUtil.getCachePager(pn*limit, limit, tagList));
				model.set("tMap", tMap);
				model.set("arr", getArr(arr, pn));
				JadeUtil.generateStaticFile("G:/jiang/code/git2/wxyx/wxyx/jade/list.jade", String.format("/home/spd/tag-%s-%s.html", tid,pn+1), model);
			}
			
		}
		for (MapData d : all) {
			download(d);
			save(d);
		}
	}
	
	private static int[] getArr(int[] arr ,int i) {
		if (arr.length <= 7) {
			return arr;
		}
		if (i <= 3) {
			int[] arr2 = new int[7];
			for (int j = 0; j < arr2.length; j++) {
				arr2[j] = arr[j];
			}
			return arr2;
		}
		if (i >= arr.length - 4) {
			int[] arr2 = new int[7];
			for (int j = 0; j < arr2.length; j++) {
				arr2[j] = arr[arr.length-7+j];
			}
			return arr2;
		}
		int[] arr2 = new int[7];
		for (int j = 0; j < arr2.length; j++) {
			arr2[j] = arr[i-3+j];
		}
		return arr2;
	}
	
	public static MapData getData() throws Exception {
		HttpClient httpClient = new SSLClient();
		HttpGet http = new HttpGet("https://minapp.com/api/v3/trochili/miniapp/?tag=&offset=0&limit=1000");
		HttpResponse response = httpClient.execute(http);
		System.out.println(response.getStatusLine().getStatusCode());
		return WebUtil.unserialize(response.getEntity().getContent());
	}
	
	private static Pattern p = Pattern.compile("image=([^,}]*)(,|})");
	private static Pattern imgp = Pattern.compile("https://sso.ifanr.com/media/user_files/trochili/([^/]*)/([^/]*)/(.*)$");
	public static void download(MapData data) {
		Matcher m = p.matcher(data.toString());
		while (m.find()) {
			try {
				String url = m.group(1);
				Matcher m2 = imgp.matcher(url);
				if (!m2.find()) {
					System.out.println("err:"+url);
					continue;
				}
				File f = new File("/home/trochili/"+m2.group(1)+"/"+m2.group(2)+"/"+m2.group(3));
				if (f.exists()) {
					continue;
				}
				new File("/home/trochili/"+m2.group(1)+"/"+m2.group(2)).mkdirs();
				download(url, f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	 public static void download(String url,String path) throws Exception{
		HttpClient httpClient = new SSLClient();
		HttpGet http = new HttpGet("https://minapp.com/api/v3/trochili/miniapp/?tag=&offset=0&limit=1000");
		HttpResponse response = httpClient.execute(http);
	    IOUtil.saveFile(response.getEntity().getContent(), path);
	  }
	 
	 public static void save(MapData data) {
		try {
			String name = data.getString("name");
			String json = WebUtil.serialize(data);
			if (json.length() < 100) {
				System.out.println(json);
			}
			DbUtil.getInstance("tmp").insertIgnore(new SqlInputData("Spd").
					addInsertField("CxName", name).addInsertField("json", json));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
	
}
