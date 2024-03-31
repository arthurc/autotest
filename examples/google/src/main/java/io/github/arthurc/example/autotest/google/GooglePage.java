/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;
import io.github.arthurc.autotest.web.Browser;
import org.springframework.stereotype.Component;

@Component
public class GooglePage {

	private final Browser browser;

	public GooglePage(Browser browser) {
		this.browser = browser;
	}

	public void search(String keyword) {
		CommandExecutionLifecycle.builder()
				.name("search")
				.parameter("keyword", keyword)
				.build()
				.run(this::doSearch);
	}

	private void doSearch() {
		this.browser.visit("/");
		this.browser.find("[aria-modal=\"true\"] button").click();
	}

}
