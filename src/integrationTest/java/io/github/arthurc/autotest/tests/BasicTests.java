/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.tests;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.autotest.web.Browser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@AutotestTest
class BasicTests {

	@Autowired
	private Browser browser;

	@Test
	void basicTest() {
		this.browser.visit("/basic.html");

		assertThat(this.browser.find("#basic"))
				.containsText("Foo")
				.containsText("foo")
				.containsText("FOO")
				.doesNotContainText("Bar");
	}

}
