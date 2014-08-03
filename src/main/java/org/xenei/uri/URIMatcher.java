/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xenei.uri;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * URI regular expression pattern matching.
 * 
 * see http://regexpal.com/ to test regular expressions.
 */
public class URIMatcher {
	public static final Integer NO_PORT = -1;

	public static final String ALPHA = "a-zA-Z";
	public static final String DIGIT = "0-9";
	public static final String SAFE = "\\$\\-_@\\.&";
	public static final String EXTRA = "\\!\\*\\\"\\'\\(\\),";
	public static final String ESCAPE = "%";
	public static final String XP = "\\|\\+";

	private static final String XALPHA = String.format("%s%s%s%s%s", ALPHA,
			ALPHA, DIGIT, SAFE, EXTRA, ESCAPE);
	private static final String XPALPHA = String.format("%s%s", XALPHA, XP);

	public static final String SCHEME_REGEX = String.format("(([%s][%s]*):)?",
			ALPHA, XALPHA);
	public static final String HOST_REGEX = String.format("(//([%s][%s]*))?",
			ALPHA, XALPHA);
	public static final String PORT_REGEX = "(:([0-9]+))?";
	public static final String PATH_REGEX = String.format("(/?[%s]+(/[%s]*))?",
			XPALPHA, XPALPHA);
	public static final String FRAGMENT_REGEX = String.format("(#([%s]+))?",
			XALPHA);
	// the pattern for the URI scheme
	private Pattern scheme;
	// the pattern for the URI host
	private Pattern host;
	// the integer for the port.
	private Integer port;
	// the pattern for the fragment
	private Pattern fragment;
	// the pattern for the path.
	private Pattern path;

	/**
	 * Construct a URI matcher from a another matcher.
	 * 
	 * @param copy
	 *            the matcher to copy.
	 */
	protected URIMatcher(URIMatcher copy) {
		this.scheme = copy.scheme;
		this.host = copy.host;
		this.port = copy.port;
		this.fragment = copy.fragment;
		this.path = copy.path;
	}

	/**
	 * Create an empty matcher.
	 */
	public URIMatcher() {
	}

	private String pattern(Pattern patt, String dflt, String fmt) {
		return pattern(patt, dflt, fmt, "");
	}

	private String pattern(Pattern patt, String dflt, String fmt,
			String emptyVal) {
		if (patt == null) {
			return dflt;
		}
		if (patt.pattern().length() != 0) {
			return String.format(fmt, patt.pattern());
		}
		return emptyVal;
	}

	/**
	 * Return the matcher as a regular expression string.
	 * 
	 * @return the regular expression used to match the URIs.
	 */
	public String asRegEx() {

		StringBuilder sb = new StringBuilder().append("^")
				.append(pattern(scheme, SCHEME_REGEX, "(%s):"))
				.append(pattern(host, HOST_REGEX, "//(%s)"));
		if (host == null || host.pattern().length() > 0) {
			sb.append(port == null ? PORT_REGEX : ((port < 0) ? ""
					: (":" + port)));
		}

		if (path != null && path.pattern().startsWith("/")) {
			sb.append(pattern(path, PATH_REGEX, "((%s))"));
		} else {
			sb.append(pattern(path, PATH_REGEX, "(/?(%s))"));
		}
		sb.append(pattern(fragment, FRAGMENT_REGEX, "(#(%s))"));
		return sb.append("$").toString();
	}
	
	private int hasPresence( Pattern p )
	{
		return p == null || p.pattern().length()>0 ? 1 : 0;
	}
	
	public int getSchemePosition()
	{
		return hasPresence( scheme );
	}
	
	public int getHostPosition()
	{
		
		int retval = hasPresence( host );
		if (retval > 0) {
			retval += hasPresence(scheme);
		}
		return retval;
	}
	
	private int hasPresencePort()
	{
		return (port == null || port > NO_PORT) ? hasPresence(host) : 0;
	}

	public int getPortPosition()
	{
		int retval = hasPresencePort();
		if (retval > 0)
		{
			retval += hasPresence( scheme );
			retval ++; // host
		}
		return retval;
	}
	
	public int getPathPosition()
	{
		int retval = hasPresence( path );
		if ( retval > 0)
		{
			retval += hasPresence( scheme );
			retval += hasPresence( host );
			retval += hasPresencePort();
		}
		return retval;
	}

	public int getFragmentPosition()
	{
		int retval = hasPresence( fragment );
		if ( retval > 0)
		{
			retval += hasPresence( scheme );
			retval += hasPresence( host );
			retval += hasPresencePort();
			retval += hasPresence( path );
		}
		return retval;
	}

	@Override
	public String toString() {
		return asRegEx();
	}

	/**
	 * Set the scheme pattern to match. If not set the default matches all
	 * scheme segments. Pattern must include the colon that follows the scheme.
	 * to match all schemes except file: use ((?!file).+): An empty string ""
	 * matches URLs without a scheme specified
	 * 
	 * @param scheme
	 *            The regular expression to use to match the scheme portion of
	 *            the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setScheme(String scheme) {
		this.scheme = scheme==null?null:Pattern.compile(scheme);
		return this;
	}

	protected Pattern getScheme() {
		return scheme;
	}

	/**
	 * Set the host pattern to match. If not set the default matches all host
	 * segments. host sections must start with "//" an empty string "" matches
	 * URLs without a host field.
	 * 
	 * @param host
	 *            The regular expression to use to match the host portion of the
	 *            URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setHost(String host) {
		this.host = host==null?null:Pattern.compile(host);
		return this;
	}

	protected Pattern getHost() {
		return host;
	}

	/**
	 * Set the port to match. If not set (or set to null) it matches all ports.
	 * This is the only component that does not accept a regular expression. any
	 * negative value will match a URI withtout a port specified.
	 * 
	 * @param port
	 *            value for the port to match.
	 * @return this matcher to facilitate chaining.
	 */

	public URIMatcher setPort(Integer port) {
		this.port = port==null?null:(port <= NO_PORT ? -1 : port);
		return this;
	}

	protected Integer getPort() {
		return port;
	}

	/**
	 * Set the fragment pattern to match. If not set the default matches all
	 * fragment segments.
	 * 
	 * @param fragment
	 *            The regular expression to use to match the fragment portion of
	 *            the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setFragment(String fragment) {
		this.fragment = fragment==null?null:Pattern.compile(fragment);
		return this;
	}

	protected Pattern getFragment() {
		return this.fragment;
	}

	/**
	 * Set the path pattern to match. If not set the default matches all path
	 * segments. the regular expression should stop at the # symbol if present
	 * 
	 * @param path
	 *            The regular expression to use to match the path portion of the
	 *            URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setPath(String path) {
		this.path = path==null?null:Pattern.compile(path);
		return this;
	}

	protected Pattern getPath() {
		return this.path;
	}

	private String value(String s) {
		return s == null ? "" : s;
	}

	/**
	 * Determines if a URI matches the pattern
	 * 
	 * @param uri
	 *            the URI to test.
	 * @return true if the URI matches the pattern, false otherwise.
	 */
	public boolean matches(URI uri) {
		if (scheme != null) {
			if (!scheme.matcher(value(uri.getScheme())).matches()) {
				return false;
			}
		}
		if (host != null) {
			if (!host.matcher(value(uri.getHost())).matches()) {
				return false;
			}
		}
		if (port != null) {
			if (uri.getPort() != port) {
				return false;
			}
		}
		if (path != null) {
			if (!path.matcher(value(uri.getPath())).matches()) {
				return false;
			}
		}
		if (fragment != null) {
			if (!fragment.matcher(value(uri.getFragment())).matches()) {
				return false;
			}
		}
		return true;
	}

}
