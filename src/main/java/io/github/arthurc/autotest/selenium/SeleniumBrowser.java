/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;
import io.github.arthurc.autotest.web.BaseUrl;
import io.github.arthurc.autotest.web.Browser;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class SeleniumBrowser implements Browser, AutoCloseable {
	private final WebDriver webDriver;
	private final BaseUrl baseUrl;

	private SeleniumBrowser(Builder builder) {
		this.webDriver = builder.webDriver;
		this.baseUrl = builder.baseUrl;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void visit(String url) {
		Objects.requireNonNull(url, "Url needs to be provided");

		String resolvedUrl = this.baseUrl.resolve(url).toString();

		CommandExecutionLifecycle.builder()
				.name("visit")
				.parameter("url", resolvedUrl)
				.subject(this)
				.build()
				.run(() -> this.webDriver.navigate().to(this.baseUrl.resolve(url).toString()));
				.run(() -> this.webDriver.navigate().to(resolvedUrl));
	}

	@Override
	public void close() {
		this.webDriver.quit();
	}

	public static class Builder {
		private WebDriver webDriver;
		private BaseUrl baseUrl;

		private Builder() {
		}

		public Builder webDriver(WebDriver webDriver) {
			this.webDriver = webDriver;
			return this;
		}

		public Builder baseUrl(BaseUrl baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public SeleniumBrowser build() {
			return new SeleniumBrowser(this);
		}
	}

}
