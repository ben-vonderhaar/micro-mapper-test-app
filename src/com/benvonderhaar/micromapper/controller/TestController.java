package com.benvonderhaar.micromapper.controller;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.annotation.responseheader.ContentType;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class TestController extends MicroMapperController {

	@MMURLPattern(urlPattern = "/test/{}?param1={}")
	@ContentType(contentType="text/html", charset = "ISO-8859-4")
	public String getStuff2(Long urlSegment1, Long param1) {

		System.out.println("okkk: " + urlSegment1);
		System.out.println("okkk: " + param1);
		
		return "okkk";
	}
}
