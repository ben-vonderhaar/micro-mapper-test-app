package com.benvonderhaar.micromapper.annotation.response.header;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.benvonderhaar.micromapper.annotation.response.header.cookie.DynamicCookieSetter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetDynamicCookie {

	Class<? extends DynamicCookieSetter> cookieSetter();
	
}
