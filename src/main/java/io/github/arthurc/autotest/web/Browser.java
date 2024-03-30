/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

/**
 * A browser is an abstraction of a web browser.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface Browser {
	/**
	 * Visits the specified URL.
	 *
	 * @param url The URL to visit.
	 * @throws NullPointerException If the URL is null.
	 */
	void visit(String url);
}
