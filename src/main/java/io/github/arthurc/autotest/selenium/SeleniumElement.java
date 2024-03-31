/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.AbstractElement;
import org.openqa.selenium.WebElement;

class SeleniumElement extends AbstractElement {
	private final WebElement element;

	SeleniumElement(WebElement element) {
		this.element = element;
	}

	@Override
	protected void doClick() {
		this.element.click();
	}
}
