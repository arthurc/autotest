/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.BrowserProperties;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SeleniumBrowserProperties extends BrowserProperties implements ApplicationContextAware {

	private String driver = "chromeDriver";
	private ApplicationContext applicationContext;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Initializes a {@link SeleniumBrowser.Builder} instance with the state of this instance.
	 *
	 * @return A {@link SeleniumBrowser.Builder} initialized with the state of this instance.
	 */
	public SeleniumBrowser.Builder initializeBrowserBuilder() {
		return SeleniumBrowser.builder()
				.webDriver(this.applicationContext.getBean(this.driver, WebDriver.class))
				.baseUrl(getBaseUrl());
	}

}
