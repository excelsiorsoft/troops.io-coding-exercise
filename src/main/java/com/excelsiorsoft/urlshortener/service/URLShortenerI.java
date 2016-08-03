package com.excelsiorsoft.urlshortener.service;

public interface URLShortenerI {
	
	String shorten(String longUrl);
	String invert (String shortUrl);

}
