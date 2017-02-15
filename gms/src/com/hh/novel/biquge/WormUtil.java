package com.hh.novel.biquge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hh.common.utils.IOUtil;

public class WormUtil {

	public static String getHtml(String url) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
		return IOUtil.inStream2String(response.getEntity().getContent());
	}
	
	public static String getRegVal(String html,String reg,int group){
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(html);
		if (m.find()) {
			return m.group(group);
		}
		return null;
	}
	
	 public static void download(String url,String path) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();;
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
	    IOUtil.saveFile(response.getEntity().getContent(), path);
	 }
}
