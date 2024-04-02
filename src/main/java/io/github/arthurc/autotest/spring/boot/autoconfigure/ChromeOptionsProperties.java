/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

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
		return this.arguments;
	}

	public void toChromeOptions(ChromeOptions chromeOptions) {
		PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		propertyMapper.from(this.arguments).to(chromeOptions::addArguments);
		propertyMapper.from(this.binary).to(chromeOptions::setBinary);
	}
}
