/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import io.github.arthurc.autotest.command.Command;

/**
 * An abstract element implementation that provides a default implementation for {@link Element}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class AbstractElement implements Element {

	@Override
	public void click() {
		Command.builder()
				.name("click")
				.subject(this)
				.build()
				.run(this::doClick);
	}

	@Override
	public void type(String text) {
		Command.builder()
				.name("type")
				.parameter("text", text)
				.subject(this)
				.build()
				.run(() -> doType(text));
	}

	protected abstract void doClick();

	protected abstract void doType(String text);
}
