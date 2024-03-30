/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.example.autotest.google;

import io.github.arthurc.autotest.AutotestScoped;
import io.github.arthurc.autotest.selenium.SeleniumBrowserProperties;
import io.github.arthurc.autotest.web.Browser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GoogleExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleExampleApplication.class, args);
	}

	@ConfigurationProperties("example.google.browser")
	@Bean
	SeleniumBrowserProperties seleniumBrowserProperties() {
		return new SeleniumBrowserProperties();
	}

	@Bean
	@AutotestScoped
	Browser browser(SeleniumBrowserProperties properties) {
		return properties.initializeBrowserBuilder().build();
	}
}
