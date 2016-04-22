package com.hh.common.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class MappingJacksonJsonpView extends MappingJacksonJsonView{

	private static final String DEFAULT_CONTENT_TYPE ="application/javascript";
	
	@Override 
	public String getContentType(){
		return DEFAULT_CONTENT_TYPE;
	}
	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if("get".equalsIgnoreCase(request.getMethod())){
			if(request.getParameterMap().containsKey("callback")){
				StringBuilder sb = new StringBuilder(request.getParameter("callback")).append("(");
				response.getOutputStream().write(sb.toString().getBytes());
				super.render(model, request, response);
				response.getOutputStream().write(");".getBytes());
				return;
			}
		}
		super.render(model, request, response);
	}
}
