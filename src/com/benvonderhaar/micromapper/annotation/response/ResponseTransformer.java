package com.benvonderhaar.micromapper.annotation.response;

import javax.servlet.http.HttpServletResponse;

public abstract class ResponseTransformer<T> {

	public abstract void transform(HttpServletResponse response, T annotation);
	
}
