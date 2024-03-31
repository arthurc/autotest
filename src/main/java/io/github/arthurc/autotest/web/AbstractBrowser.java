/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;

import java.util.Objects;

/**
 * An abstract browser that provides a base implementation for browsers.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class AbstractBrowser implements Browser {
	private final BaseUrl baseUrl;

	protected AbstractBrowser(BaseUrl baseUrl) {
		this.baseUrl = baseUrl;
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
				.run(lifecycle -> doVisit(lifecycle, resolvedUrl));
	}

	@Override
	public Element find(String selector) {
		return CommandExecutionLifecycle.builder()
				.name("find")
				.parameter("css", selector)
				.build()
				.call(lifecycle -> doFind(lifecycle, selector));
	}

	protected abstract void doVisit(CommandExecutionLifecycle lifecycle, String url);

	protected abstract Element doFind(CommandExecutionLifecycle lifecycle, String selector);

}
