package com.benvonderhaar.micromapper.controller;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class TestController extends MicroMapperController {

	//http://localhost:8080/micro-mapper-test-app/test/VALUE1/test/VALUE2/test/VALUE2.5?param1=VALUE3&param2=VALUE4
	
	// Both should fail to match for /test/VALUE1/test/VALUE2
	
	@MMURLPattern(urlPattern = "/test/{}")
//	@MMURLPattern(urlPattern = "/test/{}/test/{}?param1={}&param2={}")
	public String getStuff(Long urlSegment1, String urlSegment2, String param1, Long param2) {

		System.out.println("okkk: " + urlSegment1);
		System.out.println("okkk: " + urlSegment2);
		System.out.println("okkk: " + param1);
		System.out.println("okkk: " + param2);
		
		return "okkk";
	}
	
	@MMURLPattern(urlPattern = "/{}/test/{}")
//	@MMURLPattern(urlPattern = "/{}/test/{}/test/{}?param1={}")
	public String getStuff2(Long urlSegment1, String urlSegment2, String urlSegment3, Long param1) {

		System.out.println("okkk: " + urlSegment1);
		System.out.println("okkk: " + urlSegment2);
		System.out.println("okkk: " + urlSegment3);
		System.out.println("okkk: " + param1);
		
		return "okkk";
	}
	
	@MMURLPattern(urlPattern = "/{}/test/{}/{}")
//	@MMURLPattern(urlPattern = "/{}/test/{}/test/{}?param1={}")
	public String getStuff3(Long urlSegment1, String urlSegment2, String urlSegment3, Long param1) {

		System.out.println("okkk: " + urlSegment1);
		System.out.println("okkk: " + urlSegment2);
		System.out.println("okkk: " + urlSegment3);
		System.out.println("okkk: " + param1);
		
		return "okkk";
	}
}
