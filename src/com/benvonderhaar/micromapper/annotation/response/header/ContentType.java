package com.benvonderhaar.micromapper.annotation.response.header;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ContentType {
	
	String mediaType();
	String charset() default "";
	
	class MediaType {
		public static final String JSON = "application/json";
		public static final String TEXT = "text/plain";
		public static final String HTML = "text/html";
	}
	
	class Charset {
		public static final String UTF_8 = "UTF-8";
		public static final String CP_1252 = "cp1252";
		public static final String ISO_8869_1 = "ISO-8859-1";
	}
	
}
