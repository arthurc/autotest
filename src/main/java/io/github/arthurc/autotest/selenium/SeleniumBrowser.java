/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.AbstractBrowser;
import io.github.arthurc.autotest.web.BaseUrl;
import io.github.arthurc.autotest.web.Element;
import io.github.arthurc.autotest.web.ElementNotFoundException;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

import static org.awaitility.Awaitility.await;

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
	protected Element doFind(String selector) {
		try {
			return new SeleniumElement(await()
					.ignoreException(NoSuchElementException.class)
					.until(() -> this.webDriver.findElement(By.cssSelector(selector)), Objects::nonNull));
		} catch (ConditionTimeoutException e) {
			throw new ElementNotFoundException("Element not found: " + selector);
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
