package com.hh.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import com.hh.common.data.MapData;



import freemarker.template.Configuration;
import freemarker.template.Template;

public class FtlUtil {

	public static void generateStaticFile(String ftl, String destFilePath, MapData data) throws Exception {
		if(!destFilePath.startsWith("/home/site/")){
			throw new Exception("生成路径不合法");
		}
		Writer out = null;
		File destFile = new File(destFilePath);
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		File tempDestFile = new File(String.format("%s.%s.temp", destFilePath,System.currentTimeMillis()));
		try {
			out = new OutputStreamWriter(new FileOutputStream(tempDestFile), "UTF-8");
			generateStaticFile(ftl,data,out);
			out.flush();
			out.close();
			if(tempDestFile.exists()){
				FileUtils.copyFile(tempDestFile, destFile);
				removeFile(tempDestFile.getAbsolutePath());
			}
		} catch (Exception e) {
			if(out!=null){
				out.close();
				removeFile(tempDestFile.getAbsolutePath());
			}
			throw e;
		}
	}
	public static void generateStaticFile(String ftl,MapData data, Writer out) throws Exception {
		Configuration cfg = new Configuration();// 加载配置
		cfg.setDirectoryForTemplateLoading(new File(Conf.siteRealPath+"newGw/ftl"));
		Template template = cfg.getTemplate(ftl, "UTF-8");// 获取模板
		template.setNumberFormat("#");
		template.process(data, out);
	}
	
	public static boolean removeFile(String sourceFilePath) {
		File f = new File(sourceFilePath);
		if (f.exists()) {
			return f.delete();
		}
		return false;
	} 
	
	static class Conf {
		static String siteRealPath;
		static{
			File siteRealPathFile=new File(Conf.class.getResource("/").getFile());
			siteRealPath = siteRealPathFile.getAbsolutePath()
					.replace("\\", "/")
					.replace("WEB-INF/classes", "");
		}
		
	}
	
}
