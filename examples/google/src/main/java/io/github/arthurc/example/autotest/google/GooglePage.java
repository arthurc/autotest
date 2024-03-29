/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google;

import io.github.arthurc.autotest.AutotestScoped;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
@AutotestScoped
public class GooglePage {

	@PreDestroy
	void destroy() {
		System.out.println("Destroying GooglePage");
	}

	public String search(String keyword) {
		return "Search: " + keyword;
	}

}
