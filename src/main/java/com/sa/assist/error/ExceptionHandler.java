package com.sa.assist.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class ExceptionHandler extends SimpleMappingExceptionResolver {
	
	private static Log m_log = LogFactory.getLog(ExceptionHandler.class);
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,  HttpServletResponse response, Object handler, Exception ex) {
		
		m_log.error(ex.getMessage(), ex);
		//TODO: do any other stuffs?
		
	
		return super.doResolveException(request, response, handler, ex);
	}
	
}
