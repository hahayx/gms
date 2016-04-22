package com.hh.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;



public class ZipUtil {
	@SuppressWarnings("unchecked")
	public static Map<String, byte[]> unZip(File source) throws IOException {
		Map<String, byte[]> fileMap = new HashMap<String, byte[]>();
		ZipFile zip=new ZipFile(source,"GBK");
		Enumeration<ZipEntry> enumeration = zip.getEntries();
		ZipEntry e;
		 while (enumeration.hasMoreElements()) {
		    e = (ZipEntry) enumeration.nextElement();
			if (!e.isDirectory()) {
				fileMap.put(e.getName(), IOUtils.toByteArray(zip.getInputStream(e)));
			}
		}
		return fileMap;
	}

}
