package com.hh.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.hh.common.data.MapData;

public class VelocityUtil {

	public static void generateStaticFile(String template, String destFilePath, MapData model) throws Exception {
		Writer out = null;
		File destFile = new File(destFilePath);
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		try {
			File tempFile = new File(template);
			VelocityEngine ve = new VelocityEngine();
			Properties properties = new Properties();
			properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, tempFile.getParent()); //此处的fileDir可以直接用绝对路径来
			ve.init(properties);   //初始化
			out = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
			VelocityContext context = new VelocityContext(model);
			Template temp = ve.getTemplate(tempFile.getName());
			temp.merge(context, out);
			out.flush();
			out.close();
			
		} catch (Exception e) {
			if(out!=null){
				out.close();
			}
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		MapData model = new MapData();
		model.set("name", "aaaa");
		model.set("aa", new MapData().set("bb", "cc"));
		generateStaticFile("/home/test.vm", "/home/test.txt", model);
	
	}
	
}
