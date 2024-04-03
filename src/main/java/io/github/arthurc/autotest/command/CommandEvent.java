/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.command;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;

/**
 * Lifecycle events that are specific for {@link Command}s.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface CommandEvent extends LifecycleEvent {
	/**
	 * An event that occurs when the parameters of a command are modified.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record ParametersModified(Command lifecycle) implements CommandEvent {
	}

	/**
	 * An event that occurs when the subject of a command is changed.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record SubjectChanged(Command lifecycle) implements CommandEvent {
	}
}
