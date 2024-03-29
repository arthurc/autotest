/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.commandexecution;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;

/**
 * Lifecycle events that are specific for {@link CommandExecutionLifecycle}s.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface CommandExecutionLifecycleEvent extends LifecycleEvent {
	/**
	 * An event that occurs when the parameters of a command are modified.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record ParametersModified(CommandExecutionLifecycle<?> lifecycle) implements CommandExecutionLifecycleEvent {
	}

	/**
	 * An event that occurs when the subject of a command execution is changed.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record SubjectChanged(CommandExecutionLifecycle<?> lifecycle) implements CommandExecutionLifecycleEvent {
	}
}
