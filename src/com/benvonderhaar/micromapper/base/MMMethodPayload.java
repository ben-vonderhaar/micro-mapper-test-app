package com.benvonderhaar.micromapper.base;

import java.lang.reflect.Method;

public class MMMethodPayload {

	private Method method;
	private MicroMapperController controller;
	private Object[] matchedParametersArray;
	
	public MMMethodPayload(Method method, MicroMapperController controller, Object[] matchedParametersArray) {
		this.method = method;
		this.controller = controller;
		this.matchedParametersArray = matchedParametersArray;
	}

	public Method getMethod() {
		return method;
	}


	public MicroMapperController getController() {
		return controller;
	}

	public Object[] getMatchedParametersArray() {
		return matchedParametersArray;
	}

}
