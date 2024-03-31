/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;
import io.github.arthurc.autotest.web.Element;
import org.openqa.selenium.WebElement;

class SeleniumElement implements Element {
	private final WebElement element;

	SeleniumElement(WebElement element) {
		this.element = element;
	}

	@Override
	public void click() {
		CommandExecutionLifecycle.builder()
				.name("click")
				.subject(this)
				.build()
				.run(this.element::click);
	}
}
