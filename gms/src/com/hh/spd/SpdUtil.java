package com.hh.spd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hh.common.comparator.ObjComparator;
import com.hh.common.data.MapData;
import com.hh.common.utils.DateUtils;
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
		SiteMapUtil.init();
		SiteMapUtil.add("http://www.hahayx.com/xcx/", "1.0");
		for (Object obj : list) {
			MapData d = new MapData((Map)obj);
			//JadeUtil.generateStaticFile("G:/jiang/code/git2/wxyx/wxyx/jade/content.jade", String.format("/home/spd/xcx/xcx-%s.html", d.getInt("id")), d);
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
			Collections.sort(tagList, new ObjComparator("created_at", int.class, true));
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
				//JadeUtil.generateStaticFile("G:/jiang/code/git2/wxyx/wxyx/jade/list.jade", String.format("/home/spd/xcx/tag-%s-%s.html", tid,pn+1), model);
				System.out.println(String.format("http://www.hahayx.com/tag-%s-%s.html", tid,pn+1));
				SiteMapUtil.add(String.format("http://www.hahayx.com/xcx/tag-%s-%s.html", tid,pn+1),"0.8");
			}
			
		}
		try {
			MapData model = new MapData();
			model.set("pn", 1);
			model.set("tid", 0);
			model.set("tagName", tMap.get(0));
			model.set("list", all);
			model.set("tMap", tMap);
			model.set("arr",new int[]{0});
			//JadeUtil.generateStaticFile("G:/jiang/code/git2/wxyx/wxyx/jade/list.jade", "/home/spd/xcx/all.html", model);
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (MapData d : all) {
			System.out.println(String.format("http://www.hahayx.com/xcx-%s.html", d.getInt("id")));
			SiteMapUtil.add2(String.format("http://www.hahayx.com/xcx/xcx-%s.html", d.getInt("id")),"0.6");
		//	download(d);
		//	save(d);
		}
		//SiteMapUtil.create("/home/spd/sitemap.xml");
		
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
		String json = IOUtil.inStream2String(response.getEntity().getContent());
		/*
		json = json.replaceAll("image\": \"([^\"]*)/trochili/([^\"]*)", "image\": \"/trochili/$2");
		System.out.println(json);
		*/
		return WebUtil.unserialize(json);
	}
	
	private static Pattern p = Pattern.compile("image=([^,}]*)(,|})");
	private static Pattern imgp = Pattern.compile("^[\\d\\D]*/media/user_files/trochili/([^/]*)/([^/]*)/(.*)$");
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
		HttpClient httpClient = new DefaultHttpClient();;
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
	    IOUtil.saveFile(response.getEntity().getContent(), path);
	  }
	 
	 public static void save(MapData data) {
		try {
			String name = data.getString("name");
			String json = WebUtil.serialize(data);
			DbUtil.getInstance("tmp").insertIgnore(new SqlInputData("Spd").
					addInsertField("CxName", name).addInsertField("json", json));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }
	 
	 public static void generateStaticFile(String template, String destFilePath, MapData model) throws Exception {
			Writer out = null;
			File destFile = new File(destFilePath);
			if (!destFile.getParentFile().exists()){
				destFile.getParentFile().mkdirs();
			}
			try {
				String json = WebUtil.serialize(model);
				json = json.replaceAll("image\":\"([^\"]*)/trochili/([^\"]*)", "image\": \"/trochili/$2");
				model = WebUtil.unserialize(json);
				out = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
				out.write(JadeUtil.process(template, model));
				out.flush();
				out.close();
				
			} catch (Exception e) {
				if(out!=null){
					out.close();
				}
				throw e;
			}
		}
	
}
