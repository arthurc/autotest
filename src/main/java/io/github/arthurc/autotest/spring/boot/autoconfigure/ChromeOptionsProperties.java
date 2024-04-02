/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("autotest.browser.chrome")
public class ChromeOptionsProperties {
	private String binary;
	private final List<String> arguments = new ArrayList<>();

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public List<String> getArguments() {
		return arguments;
	}
}
