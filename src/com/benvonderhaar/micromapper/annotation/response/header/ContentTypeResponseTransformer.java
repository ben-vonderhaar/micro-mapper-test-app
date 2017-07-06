package com.benvonderhaar.micromapper.annotation.response.header;

import javax.servlet.http.HttpServletResponse;

import com.benvonderhaar.micromapper.annotation.response.ResponseTransformer;

public class ContentTypeResponseTransformer extends ResponseTransformer<ContentType> {

	@Override
	public void transform(HttpServletResponse response, ContentType annotation) {
		
		String contentType = annotation.mediaType();
		
		if (!annotation.charset().equals("")) {
			contentType += ";charset=" + annotation.charset();
		}
		
		response.setContentType(contentType);
	}
}
