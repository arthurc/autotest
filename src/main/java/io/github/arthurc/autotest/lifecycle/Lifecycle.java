/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A lifecycle is a thread scoped execution of code that has a start and an end.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class Lifecycle {
	private static final ThreadLocal<Deque<Lifecycle>> CALLSTACK = new InheritableThreadLocal<>() {
		@Override
		protected Deque<Lifecycle> initialValue() {
			return new LinkedList<>();
		}

		@Override
		protected Deque<Lifecycle> childValue(Deque<Lifecycle> parentValue) {
			return new LinkedList<>(parentValue);
		}
	};

	private Lifecycle parent;

	/**
	 * Runs a task within the lifecycle. The lifecycle publishes events before and after the task is run.
	 *
	 * @param task The task to run.
	 */
	public void run(Runnable task) {
		publish(new LifecycleEvent.BeforeBegin(this));
		this.parent = CALLSTACK.get().peek();
		CALLSTACK.get().push(this);
		publish(new LifecycleEvent.AfterBegin(this));
		try {
			task.run();
		} finally {
			publish(new LifecycleEvent.BeforeEnd(this));
			CALLSTACK.get().pop();
			this.parent = null;
			publish(new LifecycleEvent.AfterEnd(this));
		}
	}

	private void publish(LifecycleEvent event) {
		onLifecycleEvent(event);
		if (this.parent != null) {
			this.parent.publish(event);
		}
	}

	protected void onLifecycleEvent(LifecycleEvent event) {
	}
}
