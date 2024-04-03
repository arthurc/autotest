/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import java.net.URI;
import java.util.Objects;

/**
 * A base URL is a URL that is used as a base for other URLs.
 * It can be used to resolve relative URLs according to sensible rules.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class BaseUrl {
	private final URI url;

	/**
	 * Creates a new base URL with the specified URL.
	 *
	 * @param url The base URL.
	 * @throws NullPointerException If the URL is null.
	 */
	public BaseUrl(String url) {
		Objects.requireNonNull(url, "Url needs to be provided");

		this.url = URI.create(url.replaceAll("/+$", ""));
	}

	/**
	 * Resolves the specified path according to the base URL.
	 * If the path is an absolute URL, it is returned as is.
	 * If the path is a relative URL, it is resolved according to the base URL.
	 *
	 * @param path The path to resolve.
	 * @return The resolved URI.
	 */
	public URI resolve(String path) {
		if (path == null || path.isEmpty() || path.chars().allMatch(c -> c == '/')) {
			return this.url;
		}

		URI uri = URI.create(path);
		if (uri.isAbsolute()) {
			return uri;
		}

		return URI.create(String.join("/",
				this.url.toString(),
				path.replaceAll("^/+", "")));
	}
}
