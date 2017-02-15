package com.hh.spd;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import com.hh.common.utils.DateUtils;



public class SiteMapUtil {
	
	private static StringBuilder sb;
	
	public static void init() {
		sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:mobile=\"http://www.baidu.com/schemas/sitemap-mobile/1/\">\n");
	}
	
	public static void add(String url,String priority) {
		sb.append("<url>\n");
		sb.append("<loc>"+url+"</loc>\n");
		sb.append("<mobile:mobile type=\"pc,mobile\"/>\n");
		sb.append("<lastmod>"+DateUtils.formatDateYDM(new Date())+"</lastmod>\n");
		sb.append("<changefreq>daily</changefreq>\n");
		sb.append("<priority>"+priority+"</priority>\n");
		sb.append("</url>\n");
	}
	
	public static void add2(String url,String priority) {
		sb.append("<url>\n");
		sb.append("<loc>"+url+"</loc>\n");
		sb.append("<mobile:mobile type=\"pc,mobile\"/>\n");
		sb.append("<lastmod>"+DateUtils.formatDateYDM(new Date())+"</lastmod>\n");
		sb.append("<priority>"+priority+"</priority>\n");
		sb.append("</url>\n");
	}
	public static void create(String path) {
		try {
			sb.append("</urlset>\n");
			Writer out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
			out.write(sb.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
