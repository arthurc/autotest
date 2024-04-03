/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import org.assertj.core.api.AssertProvider;

/**
 * An element is a web element that can be interacted with, such as a button or a text field.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface Element extends AssertProvider<AbstractElementAssert<?>> {
	/**
	 * Performs a click on the element. This is equivalent to clicking the element with a mouse.
	 */
	void click();

	/**
	 * Enters the specified text into the element. This is equivalent to typing the text into the element.
	 *
	 * @param text The text to enter.
	 */
	void type(String text);

	/**
	 * Gets the text of the element.
	 *
	 * @return The text of the element.
	 */
	String getText();

	@Override
	default AbstractElementAssert<?> assertThat() {
		return new ElementAssert(this);
	}
}
