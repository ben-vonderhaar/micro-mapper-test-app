package com.benvonderhaar.micromapper.servlet;

import com.benvonderhaar.micromapper.base.MicroMapperRouter;
import com.benvonderhaar.micromapper.controller.TestController;

public class MicroMapper extends MicroMapperRouter {

	private static final long serialVersionUID = 4390834015157053486L;

	@Override
	public Class<?>[] controllerClasses() {
		return new Class<?>[] {TestController.class};
	}	
}
