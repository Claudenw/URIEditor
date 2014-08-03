package org.xenei.uri;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.BeforeClass;
import org.junit.Test;

public class PatternReplacerTest {

	private PatternReplacer patternReplacer;

	private static URI FULL_URI;
	private static URI NO_SCHEME;
	private static URI NO_HOST;
	private static URI NO_PORT;
	private static URI NO_PATH;
	private static URI NO_FRAGMENT;
	
	private static URI PATTERN_MATCH_URI;
	private static String SCHEME_PATTERN = "^(.).+(.)$";
	private static String HOST_PATTERN = "([^\\.]*)?.example.([^\\.]+)";
	private static String PATH_PATTERN = "^/([^/]+).*/(.*)$";
	private static String FRAGMENT_PATTERN = "^(.).*(.)$";

	@BeforeClass
	public static void setupStatic() throws URISyntaxException {
		FULL_URI = new URI("http://example.com:80/foo/foo2#bar");
		NO_SCHEME = new URI("//example.com:80/foo/foo2#bar");
		NO_HOST = new URI("http:/foo/foo2#bar");
		NO_PORT = new URI("http://example.com/foo/foo2#bar");
		NO_PATH = new URI("http://example.com:80#bar");
		NO_FRAGMENT = new URI("http://example.com:80/foo/foo2");
		PATTERN_MATCH_URI = new URI("http://bax.example.com:80/foo/foo2#bar");
	}

	private void failTest(URI uri) {
		try {
			patternReplacer.populate(uri);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("URI does not match regular expression",
					e.getMessage());
		}
	}

	private void passTest(String pattern, URI uri) {
		String s = patternReplacer.populate(uri);
		assertEquals(pattern, s);
	}

	@Test
	public void basicReplacementTest() throws URISyntaxException {
		// default pattern replacer
		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		// http://example.com:80/foo/foo2#bar"
		passTest("http example.com 80 /foo/foo2 bar", FULL_URI);
		passTest(" example.com 80 /foo/foo2 bar", NO_SCHEME);
		passTest("http   /foo/foo2 bar", NO_HOST);
		passTest("http example.com  /foo/foo2 bar", NO_PORT);
		passTest("http example.com 80  bar", NO_PATH);
		passTest("http example.com 80 /foo/foo2 ", NO_FRAGMENT);
	}

	@Test
	public void emptySchemeReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		patternReplacer.setScheme("");

		failTest(FULL_URI);
		passTest(" example.com 80 /foo/foo2 bar", NO_SCHEME);
		failTest(NO_HOST);
		failTest(NO_PORT);
		failTest(NO_PATH);
		failTest(NO_FRAGMENT);
	}

	@Test
	public void emptyHostReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		patternReplacer.setHost("");

		failTest(FULL_URI);
		failTest(NO_SCHEME);
		passTest("http   /foo/foo2 bar", NO_HOST);
		failTest(NO_PORT);
		failTest(NO_PATH);
		failTest(NO_FRAGMENT);
	}

	@Test
	public void emptyPortReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		patternReplacer.setPort(URIMatcher.NO_PORT);

		failTest(FULL_URI);
		failTest(NO_SCHEME);
		passTest("http   /foo/foo2 bar", NO_HOST);
		passTest("http example.com  /foo/foo2 bar", NO_PORT);
		failTest(NO_PATH);
		failTest(NO_FRAGMENT);
	}

	@Test
	public void emptyFragmentReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		patternReplacer.setFragment("");

		failTest(FULL_URI);
		failTest(NO_SCHEME);
		failTest(NO_HOST);
		failTest(NO_PORT);
		failTest(NO_PATH);
		passTest("http example.com 80 /foo/foo2 ", NO_FRAGMENT);
	}

	@Test
	public void emptyPathReplacementTest() throws URISyntaxException {
		// default pattern replacer
		patternReplacer = new PatternReplacer(
				"{scheme} {host} {port} {path} {fragment}");
		patternReplacer.setPath("");

		failTest(FULL_URI);
		failTest(NO_SCHEME);
		failTest(NO_HOST);
		failTest(NO_PORT);
		passTest("http example.com 80  bar", NO_PATH);
		failTest(NO_FRAGMENT);
	}

	@Test
	public void subPatternReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme:1}{scheme:2} {host:1}{host:2} {port} {path:1}{path:2} {fragment:1}{fragment:2}");
		patternReplacer.setScheme("^(.).+(.)$")
		.setHost( "([^\\.]*)?.example.([^\\.]+)")
		.setPath( "^/([^/]+).*/(.*)$")
		.setFragment( "^(.).*(.)$");

		passTest("hp baxcom 80 foofoo2 br", PATTERN_MATCH_URI );
		
	}

	@Test
	public void fullSubPatternReplacementTest() throws URISyntaxException {

		patternReplacer = new PatternReplacer(
				"{scheme:0} {host:0} {port} {path:0} {fragment:0}")
		.setScheme(SCHEME_PATTERN)
		.setHost(  HOST_PATTERN )
		.setPath( PATH_PATTERN )
		.setFragment( FRAGMENT_PATTERN );

		passTest("http bax.example.com 80 /foo/foo2 bar", PATTERN_MATCH_URI);
		
	}

	
	@Test
	public void missingSubPatternReplacementTest() throws URISyntaxException {
		
		patternReplacer = new PatternReplacer(
				"{scheme:3} {host:0} {port} {path:0} {fragment:0}")
		.setScheme(SCHEME_PATTERN)
		.setHost( HOST_PATTERN )
		.setPath( PATH_PATTERN )
		.setFragment( FRAGMENT_PATTERN );

		try {
			patternReplacer.populate(PATTERN_MATCH_URI);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Subpattern 3 does not exist in "+SCHEME_PATTERN+" matching http",
					e.getMessage());
		}
		
		patternReplacer = new PatternReplacer(
				"{scheme:0} {host:3} {port} {path:0} {fragment:0}", patternReplacer);
		try {
			patternReplacer.populate(PATTERN_MATCH_URI);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Subpattern 3 does not exist in "+HOST_PATTERN+" matching bax.example.com",
					e.getMessage());
		}
		
//		patternReplacer = new PatternReplacer(
//				"{scheme:0} {host:0} {port:3} {path:0} {fragment:0}", patternReplacer);
//		try {
//			patternReplacer.populate(PATTERN_MATCH_URI);
//			fail("Should have thrown IllegalArgumentException");
//		} catch (IllegalArgumentException e) {
//			assertEquals("Subpattern 3 does not exist in "+PORT_PATTERN+" matching bax.example.com",
//					e.getMessage());
//		}
		
		patternReplacer = new PatternReplacer(
				"{scheme:0} {host:0} {port} {path:3} {fragment:0}", patternReplacer);
		try {
			patternReplacer.populate(PATTERN_MATCH_URI);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Subpattern 3 does not exist in "+PATH_PATTERN+" matching /foo/foo2",
					e.getMessage());
		}
		
		patternReplacer = new PatternReplacer(
				"{scheme:0} {host:0} {port} {path:0} {fragment:3}", patternReplacer);
		try {
			patternReplacer.populate(PATTERN_MATCH_URI);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Subpattern 3 does not exist in "+FRAGMENT_PATTERN+" matching bar",
					e.getMessage());
		}
	}

	//	@Test
//	public void emptyHostReplacementTest() throws URISyntaxException {
//
//		patternReplacer = new PatternReplacer(
//				"{scheme} {host} {port} {path} {fragment}");
//		patternReplacer.setHost("");
//
//		failTest(FULL_URI);
//		failTest(NO_SCHEME);
//		passTest("http   /foo/foo2 bar", NO_HOST);
//		failTest(NO_PORT);
//		failTest(NO_PATH);
//		failTest(NO_FRAGMENT);
//	}
//
//	@Test
//	public void emptyPortReplacementTest() throws URISyntaxException {
//
//		patternReplacer = new PatternReplacer(
//				"{scheme} {host} {port} {path} {fragment}");
//		patternReplacer.setPort(URIMatcher.NO_PORT);
//
//		failTest(FULL_URI);
//		failTest(NO_SCHEME);
//		passTest("http   /foo/foo2 bar", NO_HOST);
//		passTest("http example.com  /foo/foo2 bar", NO_PORT);
//		failTest(NO_PATH);
//		failTest(NO_FRAGMENT);
//	}
//
//	@Test
//	public void emptyFragmentReplacementTest() throws URISyntaxException {
//
//		patternReplacer = new PatternReplacer(
//				"{scheme} {host} {port} {path} {fragment}");
//		patternReplacer.setFragment("");
//
//		failTest(FULL_URI);
//		failTest(NO_SCHEME);
//		failTest(NO_HOST);
//		failTest(NO_PORT);
//		failTest(NO_PATH);
//		passTest("http example.com 80 /foo/foo2 ", NO_FRAGMENT);
//	}
//
//	@Test
//	public void emptyPathReplacementTest() throws URISyntaxException {
//		// default pattern replacer
//		patternReplacer = new PatternReplacer(
//				"{scheme} {host} {port} {path} {fragment}");
//		patternReplacer.setPath("");
//
//		failTest(FULL_URI);
//		failTest(NO_SCHEME);
//		failTest(NO_HOST);
//		failTest(NO_PORT);
//		passTest("http example.com 80  bar", NO_PATH);
//		failTest(NO_FRAGMENT);
//	}
}
