package org.xenei.uri;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class URIMatcherTest {
	
	private URIMatcher matcher;
	
	@Before
	public void setup()
	{
		matcher = new URIMatcher();
	}
	
	@Test
	public void testSetFragment() throws URISyntaxException
	{	
		URI uri = new URI( "http://localhost:80/bar#foo");
		// null matches
		assertTrue( "Null does not match", matcher.matches( uri ));
		// empty string does not match
		matcher.setFragment("");
		assertFalse( "Empty string matches", matcher.matches( uri ));
		// identical fragment matches
		matcher.setFragment("foo");
		assertTrue( "Should have matched", matcher.matches( uri ));
		// longer fragment does not match.
		uri = new URI( "http://localhost:80/bar#foo2");
		assertFalse( "Longer fragment should not have matched", matcher.matches( uri ));
		// shorter fragment does not match
		uri = new URI( "http://localhost:80/bar#fo");
		assertFalse( "Shorter fragment should not have matched", matcher.matches( uri ));
	}
	
	@Test
	public void testSetHost() throws URISyntaxException
	{	
		URI uri = new URI( "http://localhost:80/bar#foo");
		// null matches
		assertTrue( "Null does not match", matcher.matches( uri ));
		// empty string does not match
		matcher.setHost("");
		assertFalse( "Empty string matches", matcher.matches( uri ));
		// identical fragment matches
		matcher.setHost("localhost");
		assertTrue( "Should have matched", matcher.matches( uri ));
		// longer fragment does not match.
		uri = new URI( "http://localhosts:80/bar#foo");
		assertFalse( "Longer host should not have matched", matcher.matches( uri ));
		// shorter fragment does not match
		uri = new URI( "http://localhos:80/bar#foo");
		assertFalse( "Shorter host should not have matched", matcher.matches( uri ));
	}

	@Test
	public void testSetPath() throws URISyntaxException
	{	
		URI uri = new URI( "http://localhost:80/bar#foo");
		// null matches
		assertTrue( "Null does not match", matcher.matches( uri ));
		// empty string does not match
		matcher.setPath("");
		assertFalse( "Empty string matches", matcher.matches( uri ));
		// identical fragment matches
		matcher.setPath("/bar");
		assertTrue( "Should have matched", matcher.matches( uri ));
		// longer fragment does not match.
		uri = new URI( "http://localhost:80/bars#foo");
		assertFalse( "Longer host should not have matched", matcher.matches( uri ));
		// shorter fragment does not match
		uri = new URI( "http://localhost:80/ba#foo");
		assertFalse( "Shorter host should not have matched", matcher.matches( uri ));
	}
	
	@Test
	public void testSetPort() throws URISyntaxException
	{	
		URI uri = new URI( "http://localhost:80/bar#foo");
		// null matches
		assertTrue( "Null does not match", matcher.matches( uri ));
		// empty string does not match
		matcher.setPort(-1);
		assertFalse( "Empty string matches", matcher.matches( uri ));
		// identical fragment matches
		matcher.setPort(80);
		assertTrue( "Should have matched", matcher.matches( uri ));
		// longer fragment does not match.
		uri = new URI( "http://localhost:800/bars#foo");
		assertFalse( "Longer post should not have matched", matcher.matches( uri ));
		// shorter fragment does not match
		uri = new URI( "http://localhost:8/ba#foo");
		assertFalse( "Shorter post should not have matched", matcher.matches( uri ));
	}
	
	@Test
	public void testSetScheme() throws URISyntaxException
	{	
		URI uri = new URI( "http://localhost:80/bar#foo");
		// null matches
		assertTrue( "Null does not match", matcher.matches( uri ));
		// empty string does not match
		matcher.setScheme( "");
		assertFalse( "Empty string matches", matcher.matches( uri ));
		// identical fragment matches
		matcher.setScheme("http");
		assertTrue( "Should have matched", matcher.matches( uri ));
		// longer fragment does not match.
		uri = new URI( "https://localhost:80/bar#foo");
		assertFalse( "Longer post should not have matched", matcher.matches( uri ));
		// shorter fragment does not match
		uri = new URI( "htt://localhost:80/bar#foo");
		assertFalse( "Shorter post should not have matched", matcher.matches( uri ));
	}
	
	@Test
	public void testAsRegEx() {
		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));		
	}
	
	@Test
	public void testAsRegExEmptyFragment() {
		
		matcher.setFragment("");

		assertFalse( "Matched full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match uri without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		
		assertFalse( "Matched no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match no scheme without fragment", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2"));
		
		assertFalse( "Matched no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertTrue( "Did not match no server without fragment", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2"));
		
		assertFalse( "Matched no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertTrue( "Did not match no port without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2"));
		
		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertTrue( "Did not match no path without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80"));

	}
	
	@Test
	public void testAsRegExPopulatedFragment() {
		matcher.setFragment("bar");

		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		assertFalse( "Matched uri without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertFalse( "Matched no scheme without fragment", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2"));
		
		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertFalse( "Matched no server without fragment", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2"));
		
		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertFalse( "Matched no port without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2"));
		
		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertFalse( "Matched no path without fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80"));

	}
	
	@Test
	public void testAsRegExEmptyHost() {
		matcher.setHost("");

		assertFalse( "Matched full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertFalse( "Matched no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match no scheme without host", Pattern.matches( matcher.asRegEx() , "/foo/foo2#bar"));

		assertTrue( "Did not match no host", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		
		assertFalse( "Matched no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		
		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertTrue( "Did not match no path without server", Pattern.matches( matcher.asRegEx() , "http:#bar"));
		
		assertFalse( "Matched no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertTrue( "Did not match no fragment without server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2"));
	}
	
	@Test
	public void testAsRegExPopulatedHost() {
		matcher.setHost("example.com");
		
		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertFalse( "Matched no scheme without host", Pattern.matches( matcher.asRegEx() , "/foo/foo2#bar"));

		assertFalse( "Matched no host", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		
		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		
		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertFalse( "Matched no path without host", Pattern.matches( matcher.asRegEx() , "http:#bar"));
		
		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertFalse( "Matched no fragment without host", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2"));
	}
	
	@Test
	public void testAsRegExEmptyPath() {
		matcher.setPath("");

		assertFalse( "Matched full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertFalse( "Matched no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match  no scheme without path", Pattern.matches( matcher.asRegEx() , "//example.com:80#bar"));
		
		assertFalse( "Matched no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertTrue( "Did not match  no server without path", Pattern.matches( matcher.asRegEx() , "http:#bar"));

		assertFalse( "Matched no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertTrue( "Did not match no port without path", Pattern.matches( matcher.asRegEx() , "http://example.com#bar"));
		
		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		
		assertFalse( "Matched no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertTrue( "Did not match no fragment without path", Pattern.matches( matcher.asRegEx() , "http://example.com:80"));
	}
	
	@Test
	public void testAsRegExPopulatedPath() {
		matcher.setPath("/foo/foo2");

		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertFalse( "Matched no scheme without path", Pattern.matches( matcher.asRegEx() , "//example.com:80#bar"));
		
		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertFalse( "Matched no server without path", Pattern.matches( matcher.asRegEx() , "http:#bar"));

		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertFalse( "Matched no port without path", Pattern.matches( matcher.asRegEx() , "http://example.com#bar"));
		
		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		
		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertFalse( "Matched no fragment without path", Pattern.matches( matcher.asRegEx() , "http://example.com:80"));
	}
	
	@Test
	public void testAsRegExPopulatedPathNoLeadingSlash() {
		matcher.setPath("foo/foo2");

		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertFalse( "Matched no scheme without path", Pattern.matches( matcher.asRegEx() , "//example.com:80#bar"));
		
		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertFalse( "Matched no server without path", Pattern.matches( matcher.asRegEx() , "http:#bar"));

		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertFalse( "Matched no port without path", Pattern.matches( matcher.asRegEx() , "http://example.com#bar"));
		
		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		
		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertFalse( "Matched no fragment without path", Pattern.matches( matcher.asRegEx() , "http://example.com:80"));
	}
	
	@Test
	public void testAsRegExEmptyPort() {
		matcher.setPort( URIMatcher.NO_PORT );

		assertFalse( "Matched full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));

		assertFalse( "Matched no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertTrue( "Did not match no scheme without port", Pattern.matches( matcher.asRegEx() , "//example.com/foo/foo2#bar"));

		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		
		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		
		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertTrue( "Did not match no path without port", Pattern.matches( matcher.asRegEx() , "http://example.com#bar"));

		assertFalse( "Matched no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));		
		assertTrue( "Did not match no fragment without port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2"));		
	}
	
	@Test
	public void testAsRegExPopulatedPort() {
		matcher.setPort( 80 );

		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));

		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		assertFalse( "Matched no scheme without port", Pattern.matches( matcher.asRegEx() , "//example.com/foo/foo2#bar"));

		assertFalse( "Matched no host", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		
		assertFalse( "Matched no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		
		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertFalse( "Matched no path without port", Pattern.matches( matcher.asRegEx() , "http://example.com#bar"));

		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));		
		assertFalse( "Matched no fragment without port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2"));		
	}
	
	@Test
	public void testAsRegExEmptyScheme() {

		matcher.setScheme("");
		
		assertFalse( "Matched full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertTrue( "Did not match no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		
		assertFalse( "Matched no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertTrue( "Did not match no server without scheme", Pattern.matches( matcher.asRegEx() , "/foo/foo2#bar"));

		assertFalse( "Matched no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertTrue( "Did not match no port without scheme", Pattern.matches( matcher.asRegEx() , "//example.com/foo/foo2#bar"));

		assertFalse( "Matched no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertTrue( "Did not match no path without scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80#bar"));

		assertFalse( "Matched no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertTrue( "Did not match no fragment without scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2"));
	}
	
	@Test
	public void testAsRegExPopulatedScheme() {
		
		matcher.setScheme("http");
		
		assertTrue( "Did not match full uri", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2#bar"));
		
		assertFalse( "Matched no scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2#bar"));
		
		assertTrue( "Did not match no server", Pattern.matches( matcher.asRegEx() , "http:/foo/foo2#bar"));
		assertFalse( "Matched no server without scheme", Pattern.matches( matcher.asRegEx() , "/foo/foo2#bar"));

		assertTrue( "Did not match no port", Pattern.matches( matcher.asRegEx() , "http://example.com/foo/foo2#bar"));
		assertFalse( "Matched no port without scheme", Pattern.matches( matcher.asRegEx() , "//example.com/foo/foo2#bar"));

		assertTrue( "Did not match no path", Pattern.matches( matcher.asRegEx() , "http://example.com:80#bar"));
		assertFalse( "Matched no path without scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80#bar"));

		assertTrue( "Did not match no fragment", Pattern.matches( matcher.asRegEx() , "http://example.com:80/foo/foo2"));
		assertFalse( "Matched no fragment without scheme", Pattern.matches( matcher.asRegEx() , "//example.com:80/foo/foo2"));
	}
	
	@Test
	public void testGetPosition() {
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 3, matcher.getPortPosition());
		assertEquals( 4, matcher.getPathPosition());
		assertEquals( 5, matcher.getFragmentPosition());
		
		matcher.setScheme( "" );
		assertEquals( 0, matcher.getSchemePosition());
		assertEquals( 1, matcher.getHostPosition());
		assertEquals( 2, matcher.getPortPosition());
		assertEquals( 3, matcher.getPathPosition());
		assertEquals( 4, matcher.getFragmentPosition());
		
		matcher.setScheme( null ).setHost("");
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 0, matcher.getHostPosition());
		assertEquals( 0, matcher.getPortPosition());
		assertEquals( 2, matcher.getPathPosition());
		assertEquals( 3, matcher.getFragmentPosition());
		
		matcher.setHost( null ).setPort( URIMatcher.NO_PORT);
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 0, matcher.getPortPosition());
		assertEquals( 3, matcher.getPathPosition());
		assertEquals( 4, matcher.getFragmentPosition());
		
		matcher.setPort( null ).setPath( "" );
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 3, matcher.getPortPosition());
		assertEquals( 0, matcher.getPathPosition());
		assertEquals( 4, matcher.getFragmentPosition());
		
		matcher.setPath( null ).setFragment( "" );
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 3, matcher.getPortPosition());
		assertEquals( 4, matcher.getPathPosition());
		assertEquals( 0, matcher.getFragmentPosition());
		
		matcher.setFragment( null );
	}
	
	@Test
	public void testGetPositionPopulated() {
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 3, matcher.getPortPosition());
		assertEquals( 4, matcher.getPathPosition());
		assertEquals( 5, matcher.getFragmentPosition());
		
		matcher.setScheme( "http" ).setHost("example.com").setPort( 500 )
		.setPath( "/foo/foo2" ).setFragment( "bar" );
		assertEquals( 1, matcher.getSchemePosition());
		assertEquals( 2, matcher.getHostPosition());
		assertEquals( 3, matcher.getPortPosition());
		assertEquals( 4, matcher.getPathPosition());
		assertEquals( 5, matcher.getFragmentPosition());
		
	}
}
