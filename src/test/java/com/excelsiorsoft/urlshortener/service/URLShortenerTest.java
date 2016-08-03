package com.excelsiorsoft.urlshortener.service;

import static com.excelsiorsoft.urlshortener.service.URLShortener.HTTP;
import static com.excelsiorsoft.urlshortener.service.URLShortener.HTTPS;
import static com.excelsiorsoft.urlshortener.service.URLShortener.SLASH;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.excelsiorsoft.urlshortener.service.URLShortener.Controller;

public class URLShortenerTest {

	URLShortener cut;

	@Test
	public void testAlphabetInit() {

		cut = new URLShortener();

		for (int i = 0; i < cut.getAlphabet().length; i++) {
			System.out.print(cut.getAlphabet()[i]);
		}

		assertArrayEquals(
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
						.toCharArray(),
				cut.getAlphabet());
	}
	
	@Test
	public void testUrlNormalization(){
		
		cut = new URLShortener();
		System.out.println(cut.normalizeURL("http://google.com/"));
		assertEquals("google.com", cut.normalizeURL("http://google.com/"));
		assertEquals("google.com", cut.normalizeURL("https://google.com/"));
		assertEquals("yahoo.com", cut.normalizeURL("http://yahoo.com"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testShortenUrl(){
		cut = new URLShortener();
		assertTrue("expect empty collection", cut.getMapping().size()==0);
		
		cut.shorten("http://yahoo.com");
		assertTrue("expect empty collection", cut.getMapping().size()==1);
		

		cut.shorten("http://yahoo.com");
		assertTrue("expect empty collection", cut.getMapping().size()==1);
		
		cut.shorten("http://google.com/");
		assertTrue("expect empty collection", cut.getMapping().size()==2);
		
		
		System.out.println(cut.getMapping());
		
		
	}
	
	@Test
	public void testLifecycle(){
		cut = new URLShortener();
		
		String shortened = cut.shorten(HTTP+"zipari.com"+SLASH);
		String inverted = cut.invert(shortened);
		System.out.println(HTTP+"zipari.com"+SLASH+" => "+shortened);
		System.out.println(shortened + " => " + HTTP + inverted + SLASH);
		}
	
	
	@Test
	public void testVisitCount(){
		
		cut = new URLShortener();
		String cnn = "cnn.com";
		String cnnShortened = cut.shorten(HTTP+cnn+SLASH);
		
		Controller  controller = new Controller(cut);
		
		controller.visit(cnnShortened);
		assertEquals(1, cut.getMapping().get(cut.getShortenedSegment(cnnShortened)).getVisits());
		controller.visit(cnnShortened);
		assertEquals(2, cut.getMapping().get(cut.getShortenedSegment(cnnShortened)).getVisits());
		controller.visit(cnnShortened);
		assertEquals(3, cut.getMapping().get(cut.getShortenedSegment(cnnShortened)).getVisits());
		
		String yahoo = "yahoo.com";
		String yahooShortened = cut.shorten(HTTP+yahoo+SLASH);
		
		controller.visit(yahooShortened);
		assertEquals(1, cut.getMapping().get(cut.getShortenedSegment(yahooShortened)).getVisits());
		controller.visit(yahooShortened);
		assertEquals(2, cut.getMapping().get(cut.getShortenedSegment(yahooShortened)).getVisits());
		assertEquals(2,cut.getMapping().size());
		
		
	}
	
	@Test
	public void testLongUrlBehavior(){
		
		cut = new URLShortener();
		String longUrl = HTTP+"huge.com"+SLASH;
		
		Controller  controller = new Controller(cut);
		for(int i = 0; i<10;i++){
		
		long numOfTimes = controller.visit(longUrl);
		
			System.out.println(controller.getShortener().getMapping());
			
			System.out.println("visited "+longUrl+" "+numOfTimes+" times");
		}
		
		
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	@Test
	public void testShortUrlBehavior(){
		
		cut = new URLShortener();
		String droit = "droit.com";
		String longUrl = HTTPS+droit+SLASH;
		
		String NA = "http://troops.io/2aw4kk8~";
		
		String droitShortenedNA = cut.shorten(NA);
		
		
		String droitShortened = cut.shorten(longUrl);
		
		System.out.println(droitShortened);
		
		//Controller  controller = new Controller(cut);
		//controller.visit(droitShortened);
		
		Controller  controller = new Controller(cut);
		for(int i = 0; i<10;i++){
		
		long numOfTimes = controller.visit(droitShortened);
		
			System.out.println(controller.getShortener().getMapping());
			
			System.out.println("visited "+longUrl+" "+numOfTimes+" times");
		}
		
		
	}
	
	
}
