/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google.tests;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.example.autotest.google.GooglePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AutotestTest
class SearchTests {

	@Autowired
	private GooglePage googlePage;

	@Test
	void searchTest() {
		this.googlePage.search("test");
	}

}
