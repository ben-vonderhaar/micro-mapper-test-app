package com.benvonderhaar.micromapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMethod {
	
	Verb[] verbs();
	
	enum Verb {
		GET, POST, PUT, DELETE
	}
	
}
