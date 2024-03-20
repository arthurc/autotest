/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
		begin();
		try {
			task.run();
		} finally {
			end();
		}
	}

	public <T> T call(Callable<T> action) {
		begin();
		try {
			return action.call();
		} catch (Exception e) {
			throw new LifecycleException("An exception occurred during the lifecycle action", e);
		} finally {
			end();
		}
	}

	/**
	 * Begins the lifecycle. This method publishes a {@link LifecycleEvent.BeforeBegin} event before it is attached to
	 * the lifecycle callstack, and a {@link LifecycleEvent.AfterBegin} event after it is attached to the lifecycle.
	 */
	public void begin() {
		if (!CALLSTACK.get().isEmpty() && this.parent != null) {
			throw new IllegalStateException("Lifecycle is already the current lifecycle on the callstack");
		}

		LifecycleException exception = new LifecycleException("An exception occurred during lifecycle event publication");
		publish(new LifecycleEvent.BeforeBegin(this), exception::addSuppressed);
		this.parent = CALLSTACK.get().peek();
		CALLSTACK.get().push(this);
		publish(new LifecycleEvent.AfterBegin(this), exception::addSuppressed);

		if (exception.getSuppressed().length > 0) {
			throw exception;
		}
	}

	/**
	 * Ends the lifecycle. This method publishes a {@link LifecycleEvent.BeforeEnd} event before it is detached from the
	 * lifecycle callstack, and a {@link LifecycleEvent.AfterEnd} event after it is detached from the lifecycle.
	 *
	 * @throws IllegalStateException If the lifecycle is not the current lifecycle on the callstack.
	 */
	public void end() {
		if (CALLSTACK.get().peek() != this) {
			throw new IllegalStateException("Lifecycle is not the current lifecycle on the callstack");
		}

		LifecycleException exception = new LifecycleException("An exception occurred during lifecycle event publication");
		publish(new LifecycleEvent.BeforeEnd(this), exception::addSuppressed);
		CALLSTACK.get().pop();
		this.parent = null;
		publish(new LifecycleEvent.AfterEnd(this), exception::addSuppressed);

		if (exception.getSuppressed().length > 0) {
			throw exception;
		}
	}

	private void publish(LifecycleEvent event, Consumer<RuntimeException> exceptionHandler) {
		try {
			onLifecycleEvent(event);
		} catch (RuntimeException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			} else {
				throw e;
			}
		}
		if (this.parent != null) {
			this.parent.publish(event, exceptionHandler);
		}
	}

	protected void onLifecycleEvent(LifecycleEvent event) {
	}

}
