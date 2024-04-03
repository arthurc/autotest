/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import io.github.arthurc.autotest.command.Command;
import org.awaitility.core.ConditionTimeoutException;

import java.util.Objects;
import java.util.Optional;

import static org.awaitility.Awaitility.await;

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

		Command.builder()
				.name("visit")
				.parameter("url", resolvedUrl)
				.subject(this)
				.build()
				.run(() -> doVisit(resolvedUrl));
	}

	@Override
	public Element find(String selector) {
		Command command = Command.builder()
				.name("find")
				.parameter("css", selector)
				.subject(this)
				.build();
		return command.call(() -> {
			var element = doFind(selector);
			command.setSubject(element);
			return element;
		});
	}

	@Override
	public Optional<Element> query(String selector) {
		Command command = Command.builder()
				.name("get")
				.parameter("css", selector)
				.subject(this)
				.build();
		return command.call(() -> {
			var element = doQuery(selector);
			element.ifPresent(command::setSubject);
			return element;
		});
	}

	@Override
	public Element getFocused() {
		Command command = Command.builder()
				.name("get-focused")
				.subject(this)
				.build();
		return command.call(() -> {
			var element = doGetFocused();
			command.setSubject(element);
			return element;
		});
	}

	protected abstract void doVisit(String url);

	protected abstract Optional<Element> doQuery(String selector);

	protected abstract Optional<Element> doQueryFocused();

	protected Element doGetFocused() {
		return doQueryFocused().orElseThrow(() -> new ElementNotFoundException("No focused element found"));
	}

	protected Element doFind(String selector) {
		try {
			return await().until(() -> doQuery(selector).orElse(null), Objects::nonNull);
		} catch (ConditionTimeoutException e) {
			throw new ElementNotFoundException("Element not found: " + selector);
		}
	}

}
