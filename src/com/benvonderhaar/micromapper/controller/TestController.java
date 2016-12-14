package com.benvonderhaar.micromapper.controller;

import com.benvonderhaar.micromapper.annotation.MMURLPattern;
import com.benvonderhaar.micromapper.base.MicroMapperController;

public class TestController extends MicroMapperController {

	@MMURLPattern(urlPattern = "/test")
	public String getStuff() {
		
		System.out.println("okkk");
		
		return "okkk";
	}
}
