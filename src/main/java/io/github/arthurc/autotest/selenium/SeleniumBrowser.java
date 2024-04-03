/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.AbstractBrowser;
import io.github.arthurc.autotest.web.BaseUrl;
import io.github.arthurc.autotest.web.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class SeleniumBrowser extends AbstractBrowser implements AutoCloseable {
	private final WebDriver webDriver;

	private SeleniumBrowser(Builder builder) {
		super(builder.baseUrl);

		this.webDriver = builder.webDriver;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	protected void doVisit(String url) {
		this.webDriver.navigate().to(url);
	}

	@Override
	protected Optional<Element> doQuery(String selector) {
		try {
			return Optional.of(new SeleniumElement(this.webDriver.findElement(By.cssSelector(selector))));
		} catch (NoSuchElementException e) {
			return Optional.empty();
		}
	}

	@Override
	protected Optional<Element> doQueryFocused() {
		try {
			return Optional.of(new SeleniumElement(this.webDriver.switchTo().activeElement()));
		} catch (NoSuchElementException e) {
			return Optional.empty();
		}
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
