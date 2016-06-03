package com.hh.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hh.common.data.MapData;


public class ServletUtil {

	public static Integer getSessionInt(HttpServletRequest request,String key) { 
		return WebUtil.toInt(getSessionString(request,key));
	}
	public static Long getSessionLong(HttpServletRequest request,String key) { 
		return WebUtil.toLong(getSessionString(request,key));
	}
	public static String getSessionString(HttpServletRequest request,String key) { 
		return WebUtil.getString(getSession(request).getAttribute(key));
	}
	public static Integer getParamInt(HttpServletRequest request,String key) { 
		return WebUtil.toInt(getParamString(request, key));
	}
	public static Long getParamLong(HttpServletRequest request,String key) { 
		return WebUtil.toLong(getParamString(request, key));
	}
	public static String getParamString(HttpServletRequest request,String key) { 
		return request.getParameter(key);
	}
	public static MapData getParams(HttpServletRequest request) { 
		return new MapData(request.getParameterMap());
	}
	public static MapData getSimpleValParams(HttpServletRequest request) { 
		MapData param = getParams(request);
		WebUtil.mapValue2SimpleObj(param);;
		return param;
	}
	public static void putSession(HttpServletRequest request, String key, Object value) {
		getSession(request).setAttribute(key, value);
	}
	private static HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}
	
	public static void removeSession(HttpServletRequest request, String key) {
		getSession(request).removeAttribute(key);
	}
	public static void removeSession(HttpServletRequest request, String...keys) {
		for (String key : keys) {
			removeSession(request, key);
		}
	}
	public static Object getSessionObj(HttpServletRequest request, String key) {
		return getSession(request).getAttribute(key);
	}

	public static String getClientAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip == null) {
			return "";
		}

		// 多层代理时 IP 是用逗号隔开的
		int pos = ip.indexOf(",");
		if (pos > 0) {
			String[] ipArray = ip.split(",");
			int i = 0;
			for (i = 0; i < ipArray.length; i++) {
				String ipSub = ipArray[i].trim();
				if (!"unknown".equalsIgnoreCase(ipSub)) {
					ip = ipSub;
					break;
				}
			}
			if (i >= ipArray.length) {
				ip = ipArray[i - 1].trim();
			}
		}

		if (ip.length() <= 16) {
			return ip;
		} else {
			return ip.substring(0, 16);
		}
	}
	
	public static MapData getCookies(HttpServletRequest request) {
		MapData data = new MapData();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			data.put(cookie.getName(), cookie.getValue());
		}
		return data;
	}
	
}
