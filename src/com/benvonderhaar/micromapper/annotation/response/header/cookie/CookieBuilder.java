package com.benvonderhaar.micromapper.annotation.response.header.cookie;

import java.util.Date;

import javax.servlet.http.Cookie;

public class CookieBuilder {

	private String name, value;
	private Date expiresDate;
	
	public CookieBuilder(String name, String value) {
		this.name = name;
		this.value = value;
		this.expiresDate = null;
	}
	
	public CookieBuilder expiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
		return this;
	}
	
	// TODO expiresDate is not available in javax.servlet.http.Cookie, this should produce the header value instead of a Cookie object
	public Cookie build() {
		
		System.out.println("build cookie");
		
		Cookie cookie = new Cookie(this.name, this.value);
		
		
		return cookie;
	}
	
}
