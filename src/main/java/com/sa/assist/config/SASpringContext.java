package com.sa.assist.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

//import com.sa.assist.helpers.DocumentManager;
//import com.sa.assist.helpers.EventRecurrenceManager;
//import com.sa.assist.helpers.ImageManager;
import com.sa.assist.utils.CacheHandler;

/**
 * Listener class used to make the Spring webapp context available in classes where 
 * we cannot inject the required dependencies or access the request scope
 */
public class SASpringContext implements ServletContextListener {

	public static WebApplicationContext webAppContext = null;
//	private static ImageManager imageManager;
//	private static DocumentManager documentManager;
//	private static AuthenticationManager authenticationManager;
	private static String appContext;
//	private static EventRecurrenceManager eventRecManager;
	
	

	public static String CONFIG_ROOT_PATH = "C:/config"; 
	
	/* Application Startup Event */
	public void	contextInitialized(ServletContextEvent ce) {

		//Store the applicationContext so that we can look up the correct configuration file
		setAppContext(StringUtils.substringAfter(ce.getServletContext().getContextPath(), "/"));
		
		ServletContext servletContext = ce.getServletContext();

		webAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

//		imageManager = ((ImageManager)SASpringContext.getBeanFactory().getBean("imageManager"));
		
//		String installDir = (String)servletContext.getRealPath("images");
//		installDir = StringUtils.substringBefore(installDir, ":");
//		imageManager.setImageCacheDrive(installDir);
//		
//		documentManager = ((DocumentManager)SASpringContext.getBeanFactory().getBean("documentManager"));
//		authenticationManager = (ProviderManager) SASpringContext.getBeanFactory().getBean("authenticationManager");
//		eventRecManager = ((EventRecurrenceManager) SASpringContext.getBeanFactory().getBean("eventRecurrenceManager"));
		
		
		//Initialise in-memory caches
		CacheHandler.initCaches();
	}

	/* Application Shutdown	Event */
	public void	contextDestroyed(ServletContextEvent ce) {}

	public static BeanFactory getBeanFactory(){
		return webAppContext;
	}
//
//	public static ImageManager getImageManager() {
//		return imageManager;
//	}
//
//	public static DocumentManager getDocumentManager() {
//		return documentManager;
//	}
//
//	public static AuthenticationManager getAuthenticationManager() {
//		return authenticationManager;
//	}

	public static String getAppContext() {
		return appContext;
	}

	public static void setAppContext(String appContext) {
		SASpringContext.appContext = appContext;
	}
	
//	public static EventRecurrenceManager getEventRecManager() {
//		return eventRecManager;
//	}

	
}
