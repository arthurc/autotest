/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google;

import io.github.arthurc.autotest.spring.scope.AutotestScoped;
import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.web.Browser;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@AutotestScoped
public class GooglePage {

	private final Browser browser;

	public GooglePage(Browser browser) {
		this.browser = browser;
	}

	public void search(String keyword) {
		Command.run("search", Map.of("keyword", keyword), () -> doSearch(keyword));
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

		assertThat(this.browser.find("h3")).containsText(text);
	}

}
