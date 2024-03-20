/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

/**
 * Events that occur during the lifecycle of a {@link Lifecycle}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public sealed interface LifecycleEvent {
	/**
	 * The lifecycle that the event is related to.
	 *
	 * @return The lifecycle.
	 */
	Lifecycle lifecycle();

	/**
	 * An event that occurs before the lifecycle begins and before it is attached to the
	 * lifecycle callstack.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record BeforeBegin(Lifecycle lifecycle) implements LifecycleEvent {
	}

	/**
	 * An event that occurs after the lifecycle begins and after it is attached to the lifecycle callstack.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record AfterBegin(Lifecycle lifecycle) implements LifecycleEvent {
	}

	/**
	 * An event that occurs before the lifecycle ends, and before it is detached from the lifecycle callstack.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record BeforeEnd(Lifecycle lifecycle, LifecycleResult result) implements LifecycleEvent {
	}

	/**
	 * An event that occurs after the lifecycle ends, and after it is detached from the lifecycle callstack.
	 *
	 * @param lifecycle The lifecycle.
	 */
	record AfterEnd(Lifecycle lifecycle) implements LifecycleEvent {
	}
}
