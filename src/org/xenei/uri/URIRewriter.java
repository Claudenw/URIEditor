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
import java.net.URISyntaxException;

/**
 * A class to rewrite URIs using regular expressions and pattern strings.
 */
public class URIRewriter {
	// the editor to use.
	private PatternReplacer editor;

	/**
	 * Constructor.
	 * 
	 * Uses a PatternReplacer instance to edit the incoming URIs.  The PatternReplacer must
	 * be configured to produce valid URI strings.
	 * 
	 * @param editor The PatternReplacer the replacer to use. 
	 */
	public URIRewriter(PatternReplacer editor) {
		this.editor = editor;
	}

	/**
	 * Rewrite the URI as per the PatternReplacer.
	 * 
	 * If the PatternReplacer does <em>not</em> match the input URI the original URI is returned 
	 * unchanged.
	 * If the PatternReplace does match the results of the populate() method is used to construct
	 * a new URI
	 * @param uri The uri to edit
	 * @return The resulting uri.
	 * @throws URISyntaxException if the PatternMatcher does not generate a valid URI.
	 */
	public URI rewrite(URI uri) throws URISyntaxException {
		if (editor.matches(uri)) {
			return new URI(editor.populate(uri));
		}
		return uri;

	}

	@Override
	public String toString() {
		return String.format("URIRewriter[ %s  ]", editor);
	}

}
