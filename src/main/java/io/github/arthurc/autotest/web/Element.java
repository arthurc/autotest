/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

/**
 * An element is a web element that can be interacted with, such as a button or a text field.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface Element {
	/**
	 * Performs a click on the element. This is equivalent to clicking the element with a mouse.
	 */
	void click();
}
