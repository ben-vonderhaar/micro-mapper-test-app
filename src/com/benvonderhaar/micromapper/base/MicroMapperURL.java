package com.benvonderhaar.micromapper.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
	
	private Method method;
	private Map<String, List<String>> matchingPatterns;
	
	public MicroMapperURL(String urlPattern, Method method) {

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
		this.urlPattern = urlPattern.substring(1);	

		this.method = method;
		this.matchingPatterns = new HashMap<String, List<String>>();
		
		// Avoid empty element at start of array if the URL begins with MATCH_STRING.
		if (urlPattern.startsWith(MATCH_STRING)) {
			this.urlSegmentsArray = this.urlPattern.substring(2).split("\\{\\}");
		} else {
			this.urlSegmentsArray = this.urlPattern.split("\\{\\}");
		}
	}
	
	@Override
	public boolean equals(Object other) {
		
		System.out.println("comparison");
		
		if (other instanceof MicroMapperURL) {
			return this.toString().equals(((MicroMapperURL) other).toString());
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "/" + this.urlPattern;
	}
	
	/**
	 * TODO log reasoning and be sensitive to log levels (i.e. check if debug is enabled)
	 * 
	 * @param url
	 * @return
	 */
	public boolean matchesURL(String url) {
		
		// Strip preceding /
		url = url.substring(1);
		
		System.out.println("------");
		System.out.println(url);
		System.out.println(urlSegmentsArray[0]);
		
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

				if (unprocessedURL.indexOf(urlSegment) != 0) {
					String patternMatch = url.substring(segmentIndex, segmentIndex + unprocessedURL.indexOf(urlSegment));
					
					// MATCH_STRING segments cannot contain /
					if (patternMatch.contains("/")) {
						System.out.println("patternMatch contains /");
						return false;
					}
					
					patternMatches.add(url.substring(segmentIndex, segmentIndex + unprocessedURL.indexOf(urlSegment)));
				}
				
				segmentIndex += unprocessedURL.indexOf(urlSegment) + urlSegment.length();
				unprocessedURL = url.substring(segmentIndex);
			} else {
				System.out.println("segments do not match");
				return false;
			}
		}
		
		// TODO determine if this is true?  what if URL parameters have /?
		// Remainder of URL cannot contain /
		if (unprocessedURL.contains("/")) {
			
			System.out.println("unprocessedURL contains /");
			
			return false;
		}
		
		patternMatches.add(unprocessedURL);
		
		if (patternMatches.size() != this.urlSegmentsArray.length) {
			System.out.println("sizes don't match");
			return false;
		}
		
		this.matchingPatterns.put("/" + this.urlPattern, patternMatches);
		
		return true;
	}
	

	public List<String> getMatchingPattern(String url) {
		
		System.out.println("here");
		
		System.out.println(url);
		System.out.println(matchingPatterns);
		
		return matchingPatterns.get(url);
	}	
	
	public Method getMethod() {
		return this.method;
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