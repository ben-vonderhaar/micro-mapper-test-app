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

//	private List<PatternString> urlSegments;
//	private List<PatternString> parameterSegments;
	
	private static final String MATCH_STRING = "{}";
	
	private String[] urlSegmentsArray;
	private String urlPattern;
	
	public MicroMapperURL(String urlPattern) {

		// Ensure urlPattern starts with /
		if (!urlPattern.startsWith("/")) {
			
			// TODO more specific exception type
			throw new RuntimeException("MMURLPattern must begin with / character");
		}
		
		if (urlPattern.contains(MATCH_STRING + MATCH_STRING)) {
			
			// TODO more specific exception type
			throw new RuntimeException("MMURLPattern cannot contain adjacent Match Strings");
		}
		
		// Strip preceding / and store URL Pattern to ensure consistent pattern matching.
		urlPattern = urlPattern.substring(1);		
		this.urlPattern = urlPattern;
		
		// Avoid empty element at start of array if the URL begins with MATCH_STRING.
		if (urlPattern.startsWith(MATCH_STRING)) {
			this.urlSegmentsArray = urlPattern.substring(2).split("\\{\\}");
		} else {
			this.urlSegmentsArray = urlPattern.split("\\{\\}");
		}		
	}
	
	@Override
	public String toString() {
		return "/" + this.urlPattern;
	}
	
	/**
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public boolean matchesURL(String url) {
		
		System.out.println(this.toString());
		
		// Strip preceding /
		url = url.substring(1);
		
		// If pattern begins with characters to match (not the match string) and those characters
		// do not match the first match-able segment of this URL Pattern, no match.
		if (!this.urlPattern.startsWith(MATCH_STRING) && 0 != url.indexOf(urlSegmentsArray[0])) {
			System.out.println("beginning of url doesn't match");
			return false;
		}
		
		int segmentIndex = 0;
		String unprocessedURL = url;
		
		List<String> patternMatches = new ArrayList<String>();
		
		// Verify that url contains each segment in appropriate order
		for (String urlSegment : urlSegmentsArray) {
			
			// If the remainder of the URL contains the in-process match-able segment, adjust
			// bookkeeping and add matched pattern
			if (unprocessedURL.indexOf(urlSegment) >= 0) {

				// TODO ensure match does not contain /
				
				patternMatches.add(url.substring(segmentIndex, segmentIndex + unprocessedURL.indexOf(urlSegment)));
				segmentIndex += unprocessedURL.indexOf(urlSegment) + urlSegment.length();
				unprocessedURL = url.substring(segmentIndex);
			} else {
				System.out.println("segments do not match");
				return false;
			}
		}

		// TODO ensure unprocessedURL matches the last segment?
		// Validation necessary
		
		patternMatches.add(unprocessedURL);
		
		return true;
	}
	
//	private void populateUrlSegments(String baseURL) {
//		for (String segment : baseURL.split("/")) {
//			urlSegments.add(new PatternString(segment));
//		}
//	}
//	
//	private void populateParameterSegments(String parameters) {
//		for (String segment : parameters.split("/")) {
//			parameterSegments.add(new PatternString(segment));
//		}
//	}
	
	
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