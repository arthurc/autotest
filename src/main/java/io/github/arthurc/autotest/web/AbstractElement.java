/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import io.github.arthurc.autotest.commandexecution.CommandExecutionLifecycle;

/**
 * An abstract element implementation that provides a default implementation for {@link Element}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class AbstractElement implements Element {

	@Override
	public void click() {
		CommandExecutionLifecycle.builder()
				.name("click")
				.subject(this)
				.build()
				.run(this::doClick);
	}

	protected abstract void doClick();
}
