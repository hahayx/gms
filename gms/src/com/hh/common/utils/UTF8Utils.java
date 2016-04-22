package com.hh.common.utils;

public class UTF8Utils {

	public static String stripNonCharCodepoints(String input) {
		if (input == null) {
			return null;
		}

		StringBuilder retval = new StringBuilder();
		char ch;

		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step
											// 0x10000
					ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
					(ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
					(ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd)) {

				retval.append(ch);
			}
		}
		return filterNot2ByteUtf8Char(retval.toString());
	}
	public static String filterNot2ByteUtf8Char(String content) {
		byte[] bytes = content.getBytes();
		byte[] newBytes=new byte[bytes.length];
		int newByteIndex=0;
		for (int i=0,len=bytes.length;i<len;) {
			byte b=bytes[i];
			if(b>=(byte)0xf0 && b<=(byte)0xf7){//四字节
				i=i+4;
			}else{
				i++;
				newBytes[newByteIndex++]=b;
			}
		}
		return newByteIndex==0?"":new String(newBytes,0,newByteIndex);
	}

}
