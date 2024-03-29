/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google.tests;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.example.autotest.google.GooglePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@AutotestTest
class SearchTests {

	@Autowired
	private GooglePage googlePage;

	@Test
	void searchTest() {
		String result = googlePage.search("test");

		assertThat(result).isEqualTo("Search: test");
	}

}
