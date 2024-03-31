/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import java.util.Optional;

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

	/**
	 * Finds an element by the specified CSS selector. If no element is found, an {@link ElementNotFoundException} is thrown.
	 *
	 * @param selector The CSS selector to find the element by.
	 * @return A reference to the element.
	 * @throws ElementNotFoundException If no element is found.
	 */
	Element find(String selector);

	/**
	 * Queries for an element by the specified CSS selector. If no element is found, an empty optional is returned.
	 *
	 * @param selector The CSS selector to query for.
	 * @return An optional containing the element if it was found.
	 */
	Optional<Element> query(String selector);

	/**
	 * Gets the element that currently has focus. If no element has focus, a {@link ElementNotFoundException} is thrown.
	 *
	 * @return The element that currently has focus.
	 * @throws ElementNotFoundException If no element has focus.
	 */
	Element getFocused();
}
