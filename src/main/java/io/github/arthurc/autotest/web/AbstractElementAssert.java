/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Strings;

public abstract class AbstractElementAssert<SELF extends AbstractElementAssert<SELF>> extends AbstractAssert<SELF, Element> {
	private final Strings strings = Strings.instance();

	protected AbstractElementAssert(Element element, Class<?> selfType) {
		super(element, selfType);
	}

	/**
	 * Asserts that the element contains the specified text. The text is case-insensitive.
	 * <p>
	 * Example:
	 * <pre><code class='java'> // assertion succeeds
	 * assertThat(browser.find("h1"))
	 *     .containsText(&quot;Hello, world!&quot;);</code></pre>
	 *
	 * @param text The text to assert.
	 * @return {@code this} assertion object.
	 */
	public SELF containsText(String text) {
		strings.assertContainsIgnoringCase(info, actual.getText(), text);
		return myself;
	}
}
