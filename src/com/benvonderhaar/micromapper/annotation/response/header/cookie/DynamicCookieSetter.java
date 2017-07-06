package com.benvonderhaar.micromapper.annotation.response.header.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public interface DynamicCookieSetter {
	
	public Cookie buildDynamicCookie(HttpServletRequest request);
}