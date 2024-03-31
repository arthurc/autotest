/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.AbstractElement;
import io.github.arthurc.autotest.web.InputText;
import io.github.arthurc.autotest.web.InputToken;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

class SeleniumElement extends AbstractElement {
	private final WebElement element;

	SeleniumElement(WebElement element) {
		this.element = element;
	}

	@Override
	public String getText() {
		return this.element.getText();
	}

	@Override
	protected void doClick() {
		this.element.click();
	}

	@Override
	protected void doType(String text) {
		new InputText(text).tokens().forEach(token -> {
			switch (token) {
				case InputToken.Text(String string) -> this.element.sendKeys(string);
				case InputToken.Key(String string) -> this.element.sendKeys(Keys.valueOf(string));
			}
		});
	}
}
