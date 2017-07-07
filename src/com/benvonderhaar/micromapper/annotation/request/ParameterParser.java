package com.benvonderhaar.micromapper.annotation.request;

import java.util.Map;

import javax.servlet.http.Cookie;

public abstract class ParameterParser<T> {

	public abstract Object parse(Map<String, Cookie> cookies, T parameterParser);
	
}
