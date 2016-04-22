package com.hh.common.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

public class ExceptionResolver extends DefaultHandlerExceptionResolver{

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResolver.class);
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
		
		LOGGER.error(ex.getMessage(),ex);
		
		return super.doResolveException(request, response, handler, ex);
	}
	
}
