package com.benvonderhaar.micromapper.base;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * TODO list
 * --Handle case where varying url section is not the only thing in a segment (?)
 * 
 * @author Ben
 */
public class MicroMapperURL {

	private List<PatternString> urlSegments;
	private List<PatternString> parameterSegments;
	
	public MicroMapperURL(String urlPattern) {
		
		urlSegments = new ArrayList<PatternString>();
		parameterSegments = new ArrayList<PatternString>();
		
		if (!urlPattern.contains("?")) {
			populateUrlSegments(urlPattern);
		} else {
			populateUrlSegments(urlPattern.split("?")[0]);
			populateParameterSegments(urlPattern.split("?")[1]);
		}
	}
	
	/**
	 * 
	 * TODO this can be genericized/simplified
	 * 
	 * @param url
	 * @return
	 */
	public boolean equalsURL(String url) {
		
		String preQueryURL = url.contains("?") ? url.split("?")[0] : url;
		String postQueryURL = url.contains("?") ? url.split("?")[1] : "";
		
		// Handle case where pattern contains a query and the URL does not.
		if (parameterSegments.size() > 0 && postQueryURL.equals("")) {
			return false;
		}
		
		String[] preQueryURLSegments = preQueryURL.split("/");
		
		// Handle case of different numbers of pre-query segments.
		if (preQueryURLSegments.length != this.urlSegments.size()) {
			return false;
		}
		
		// Compare pre-query URL Segments to pattern's URL segments
		for (int i = 0; i < preQueryURLSegments.length; i++) {
			if (!this.urlSegments.get(i).equalsString(preQueryURLSegments[i])) {
				return false;
			}
		}
		
		if (parameterSegments.size() > 0) {
			String[] postQueryURLSegments = postQueryURL.split("/");
			
			// Handle case of different numbers of post-query segments.
			if (postQueryURLSegments.length != this.parameterSegments.size()) {
				return false;
			}
			
			// Compare post-query URL Segments to pattern's URL segments
			for (int i = 0; i < postQueryURLSegments.length; i++) {
				if (!this.parameterSegments.get(i).equalsString(postQueryURLSegments[i])) {
					return false;
				}
			}
		}

		
		return true;
	}
	
	private void populateUrlSegments(String baseURL) {
		for (String segment : baseURL.split("/")) {
			urlSegments.add(new PatternString(segment));
		}
	}
	
	private void populateParameterSegments(String parameters) {
		for (String segment : parameters.split("/")) {
			parameterSegments.add(new PatternString(segment));
		}
	}
	
	
}
class PatternString {
	
	private String string;
	
	public PatternString(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return this.string;
	}
	
	public boolean equalsString(String otherString) {
		
		 if (this.string.equals("{%d}") && isNumber(otherString)) {
			return true;
		} else if (this.string.equals("{%s}")) {
			return true;
		} else if (this.string.equals(otherString)) {
			return true;
		}
		
		return false;
	}
	
	// This could be more performant, see http://stackoverflow.com/a/3543749
	public static boolean isNumber(String string) {
		
		if (string.equals("{%d}")) {
			return true;
		}
		
		try {
			Double.valueOf(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
}