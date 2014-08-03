package org.xenei.uri;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;

public class URIRewriterTest {
	private URIRewriter rewriter;

	private static String SCHEME_PATTERN = "^(.).+(.)$";
	private static String HOST_PATTERN = "([^\\.]*)?.example.([^\\.]+)";
	private static String PATH_PATTERN = "^/([^/]+).*/(.*)$";
	private static String FRAGMENT_PATTERN = "^(.).*(.)$";
	private static String PATTERN_MATCH_URI_STR = "http://bax.example.com:80/foo/foo2#bar";

	@Test
	public void testRewrite() throws URISyntaxException {
		PatternReplacer patternReplacer = new PatternReplacer(
				"{scheme:0}://{host:1}.{host:2}:{port}/{path:1}/{fragment:1}#{fragment:2}")
				.setScheme(SCHEME_PATTERN).setHost(HOST_PATTERN)
				.setPath(PATH_PATTERN).setFragment(FRAGMENT_PATTERN);
		rewriter = new URIRewriter(patternReplacer);
		URI uri = rewriter.rewrite(new URI(PATTERN_MATCH_URI_STR));
		assertEquals("http", uri.getScheme());
		assertEquals("bax.com", uri.getHost());
		assertEquals(80, uri.getPort());
		assertEquals("/foo/b", uri.getPath());
		assertEquals("r", uri.getFragment());
	}

	@Test
	public void testRewriteSkip() throws URISyntaxException {
		PatternReplacer patternReplacer = new PatternReplacer(
				"{scheme:0}://{host:1}.{host:2}:{port}/{path:1}/{fragment:1}#{fragment:2}")
				.setScheme(SCHEME_PATTERN).setHost(HOST_PATTERN)
				.setPath(PATH_PATTERN).setFragment(FRAGMENT_PATTERN);
		rewriter = new URIRewriter(patternReplacer);
		URI uri = rewriter
				.rewrite(new URI("http://example.com:80/foo/foo2#bar"));
		assertEquals("http", uri.getScheme());
		assertEquals("example.com", uri.getHost());
		assertEquals(80, uri.getPort());
		assertEquals("/foo/foo2", uri.getPath());
		assertEquals("bar", uri.getFragment());
	}

	@Test
	public void testRewriteBadPattern() throws URISyntaxException {
		PatternReplacer patternReplacer = new PatternReplacer(
				"{scheme:3}://{host:1}.{host:2}:{port}/{path:1}/{fragment:1}#{fragment:2}")
				.setScheme(SCHEME_PATTERN).setHost(HOST_PATTERN)
				.setPath(PATH_PATTERN).setFragment(FRAGMENT_PATTERN);
		rewriter = new URIRewriter(patternReplacer);
		URI testURI = new URI(PATTERN_MATCH_URI_STR);
		try {
			rewriter.rewrite(testURI);
			fail("Should have thrown URISyntaxException");
		} catch (URISyntaxException e) {
			assertEquals(
					"Subpattern 3 does not exist in ^(.).+(.)$ matching http: http://bax.example.com:80/foo/foo2#bar",
					e.getMessage());
		}
	}
}
