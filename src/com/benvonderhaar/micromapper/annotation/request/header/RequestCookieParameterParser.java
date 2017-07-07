package com.benvonderhaar.micromapper.annotation.request.header;

import java.util.Map;

import javax.servlet.http.Cookie;

import com.benvonderhaar.micromapper.annotation.request.ParameterParser;

public class RequestCookieParameterParser extends ParameterParser<RequestCookie> {
	
	@Override
	public Object parse(Map<String, Cookie> cookies, RequestCookie requestCookieAnnotation) {
		
		if (null != cookies.get(requestCookieAnnotation.name())) {
			// TODO cast this to appropriate type based on type declared on parameter
			return cookies.get(requestCookieAnnotation.name()).getValue();
		} else if (null != requestCookieAnnotation.fallback() 
				&& !requestCookieAnnotation.fallback().equals("")) {
			return requestCookieAnnotation.fallback();
		} else {
			throw new IllegalArgumentException("Could not find cookie with name \""
				+ requestCookieAnnotation.name() + "\" and no fallback provided.");
		}
		
	}

}
