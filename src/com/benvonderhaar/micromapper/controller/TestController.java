package com.benvonderhaar.micromapper.controller;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class TestController extends MicroMapperController {

	@MMURLPattern(urlPattern = "/test?param={%s}")
	public String getStuff(String param1) {
		
		System.out.println("okkk: " + param1);
		
		return "okkk";
	}
}
