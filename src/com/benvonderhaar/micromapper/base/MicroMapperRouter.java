package com.benvonderhaar.micromapper.base;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.annotation.responseheader.ContentType;

public abstract class MicroMapperRouter extends HttpServlet {

	private static final long serialVersionUID = 1120156049273835083L;

	private Map<Method, MicroMapperController> controllerMappings;
	private Map<String, MicroMapperURL> controllerMethodMappings;
	
	private Map<Class<? extends Annotation>, Method> responseHeaderAnnotations;
	
	@Override
	public void init() throws ServletException {
		System.out.println("init");
		
		this.controllerMappings = new ConcurrentHashMap<Method, MicroMapperController>();
		this.controllerMethodMappings = new ConcurrentHashMap<String, MicroMapperURL>();
		
		this.responseHeaderAnnotations = new ConcurrentHashMap<Class<? extends Annotation>, Method>();
		
		for (Class<?> controller : controllerClasses()) {
			
			if (!controller.getSuperclass().equals(MicroMapperController.class)) {
				
				System.out.println(controller + " is not a MicroMapperController");
				continue;
			}
			
			for (Method method : controller.getDeclaredMethods()) {

				try {
					controllerMappings.put(method, (MicroMapperController) controller.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				for (Annotation methodAnnotation : method.getAnnotations()) {
					
					System.out.println(methodAnnotation);
					
					if (methodAnnotation instanceof MMURLPattern) {
						
						String urlPattern = ((MMURLPattern)methodAnnotation).urlPattern();
						
						controllerMethodMappings.put(urlPattern, new MicroMapperURL(urlPattern, method));
					}
					
				}
				
			}
		}
		
		// Wire up response header annotation handling
		
		for (Method method : HttpServletResponse.class.getMethods()) {
			if (method.getName().equals("setContentType")) {
				responseHeaderAnnotations.put(ContentType.class, method);
			}
		}
		
		super.init();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("doGet");
		
		String specificRequestURL = req.getRequestURI().split(req.getContextPath())[1];
		
		if (null != req.getQueryString()) {
			specificRequestURL += "?" + req.getQueryString();
		}
		
		System.out.println(specificRequestURL);
		
		for (MicroMapperURL urlPattern : controllerMethodMappings.values()) {
						
			System.out.println("URL: " + specificRequestURL);
			System.out.println("Testing Pattern: " + urlPattern.toString());
			
			if (urlPattern.matchesURL(specificRequestURL)) {
				System.out.println("we have a match");
				
				try {
				
					MicroMapperController controller = controllerMappings.get(urlPattern.getMethod());
					List<String> matchedParameters = urlPattern.getMatchingPattern(urlPattern.toString());
				
					System.out.println(urlPattern.getMethod());
					System.out.println(controller);
					System.out.println(matchedParameters);
				
					
					for (Annotation annotation : urlPattern.getMethod().getAnnotations()) {
						
						System.out.println(annotation.annotationType());
						
						if (annotation instanceof ContentType) {
							
							String contentType = ((ContentType)annotation).contentType();
							
							if (!((ContentType)annotation).charset().equals("")) {
								contentType += ";charset=" + ((ContentType)annotation).charset();
							}
							
							resp.setContentType(contentType);
						}
					}
					
					
//					resp.setContentType("text/html; charset=ISO-8859-4");
					
					// TODO automatically cast argument types 
					
					Object[] matchedParametersArray = matchedParameters.toArray();
					matchedParametersArray[0] = Long.valueOf(matchedParametersArray[0].toString());
					matchedParametersArray[1] = Long.valueOf(matchedParametersArray[1].toString());
					
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().write(urlPattern.getMethod().invoke(controller, matchedParametersArray).toString());
					resp.getWriter().flush();
					resp.getWriter().close();
			
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					// TODO handle argument type mismatch
					// TODO handle wrong number of arguments
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return;
			}
				
		}
		
		System.out.println("couldn't find mapping");
	}

	public abstract Class<?>[] controllerClasses();
}
