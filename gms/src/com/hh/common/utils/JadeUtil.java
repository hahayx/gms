package com.hh.common.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.hh.common.data.MapData;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.template.JadeTemplate;

public class JadeUtil {
	
	public static void generateStaticFile(String template, String destFilePath, MapData model) throws Exception {
		Writer out = null;
		File destFile = new File(destFilePath);
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		try {
			out = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
			out.write(process(template, model));
			out.flush();
			out.close();
			
		} catch (Exception e) {
			if(out!=null){
				out.close();
			}
			throw e;
		}
	}
	
	public static String process(String template,MapData model) throws Exception {
		JadeTemplate temp = Jade4J.getTemplate(template);
		return Jade4J.render(temp, model);
	}
	
}
