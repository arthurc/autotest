/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

public class ElementAssert extends AbstractElementAssert<ElementAssert> {
	protected ElementAssert(Element element) {
		super(element, ElementAssert.class);
	}
}
