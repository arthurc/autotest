/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ChromeDriver.class)
class ChromeDriverConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@Scope("prototype")
	ChromeDriver chromeDriver(ChromeOptions chromeOptions) {
		return new ChromeDriver(chromeOptions);
	}

	@Bean
	@ConditionalOnMissingBean
	ChromeOptions chromeOptions(ChromeOptionsProperties properties) {
		ChromeOptions chromeOptions = new ChromeOptions();
		properties.toChromeOptions(chromeOptions);
		return chromeOptions;
	}
}
