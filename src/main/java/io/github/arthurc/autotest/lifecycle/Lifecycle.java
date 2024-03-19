/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

/**
 * A lifecycle is a thread scoped execution of code that has a start and an end.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class Lifecycle {
	/**
	 * Runs a task within the lifecycle. The lifecycle publishes events before and after the task is run.
	 *
	 * @param task The task to run.
	 */
	public void run(Runnable task) {
		onLifecycleEvent(new LifecycleEvent.BeforeBegin(this));
		onLifecycleEvent(new LifecycleEvent.AfterBegin(this));
		try {
			task.run();
		} finally {
			onLifecycleEvent(new LifecycleEvent.BeforeEnd(this));
			onLifecycleEvent(new LifecycleEvent.AfterEnd(this));
		}
	}

	protected void onLifecycleEvent(LifecycleEvent event) {
	}
}
