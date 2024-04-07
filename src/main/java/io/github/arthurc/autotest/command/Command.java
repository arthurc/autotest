/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.command;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * A command is a lifecycle that represents a command that can be executed.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class Command extends Lifecycle {
	private final CommandId id = new CommandId();
	private final String name;
	private final Map<String, String> parameters = new LinkedHashMap<>();
	private Object subject;

	private Command(Builder builder) {
		this.name = Objects.requireNonNull(builder.name, "name has to be set");
		this.parameters.putAll(builder.parameters);
		this.subject = builder.subject;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static void run(String name, Map<String, String> parameters, Runnable task) {
		builder().name(name).parameters(parameters).build().run(task);
	}

	public static <T> T call(String name, Map<String, String> parameters, Callable<T> action) {
		return builder().name(name).parameters(parameters).build().call(action);
	}

	public CommandId getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the parameters of the command as an unmodifiable list.
	 *
	 * @return The parameters of the command.
	 */
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	/**
	 * Gets the subject of the command.
	 *
	 * @return The subject of the command.
	 */
	public Optional<Object> getSubject() {
		return Optional.ofNullable(this.subject);
	}

	/**
	 * Adds the specified parameters to the list of parameters.
	 *
	 * @param parameters The parameters to add.
	 */
	public void addParameters(Map<String, String> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			return;
		}

		this.parameters.putAll(parameters);
		publish(new CommandEvent.ParametersModified(this));
	}

	/**
	 * Sets the subject of the command.
	 *
	 * @param subject The subject of the command.
	 */
	public void setSubject(Object subject) {
		if (!Objects.equals(this.subject, subject)) {
			this.subject = subject;
			publish(new CommandEvent.SubjectChanged(this));
		}
	}

	public static class Builder {
		private String name;
		private final Map<String, String> parameters = new LinkedHashMap<>();
		private Object subject;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder parameter(String tag, String name) {
			return parameters(Map.of(tag, name));
		}

		public Builder parameters(Map<String, String> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public Builder subject(Object subject) {
			this.subject = subject;
			return this;
		}

		public Command build() {
			return new Command(this);
		}
	}
}
