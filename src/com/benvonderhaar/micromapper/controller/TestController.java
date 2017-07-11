package com.benvonderhaar.micromapper.controller;

import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.annotation.request.header.RequestCookie;
import com.benvonderhaar.micromapper.annotation.response.header.ContentType;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookie;
import com.benvonderhaar.micromapper.annotation.response.header.cookie.CookieBuilder;
import com.benvonderhaar.micromapper.annotation.response.header.cookie.DynamicCookieSetter;
import com.benvonderhaar.micromapper.base.MicroMapperController;
import com.benvonderhaar.micromapper.test.annotation.MMControllerReference;

public class TestController extends MicroMapperController {

	@MMURLPattern(urlPattern = "/test/{}?param1={}")
	@ContentType(mediaType=ContentType.MediaType.TEXT, charset=ContentType.Charset.CP_1252)
	@SetDynamicCookie(cookieSetter=SampleDynamicCookieSetter.class)
	@MMControllerReference(referenceId = "test")
	public String testMethod(
			@RequestCookie(name = "sample_name", fallback = "fallback") String cookie, 
			Long parameter1, 
			Long parameter2) {

		System.out.println("cookie: " + cookie);
		System.out.println("parameter1: " + parameter1);
		System.out.println("parameter2: " + parameter2);
		
		return "testMethod called";
	}
	
	class SampleDynamicCookieSetter implements DynamicCookieSetter {

		public Cookie buildDynamicCookie(HttpServletRequest request) {
			return new CookieBuilder("sample_name", "sample_value2").expiresDate(Calendar.getInstance().getTime()).build();
		}
	}
}
