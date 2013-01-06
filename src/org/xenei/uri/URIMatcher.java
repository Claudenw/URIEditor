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
 */
public class URIMatcher {
	// the pattern for the URI scheme
	protected Pattern scheme;
	// the pattern for the URI host
	protected Pattern host;
	// the integer for the port.
	protected Integer port;
	// the pattern for the fragment
	protected Pattern fragment;
	// the pattern for the path.
	protected Pattern path;

	/**
	 * Construct a URI matcher from a another matcher.
	 * @param copy the matcher to copy.
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

	/**
	 * Return the matcher as a regular expression string.
	 * @return the regular expression used to match the URIs.
	 */
	public String asRegEx() {
		StringBuilder sb = new StringBuilder()
				.append(scheme == null ? "([^:]+)" : scheme).append("://")
				.append(host == null ? "([^/:])" : host)
				.append(port == null ? "(:[0-9]+)?" : ":" + port)
				.append(path == null ? "([^#]*)" : path)
				.append(fragment == null ? "(#(.*))?" : "#" + fragment);
		return sb.toString();
	}

	@Override
	public String toString() {
		return asRegEx();
	}

	/**
	 * Set the scheme pattern to match.
	 * If not set the default matches all scheme segments.
	 * @param scheme The regular expression to use to match the scheme portion of the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setScheme(String scheme) {
		this.scheme = Pattern.compile(scheme);
		return this;
	}

	/**
	 * Set the host pattern to match.
	 * If not set the default matches all host segments.
	 * @param host The regular expression to use to match the host portion of the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setHost(String host) {
		this.host = Pattern.compile(host);
		return this;
	}
	/**
	 * Set the port to match.
	 * 	If not set the default matches all ports.
	 * This is the only component that does not accept a regular expression.
	 * @param port value for the port to match.
	 * @return this matcher to facilitate chaining.
	 */

	public URIMatcher setPort(Integer port) {
		this.port = port;
		return this;
	}

	/**
	 * Set the fragment pattern to match.
	 * If not set the default matches all fragment segments.
	 * @param fragment The regular expression to use to match the fragment portion of the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setFragment(String fragment) {
		this.fragment = Pattern.compile(fragment);
		return this;
	}
	
	/**
	 * Set the path pattern to match.
	 * If not set the default matches all path segments.
	 * @param path The regular expression to use to match the path portion of the URI.
	 * @return this matcher to facilitate chaining.
	 */
	public URIMatcher setPath(String path) {
		this.path = Pattern.compile(path);
		return this;
	}

	/**
	 * Determines if a URI matches the pattern
	 * @param uri the URI to test.
	 * @return true if the URI matches the pattern, false otherwise.
	 */
	public boolean matches(URI uri) {
		if (scheme != null) {
			if (!scheme.matcher(uri.getScheme()).matches()) {
				return false;
			}
		}
		if (host != null) {
			if (!host.matcher(uri.getHost()).matches()) {
				return false;
			}
		}
		if (port != null) {
			if (uri.getPort() != port) {
				return false;
			}
		}
		if (path != null) {
			if (!path.matcher(uri.getPath()).matches()) {
				return false;
			}
		}
		if (fragment != null) {
			if (!fragment.matcher(uri.getFragment()).matches()) {
				return false;
			}
		}
		return true;
	}

}
