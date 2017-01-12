package com.hh.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	private static final String DEFAULT_ENCODE = "UTF-8";
	public static String inStream2String(InputStream is) throws IOException {
		return inStream2String(is,DEFAULT_ENCODE);
	}

	public static String inStream2String(InputStream is,String encode) throws IOException {
		if(encode == null){
			encode = DEFAULT_ENCODE;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toString(encode));
	}
	
	/**
	 * 
	 * @param in
	 *            文件流
	 * @param pathName
	 *            文件的绝对路径
	 * @return
	 * @throws IOException
	 */

	public static void saveFile(InputStream in, String pathName)
			throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(pathName));
			int readLen = 0;
			byte[] bytes = new byte[32 * 1024];
			while ((readLen = in.read(bytes)) != -1) {
				out.write(bytes, 0, readLen);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//检查文件是否存在
	public static boolean isFileExist(String path){
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		}
		return false;
	}
}
