/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;
import io.github.arthurc.autotest.web.Browser;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

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
				.run(() -> doSearch(keyword));
	}

	private void doSearch(String text) {
		this.browser.visit("/");
		this.browser.query("[aria-modal=\"true\"]").ifPresent(element -> {
			element.type("{tab}");
			this.browser.getFocused().type("{tab}");
			this.browser.getFocused().type("{tab}");
			this.browser.getFocused().type("{tab}");
			this.browser.getFocused().click();
		});
		this.browser.find("[autofocus]").type(text + "{enter}");

		assertThat(this.browser.find("h3").getText()).containsIgnoringCase(text);
	}

}
