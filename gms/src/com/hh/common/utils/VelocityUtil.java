package com.hh.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.hh.common.data.MapData;

public class VelocityUtil {

	public static void generateStaticFile(String template, String destFilePath, MapData model) throws Exception {
		generateStaticFile(createTemplate(template), destFilePath, model);
	}
	
	public static void generateStaticFile(Template template, String destFilePath, MapData model) throws Exception {
		File destFile = new File(destFilePath);
		FileOutputStream fos = null;
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		try {
			fos = new FileOutputStream(destFile);
			VelocityContext context = new VelocityContext(model);		
			StringWriter writer = new StringWriter(); 
			PrintStream ps = new PrintStream(new FileOutputStream(destFile), true, "utf-8");
			template.merge(context, writer);
			ps.print(writer.toString());
	        ps.flush();
	        ps.close();
	        fos.close();  
			fos = null;
		}finally{
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	public static Template createTemplate(String template) {
		File tempFile = new File(template);
		VelocityEngine ve = new VelocityEngine();
		Properties properties = new Properties();
		properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, tempFile.getParent()); //此处的fileDir可以直接用绝对路径来
		properties.setProperty(Velocity.INPUT_ENCODING, "utf-8");
		properties.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");
		properties.setProperty(Velocity.ENCODING_DEFAULT, "utf-8");
		ve.init(properties);   //初始化
		return ve.getTemplate(tempFile.getName());	
	}
	
	public static void main(String[] args) throws Exception {
		MapData model = new MapData();
		model.set("name", "aaaa");
		model.set("aa", new MapData().set("bb", "cc"));
		generateStaticFile("/home/test.vm", "/home/test.txt", model);
	
	}
	
}
