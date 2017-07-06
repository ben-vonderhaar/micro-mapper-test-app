package com.benvonderhaar.micromapper.controller;

import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.annotation.response.header.ContentType;
import com.benvonderhaar.micromapper.annotation.response.header.SetDynamicCookie;
import com.benvonderhaar.micromapper.annotation.response.header.cookie.CookieBuilder;
import com.benvonderhaar.micromapper.annotation.response.header.cookie.DynamicCookieSetter;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class TestController extends MicroMapperController {

	@MMURLPattern(urlPattern = "/test/{}?param1={}")
	@ContentType(mediaType=ContentType.MediaType.TEXT, charset=ContentType.Charset.CP_1252)
	@SetDynamicCookie(cookieSetter=SampleDynamicCookieSetter.class)
	public String getStuff2(Long urlSegment1, Long param1) {

		System.out.println("okkk: " + urlSegment1);
		System.out.println("okkk: " + param1);
		
		return "okkk";
	}
	
	class SampleDynamicCookieSetter implements DynamicCookieSetter {

		public Cookie buildDynamicCookie(HttpServletRequest request) {
			return new CookieBuilder("sample_name", "sample_value").expiresDate(Calendar.getInstance().getTime()).build();
		}
	}
}
