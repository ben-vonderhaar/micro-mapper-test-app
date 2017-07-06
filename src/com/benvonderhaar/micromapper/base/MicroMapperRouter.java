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
import com.benvonderhaar.micromapper.annotation.RequestMethod;
import com.benvonderhaar.micromapper.annotation.response.ResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.ContentType;
import com.benvonderhaar.micromapper.annotation.response.header.ContentTypeResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookie;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookieResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.SetStaticCookie;
import com.benvonderhaar.micromapper.annotation.response.header.SetStaticCookieResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.cookie.DynamicCookieSetter;

public abstract class MicroMapperRouter extends HttpServlet {

	private static final long serialVersionUID = 1120156049273835083L;

	private Map<Method, MicroMapperController> controllerMappings;
	private Map<String, MicroMapperURL> controllerMethodMappings;

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Annotation>, ResponseTransformer> annotationResponseTransformers;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init() throws ServletException {
		System.out.println("init");
		
		this.controllerMappings = new ConcurrentHashMap<Method, MicroMapperController>();
		this.controllerMethodMappings = new ConcurrentHashMap<String, MicroMapperURL>();
		this.annotationResponseTransformers = new ConcurrentHashMap<Class<? extends Annotation>, ResponseTransformer>();
		
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
				
				System.out.println(method.getAnnotations());
				
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
		annotationResponseTransformers.put(ContentType.class, new ContentTypeResponseTransformer());
		annotationResponseTransformers.put(SetStaticCookie.class, new SetStaticCookieResponseTransformer());
		
		super.init();
	}

	@SuppressWarnings("unchecked")
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

				if (!isValidRequestMethod(urlPattern.getMethod(), RequestMethod.Verb.GET)) {
					System.out.println("invalid verb");
					return;
				}
				
				try {
				
					MicroMapperController controller = controllerMappings.get(urlPattern.getMethod());
					
					List<String> matchedParameters = urlPattern.getMatchingPattern(urlPattern.toString());
				
					System.out.println(urlPattern.getMethod());
					System.out.println(controller);
					System.out.println(matchedParameters);
				
					System.out.println(this.annotationResponseTransformers);
					
					for (Annotation annotation : urlPattern.getMethod().getAnnotations()) {
												
						System.out.println(annotation);
						
						if (null != this.annotationResponseTransformers.get(annotation.annotationType())) {
							this.annotationResponseTransformers.get(annotation.annotationType()).transform(resp, annotation);
						} else if (annotation.annotationType().equals(SetDynamicCookie.class)) {
							// Dynamic cookies are applied using a class provided as an annotation parameter so the
							// transformer must be called after interrogating the annotation.
							new SetDynamicCookieResponseTransformer(req).transform(resp, ((SetDynamicCookie) annotation));
						}
					}
					
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
	
	private boolean isValidRequestMethod(Method controllerMethod, RequestMethod.Verb verb) {
		
		// If no Request Method(s) specified on the controller method, no need to check validity of the verb.
		if (null == controllerMethod.getAnnotation(RequestMethod.class)) {
			return true;
		}
		
		for (RequestMethod.Verb validVerb : controllerMethod.getAnnotation(RequestMethod.class).verbs()) {
			if (validVerb.equals(verb)) {
				return true;
			}
		}
		
		return false;
			
	}

	/**
	 * 
	 * Provides context for Micro Mapper to determine which classes should be parsed as Controllers.
	 * These classes must inherit from MicroMapperController, and any public non-static method that
	 * is annotated with MicroMapperURL will be tried against all incoming requests to attempt to match
	 * the request URL with the provided pattern(s).
	 * 
	 * @return a list of classes that should be treated as Controllers 
	 */
	public abstract Class<?>[] controllerClasses();
}
