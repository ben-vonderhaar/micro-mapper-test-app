package com.benvonderhaar.micromapper.base;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;

public abstract class MicroMapperRouter extends HttpServlet {

	private static final long serialVersionUID = 1120156049273835083L;

	private Map<Method, MicroMapperController> controllerMappings;
	private Map<MicroMapperURL, Method> controllerMethodMappings;
	
	@Override
	public void init() throws ServletException {
		System.out.println("init");
		
		controllerMappings = new ConcurrentHashMap<Method, MicroMapperController>();
		controllerMethodMappings = new ConcurrentHashMap<MicroMapperURL, Method>();
		
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
						controllerMethodMappings.put(new MicroMapperURL(((MMURLPattern)methodAnnotation).urlPattern()), method);
					}
					
				}
				
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
		
		for (MicroMapperURL urlPattern : controllerMethodMappings.keySet()) {
			
			System.out.println("URL: " + specificRequestURL);
			System.out.println("Testing Pattern: " + urlPattern.toString());
			
			if (urlPattern.matchesURL(specificRequestURL)) {
				System.out.println("we have a match");
				
				// TODO if match found, attempt to map results to 
			}
		}
		
		if (null != controllerMethodMappings.get(specificRequestURL)) {
			try {
				Method methodToInvoke = controllerMethodMappings.get(specificRequestURL);

				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(methodToInvoke.invoke(controllerMappings.get(methodToInvoke)).toString());
				resp.getWriter().flush();
				resp.getWriter().close();
				
						
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("couldn't find mapping");
		}
	}

	public abstract Class<?>[] controllerClasses();
}
