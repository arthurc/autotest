/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

public abstract class BrowserProperties {
	/**
	 * The base URL of the browser.
	 */
	private BaseUrl baseUrl;

	public BaseUrl getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(BaseUrl baseUrl) {
		this.baseUrl = baseUrl;
	}

}
