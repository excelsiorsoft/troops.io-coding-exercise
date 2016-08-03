/**
 * 
 */
package com.excelsiorsoft.urlshortener.service;

import java.util.Random;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Simeon
 *
 */
public class URLShortener implements URLShortenerI{
	
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String SLASH = "/";
	private static String PREFIX = "http://troops.io";
	private static char alphabet[];
	
	private Random randomizer = new Random();;
	private BiMap<String, Tuple> mappings;
	
	private int shortUrlLength = 8;
	
	
	public BiMap<String, Tuple> getMapping() {
		return mappings;
	}
	
	public String getPrefix() {
		return PREFIX;
	}

	public void setPrefix(String prefix) {
		this.PREFIX = prefix;
	}

	public int getShortUrlLength() {
		return shortUrlLength;
	}

	public void setShortUrlLength(int shortUrlLength) {
		this.shortUrlLength = shortUrlLength;
	}

	public static void setAlphabet(char[] alphabet) {
		URLShortener.alphabet = alphabet;
	}

	public char[] getAlphabet() {
		return URLShortener.alphabet;
	}

	public URLShortener() {
		
		mappings = HashBiMap.create();
		
		setAlphabet();
	}

	private void setAlphabet() {
		URLShortener.alphabet = new char[62];
		for (int i = 0; i < 62; i++) {
			int j = 0;
			if (i < 10) {
				j = i + 48;
			} else if (i > 9 && i <= 35) {
				j = i + 55;
			} else {
				j = i + 61;
			}
			URLShortener.alphabet[i] = (char) j;
		}
	}
	
	protected String normalizeURL(String url) {
		
		if(url == null) throw new IllegalArgumentException("The provided URL is invalid");
		String result = null;
		
		if (url.substring(0, 7).equals(HTTP))
			result = url.substring(7);

		if (url.substring(0, 8).equals(HTTPS))
			result = url.substring(8);

		if (result.charAt(result.length() - 1) == SLASH.toCharArray()[0])
			result = result.substring(0, result.length() - 1);
		return result;
	}
	
	
	protected boolean valid(String url) {//to be implemented
		return true;
	}
	
	private String generateShortUrl() {
		
		String shortUrl = "";
		boolean flag = true;
		
		while (flag) {

			for (int i = 1; i <= shortUrlLength; i++) {
				shortUrl += alphabet[randomizer.nextInt(62)];
			}
			//exit when unique key is created
			if (!mappings.containsKey(shortUrl)) {
				flag = false;
			}
		}
		return shortUrl;
	}
	
	private String getShortUrl (Tuple stub) {

		if(stub.getUrl() == null) throw new IllegalArgumentException("The provided URL is invalid");
		
		String shortUrl = generateShortUrl();
		mappings.forcePut(shortUrl, stub);

		return shortUrl;
	}

	
	public String shorten(String longUrl) {
		
		if(!valid(longUrl)) throw new IllegalArgumentException("The provided URL is invalid");
		
		String shortened = "";
		String normalizedUrl = normalizeURL(longUrl);
			Tuple stub = new Tuple(normalizedUrl,0);	
					
			if(mappings.containsValue(stub)){
				String shortKey = mappings.inverse().get(stub);
				Tuple realValue = mappings.get(shortKey);
				long oldVisits = realValue.getVisits();
				mappings.remove(shortKey);
				mappings.put(shortKey, new Tuple(normalizedUrl, ++oldVisits));
				shortened = PREFIX + SLASH + shortKey;
			}else{
				shortened = PREFIX + SLASH + getShortUrl(/*normalizedUrl*/stub);
			}
					

		return shortened;
	}

	public String invert(String shortUrl) {

		if(shortUrl == null) throw new IllegalArgumentException("The provided URL is invalid");
		
		return mappings
				.get(getShortenedSegment(shortUrl)).getUrl();

	}
	
	public String getShortenedSegment(String shortUrl){
		return shortUrl.substring(PREFIX.length() + 1);
	}
	
	
	
	
	public final static class Tuple{
		
		
		public String url;
		public String getUrl() {
			return url;
		}

		public long visits;
		
		public long getVisits() {
			return visits;
		}

		public void setVisits(long visits) {
			this.visits = visits;
		}

		public Tuple(String url, long visits) {

			this.url = url;
			this.visits = visits;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple other = (Tuple) obj;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Tuple [url=" + url + ", visits=" + visits + "]";
		}

		
	}
	
	

	
	public static class Controller{
		
		
		URLShortener shortener;
		
		public Controller(URLShortener shortener){
			this.shortener = shortener;
			
		}
		
		
		
		@SuppressWarnings("unused")
		public long visit(String providedUrl){
			long visits = 0;
			String prefix = shortener.getPrefix();
			BiMap<String, Tuple> map = shortener.getMapping();
			
			int prefixLength = prefix.length();
			int slashLength = SLASH.length();
			int shortSegmentLength = shortener.getShortUrlLength();
			
			boolean startsWithTroopsDomain = providedUrl.contains(prefix);
			boolean shortenedInLength = providedUrl.length() == prefixLength+slashLength+shortSegmentLength;
			
			if(startsWithTroopsDomain && shortenedInLength){//already shortened
				
				String url = shortener.getShortenedSegment(providedUrl);
				
				if(map.containsKey(url)){
					
					Tuple oldTuple = map.get(url);
					long oldVisits = oldTuple.getVisits();
					visits = ++oldVisits;
					oldTuple.setVisits(visits);
					
				}
			}else{//longUrl
				

					String shortened = shortener.shorten(providedUrl);
					visits = shortener.getMapping().get(shortener.getShortenedSegment(shortened)).visits;
					
				
			}
			
			return visits;
		}



		public URLShortener getShortener() {
			return shortener;
		}
	}
}
