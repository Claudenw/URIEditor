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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the URIMatcher class with pattern replacement capabilities.
 * 
 * Provides the ability to insert URI sections into a string.
 */
public class PatternReplacer extends URIMatcher {
	// the string that describes the output pattern.
	private String pattern;

	/**
	 * The constructor.
	 * <p>
	 * The pattern specifies how to output the matching portions of the URI.
	 * The special tokens {scheme}, {host}, {port}, {path}, {fragment} and {uri} are 
	 * used in the pattern to identify where the matching portions of the URI or the entire
	 * URI are to be 
	 * inserted in the resulting string.  If the token is specified as noted above the entire
	 * portion will be inserted. However, if the token is followed by a colon and a number prior
	 * to the closing brace, the matching group from that section will be inserted.
	 * </p><p>
	 * For example: <br />
	 * setting the host match segment to <code>"(.*)\.example\.(.+)"</code> <br />
	 * setting the pattern to <code>"{host:2} has the {host:1} server"</code> <br />
	 * matching the URI <code>"http://www.example.com/foo"</code> <br />
	 * would yield the string <code>"com has the www server"</code>
	 * </p>
	 * @param pattern The pattern to change the URI to.
	 */
	public PatternReplacer(String pattern) {
		super();
		this.pattern = pattern;
	}

	/**
	 * A constructor that takes both a pattern and an existing URIMatcher.
	 * @param pattern The pattern to use for replacement.
	 * @param copy THe URIMatcher to use for matching.
	 */
	public PatternReplacer(String pattern, URIMatcher copy) {
		super(copy);
		this.pattern = pattern;
	}

	@Override
	public PatternReplacer setScheme(String scheme) {
		super.setScheme(scheme);
		return this;
	}

	@Override
	public PatternReplacer setHost(String host) {
		super.setHost(host);
		return this;
	}

	@Override
	public PatternReplacer setPort(Integer port) {
		super.setPort(port);
		return this;
	}

	@Override
	public PatternReplacer setFragment(String fragment) {
		super.setFragment(fragment);
		return this;
	}

	@Override
	public PatternReplacer setPath(String path) {
		super.setPath(path);
		return this;
	}

	/**
	 * Populate the pattern with the portions of the URI.
	 * @param uri The uri to populate the pattern with.
	 * @return the pattern with the tags replaced
	 */
	public String populate(URI uri) {

		String retval = pattern;

		while (retval.contains("{scheme")) {
			retval = subEdit(retval, "{scheme", uri.getScheme(), getScheme());
		}
		while (retval.contains("{host")) {
			retval = subEdit(retval, "{host", uri.getHost(), getHost());
		}
		while (retval.contains("{port}")) {
			retval = retval.replace("{port}", Integer.toString(uri.getPort()));	
		}

		while (retval.contains("{path")) {
			retval = subEdit(retval, "{path", uri.getPath(), getPath());
		}
		while (retval.contains("{fragment")) {
			retval = subEdit(retval, "{fragment", uri.getPath(), getFragment());
		}
		while (retval.contains("{uri}")) {
			retval = retval.replace("{uri}", uri.toString());
		}
		return retval;
	}

	// Performs the replacement on a sub section of the pattern
	private String subEdit(String retval, String pattern, String value,
			Pattern p) {
		int pos = retval.indexOf(pattern) + pattern.length();
		if (retval.charAt(pos) == '}') {
			return retval.replace(pattern + "}", value);

		}
		if (retval.charAt(pos) == ':') {
			int endpos = retval.indexOf('}', pos);
			String idxStr = retval.substring(pos + 1, endpos);
			int patternIdx = Integer.valueOf(idxStr);
			try {
				Matcher m = p.matcher(value);
				m.find();
				return retval.replace(
						retval.substring(retval.indexOf(pattern), endpos + 1),
						m.group(patternIdx));
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		throw new IllegalArgumentException(String.format(
				"Bad pattern string: %s", pattern));
	}

	@Override
	public String toString() {
		return String.format("URIEditor[ regEx=%s  pattern=%s]",
				super.toString(), pattern);
	}

}
