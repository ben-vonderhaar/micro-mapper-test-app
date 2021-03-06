package com.benvonderhaar.micromapper.base;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.annotation.RequestMethod;
import com.benvonderhaar.micromapper.annotation.request.ParameterParser;
import com.benvonderhaar.micromapper.annotation.request.header.RequestCookie;
import com.benvonderhaar.micromapper.annotation.request.header.RequestCookieParameterParser;
import com.benvonderhaar.micromapper.annotation.response.ResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.ContentType;
import com.benvonderhaar.micromapper.annotation.response.header.ContentTypeResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookie;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookieResponseTransformer;
import com.benvonderhaar.micromapper.annotation.response.header.SetStaticCookie;
import com.benvonderhaar.micromapper.annotation.response.header.SetStaticCookieResponseTransformer;

public abstract class MicroMapperRouter extends HttpServlet {

	private static final long serialVersionUID = 1120156049273835083L;

	private Map<Method, MicroMapperController> controllerMappings;
	private Map<String, MicroMapperURL> controllerMethodMappings;

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Annotation>, ResponseTransformer> annotationResponseTransformers;
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Annotation>, ParameterParser> annotationParameterParsers;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init() throws ServletException {
		System.out.println("init");
		
		this.controllerMappings = new ConcurrentHashMap<Method, MicroMapperController>();
		this.controllerMethodMappings = new ConcurrentHashMap<String, MicroMapperURL>();
		this.annotationResponseTransformers = new ConcurrentHashMap<Class<? extends Annotation>, ResponseTransformer>();
		this.annotationParameterParsers = new ConcurrentHashMap<Class<? extends Annotation>, ParameterParser>();
		
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
		
		// Wire up custom request parameter handling
		annotationParameterParsers.put(RequestCookie.class, new RequestCookieParameterParser());
		
		super.init();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("doGet");
		
		MMMethodPayload controllerMethod = getIntendedControllerMethod(req, resp, RequestMethod.Verb.GET);
				
		try {
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(controllerMethod.getMethod().invoke(controllerMethod.getController(),
					controllerMethod.getMatchedParametersArray()).toString());
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
				
		
		System.out.println("couldn't find mapping");
	}
	
	@SuppressWarnings("unchecked")
	public MMMethodPayload getIntendedControllerMethod(HttpServletRequest req, HttpServletResponse resp, RequestMethod.Verb verb) throws ServletException {
		String specificRequestURL = req.getRequestURI().split(req.getContextPath())[1];
		
		if (null != req.getQueryString()) {
			specificRequestURL += "?" + req.getQueryString();
		}
		
		System.out.println(specificRequestURL);
		
		Map<String, Cookie> cookies = new HashMap<String, Cookie>();
		
		for (Cookie cookie : req.getCookies()) {
			cookies.put(cookie.getName(), cookie);
		}
		
		for (MicroMapperURL urlPattern : controllerMethodMappings.values()) {
						
			System.out.println("URL: " + specificRequestURL);
			System.out.println("Testing Pattern: " + urlPattern.toString());
			
			if (urlPattern.matchesURL(specificRequestURL)) {

				if (!isValidRequestMethod(urlPattern.getMethod(), verb)) {
					System.out.println("invalid verb");
					resp.setStatus(405); // TODO figure out a constant for this
					throw new ServletException();
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
					
					Object[] matchedParametersArray = new Object[urlPattern.getMethod().getParameters().length];
					Parameter[] parameters = urlPattern.getMethod().getParameters();
					int matchedParametersIndex = 0;
					
					for (int i = 0; i < parameters.length; i++) {
						
						Parameter parameter = parameters[i];
						Object matchedParameter = null;
						
						for (Annotation parameterAnnotation : parameter.getDeclaredAnnotations()) {
							
							if (null != this.annotationParameterParsers.get(parameterAnnotation.annotationType())) {
								matchedParameter = this.annotationParameterParsers.get(
										parameterAnnotation.annotationType()).parse(cookies, parameterAnnotation);
							}
							
							// TODO probably should log error if multiple annotations are found, but potentially
							// do this on initial wire-up?
							if (null != matchedParameter) {
								matchedParametersArray[i] = matchedParameter;
								break;
							}
						}
						
						if (null == matchedParameter) {
							
							if (parameter.getType().equals(String.class)) {
								matchedParametersArray[i] = 
										matchedParameters.get(matchedParametersIndex);
								matchedParametersIndex++;
							} else if (parameter.getType().equals(Long.class)) {
								matchedParametersArray[i] = 
										Long.valueOf(matchedParameters.get(matchedParametersIndex));
								matchedParametersIndex++;
							} else if (parameter.getType().equals(Integer.class)) {
								matchedParametersArray[i] = 
										Integer.valueOf(matchedParameters.get(matchedParametersIndex));
								matchedParametersIndex++;
							} else {
								//TODO handle this error properly
							}
							
						}
					}
					
					return new MMMethodPayload(urlPattern.getMethod(), controller, matchedParametersArray);
							
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
				
		}
		
		throw new ServletException("Couldn't find mapping");
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
