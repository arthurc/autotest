/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.xautotest.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.github.arthurc.autotest.spring.scope.AutotestScoped;
import io.github.arthurc.autotest.selenium.SeleniumBrowserProperties;
import io.github.arthurc.autotest.web.Browser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.URL;
import java.util.Objects;

@SpringBootApplication
public class IntegrationTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntegrationTestApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("tests.browser")
	SeleniumBrowserProperties browserProperties() {
		return new SeleniumBrowserProperties();
	}

	@Bean
	@AutotestScoped
	Browser browser(SeleniumBrowserProperties properties) {
		return properties.initializeBrowserBuilder().build();
	}

	@Bean(destroyMethod = "stop")
	WireMockServer wireMockServer() {
		URL rootUrl = Objects.requireNonNull(getClass().getResource("/wiremock"));

		WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration()
				.port(8090)
				.withRootDirectory(rootUrl.getPath()));
		wireMockServer.start();
		WireMock.configureFor(wireMockServer.port());
		return wireMockServer;
	}
}
