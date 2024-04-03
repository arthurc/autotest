/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

/**
 * An exception that is thrown when an element is not found.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ElementNotFoundException extends AssertionError {
	public ElementNotFoundException(String message) {
		super(message);
	}
}
