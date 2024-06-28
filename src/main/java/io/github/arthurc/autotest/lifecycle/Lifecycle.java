/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A lifecycle is a thread scoped execution of code that has a start and an end.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public abstract class Lifecycle {
	private static final Logger log = Logger.getLogger(Lifecycle.class.getName());

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

	static {
		ServiceLoader.load(Lifecycle.class).forEach(Lifecycle::begin);
	}

	private Lifecycle parent;
	private final Attributes attributes = new Attributes();

	/**
	 * Finds a lifecycle of the specified type and returns it as an {@link Optional}.
	 * If no lifecycle of the specified type is found, an empty {@link Optional} is returned.
	 *
	 * @param type The type of the lifecycle to find.
	 * @param <T>  The type of the lifecycle.
	 * @return An {@link Optional} containing the lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public static <T extends Lifecycle> Optional<T> find(Class<T> type) {
		return find(type, t -> true);
	}

	/**
	 * Finds a lifecycle of the specified type that satisfies the specified predicate and returns it as an {@link Optional}.
	 * If no lifecycle of the specified type is found, an empty {@link Optional} is returned.
	 *
	 * @param type      The type of the lifecycle to find.
	 * @param predicate The predicate to satisfy.
	 * @param <T>       The type of the lifecycle.
	 * @return An {@link Optional} containing the lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public static <T extends Lifecycle> Optional<T> find(Class<T> type, Predicate<T> predicate) {
		return CALLSTACK.get().stream()
				.filter(type::isInstance)
				.map(type::cast)
				.filter(predicate)
				.findFirst();
	}

	/**
	 * Gets a lifecycle of the specified type. If no lifecycle of the specified type is found, a
	 * {@link NoSuchLifecycleException} is thrown.
	 *
	 * @param type The type of the lifecycle to get.
	 * @param <T>  The type of the lifecycle.
	 * @return The lifecycle, never {@code null}.
	 */
	public static <T extends Lifecycle> T get(Class<T> type) {
		return find(type).orElseThrow(NoSuchLifecycleException::new);
	}

	/**
	 * Gets a lifecycle of the specified type that satisfies the specified predicate. If no lifecycle of the specified
	 * type is found, a {@link NoSuchLifecycleException} is thrown.
	 *
	 * @param type      The type of the lifecycle to get.
	 * @param predicate The predicate to satisfy.
	 * @param <T>       The type of the lifecycle.
	 * @return The lifecycle, never {@code null}.
	 */
	public static <T extends Lifecycle> T get(Class<T> type, Predicate<T> predicate) {
		return find(type, predicate).orElseThrow(NoSuchLifecycleException::new);
	}

	/**
	 * Gets an attribute from the lifecycle.
	 *
	 * @param name The name of the attribute.
	 * @return The attribute, or {@code null} if the attribute does not exist.
	 */
	public Object getAttribute(String name) {
		return this.attributes.getAttribute(name);
	}

	/**
	 * Sets an attribute on the lifecycle.
	 *
	 * @param name  The name of the attribute.
	 * @param value The attribute value.
	 */
	public void setAttribute(String name, Object value) {
		this.attributes.setAttribute(name, value);
	}

	/**
	 * Removes an attribute from the lifecycle.
	 *
	 * @param name The name of the attribute.
	 * @return The removed attribute value, or {@code null} if the attribute did not exist.
	 */
	public Object removeAttribute(String name) {
		return this.attributes.removeAttribute(name);
	}

	/**
	 * Registers a destruction callback on the lifecycle.
	 * This callback will be executed when the lifecycle ends.
	 *
	 * @param name     The name of the callback.
	 * @param callback The callback to register.
	 */
	public void registerDestructionCallback(String name, Runnable callback) {
		this.attributes.registerDestructionCallback(name, callback);
	}

	/**
	 * Runs a task within the lifecycle. The lifecycle publishes events before and after the task is run.
	 *
	 * @param task The task to run.
	 */
	public void run(Runnable task) {
		call(() -> {
			task.run();
			return null;
		});
	}

	/**
	 * Calls an action within the lifecycle. The lifecycle publishes events before and after the action is called along
	 * with the result of the action.
	 *
	 * @param action The action to call.
	 * @param <T>    The type of the result.
	 * @return The result of the action.
	 */
	public <T> T call(Callable<T> action) {
		begin();
		try {
			var value = action.call();
			end(new LifecycleResult.Ok(value));
			return value;
		} catch (RuntimeException | Error e) {
			end(new LifecycleResult.Error(e));
			throw e;
		} catch (Throwable e) {
			end(new LifecycleResult.Error(e));
			throw new LifecycleException("An exception occurred during the lifecycle action", e);
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

		synchronized (this) {
			publish(new LifecycleEvent.BeforeBegin(this));
			this.parent = CALLSTACK.get().peek();
			CALLSTACK.get().push(this);
			publish(new LifecycleEvent.AfterBegin(this));
		}
	}

	/**
	 * Ends the lifecycle. This method publishes a {@link LifecycleEvent.BeforeEnd} event before it is detached from the
	 * lifecycle callstack, and a {@link LifecycleEvent.AfterEnd} event after it is detached from the lifecycle.
	 *
	 * @throws IllegalStateException If the lifecycle is not the current lifecycle on the callstack.
	 */
	public void end(LifecycleResult result) {
		Lifecycle currentLifecycle = CALLSTACK.get().peek();
		if (currentLifecycle != this) {
			throw new IllegalStateException("Lifecycle is not the current lifecycle on the callstack. Current lifecycle is %s, expected %s.".formatted(
					currentLifecycle != null ? currentLifecycle.getClass() : "null",
					getClass()));
		}

		synchronized (this) {
			publish(new LifecycleEvent.BeforeEnd(this, result));
			this.attributes.executeDestructionCallbacks();
			CALLSTACK.get().pop();
			this.parent = null;
			publish(new LifecycleEvent.AfterEnd(this));
		}
	}

	/**
	 * Ends the lifecycle with a void result. This method is a convenience method for {@link #end(LifecycleResult)}.
	 */
	public void end() {
		end(LifecycleResult.VOID);
	}

	/**
	 * Finds a parent lifecycle to this lifecycle of the specified type and matching the specified predicate.
	 * If no parent lifecycle is found, an empty {@link Optional} is returned.
	 *
	 * @param type      The type of the parent lifecycle to find.
	 * @param predicate The predicate to satisfy.
	 * @return An {@link Optional} containing the parent lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public <T extends Lifecycle> Optional<T> findParent(Class<T> type, Predicate<T> predicate) {
		return this.parent != null
				? this.parent.findSelfOrParent(type, predicate)
				: Optional.empty();
	}

	/**
	 * Finds a parent lifecycle to this lifecycle of the specified type.
	 * If no parent lifecycle is found, an empty {@link Optional} is returned.
	 *
	 * @param type The type of the parent lifecycle to find.
	 * @return An {@link Optional} containing the parent lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public <T extends Lifecycle> Optional<T> findParent(Class<T> type) {
		return findParent(type, t -> true);
	}

	/**
	 * Finds a lifecycle of the specified type and matching the specified predicate in the lifecycle stack relative to
	 * this lifecycle. If no lifecycle is found, an empty {@link Optional} is returned.
	 *
	 * @param type      The type of the lifecycle to find.
	 * @param predicate The predicate to satisfy.
	 * @param <T>       The type of the lifecycle.
	 * @return An {@link Optional} containing the lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public <T extends Lifecycle> Optional<T> findSelfOrParent(Class<T> type, Predicate<T> predicate) {
		for (var p = this; p != null; p = p.parent) {
			if (type.isInstance(p) && predicate.test(type.cast(p))) {
				return Optional.of(type.cast(p));
			}
		}
		return Optional.empty();
	}

	/**
	 * Finds a lifecycle of the specified type in the lifecycle stack relative to this lifecycle.
	 *
	 * @param type The type of the lifecycle to find.
	 * @param <T>  The type of the lifecycle.
	 * @return An {@link Optional} containing the lifecycle if found, otherwise an empty {@link Optional}.
	 */
	public <T extends Lifecycle> Optional<T> findSelfOrParent(Class<T> type) {
		return findSelfOrParent(type, t -> true);
	}

	protected void publish(LifecycleEvent event) {
		try {
			onLifecycleEvent(event);
		} catch (RuntimeException e) {
			log.log(Level.WARNING, "An exception occurred during lifecycle event publication", e);
		}
		if (this.parent != null) {
			this.parent.publish(event);
		}
	}

	protected void onLifecycleEvent(LifecycleEvent event) {
	}

}
