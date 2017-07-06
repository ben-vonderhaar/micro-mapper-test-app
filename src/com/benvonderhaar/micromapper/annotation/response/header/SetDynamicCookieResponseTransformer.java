package com.benvonderhaar.micromapper.annotation.response.header;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benvonderhaar.micromapper.annotation.response.ResponseTransformer;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class SetDynamicCookieResponseTransformer extends ResponseTransformer<SetDynamicCookie> {

	private HttpServletRequest request;
	
	public SetDynamicCookieResponseTransformer(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void transform(HttpServletResponse response, SetDynamicCookie annotation) {
		try {
			Method buildDynamicCookieMethod = annotation.cookieSetter()
					.getDeclaredMethod("buildDynamicCookie", HttpServletRequest.class);
			buildDynamicCookieMethod.setAccessible(true);
			
			@SuppressWarnings("rawtypes")
			Constructor buildDynamicCookieConstructor = annotation.cookieSetter().getDeclaredConstructors()[0];
			buildDynamicCookieConstructor.setAccessible(true);
			
			response.addCookie((Cookie) buildDynamicCookieMethod.invoke(
					buildDynamicCookieConstructor.newInstance(new Object[] {null}), 
					this.request));
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO push these to the cookie builder
	
//	public abstract Date getExpiresDate(HttpServletResponse response);
//	public abstract Long getMaxAge(HttpServletResponse response);
//	public abstract String getDomain(HttpServletResponse response);
//	public abstract String getPath(HttpServletResponse response);
//	public abstract Boolean isSecure(HttpServletResponse response);
//	public abstract Boolean isHttpOnly(HttpServletResponse response);
//	public abstract SameSitePolicy getSameSitePolicy(HttpServletResponse response);
//	
//	// TODO map these to Strict and Lax
//	// TODO way to ignore
//	public enum SameSitePolicy {
//		STRICT, LAX
//	}
	
	
}
