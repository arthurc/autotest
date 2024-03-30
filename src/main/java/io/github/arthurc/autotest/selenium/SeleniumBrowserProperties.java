/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.web.BrowserProperties;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumBrowserProperties extends BrowserProperties {

	/**
	 * Initializes a {@link SeleniumBrowser.Builder} instance with the state of this instance.
	 *
	 * @return A {@link SeleniumBrowser.Builder} initialized with the state of this instance.
	 */
	public SeleniumBrowser.Builder initializeBrowserBuilder() {
		return SeleniumBrowser.builder()
				.webDriver(new ChromeDriver())
				.baseUrl(getBaseUrl());
	}

}
