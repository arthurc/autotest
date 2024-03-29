/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.commandexecution;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A command lifecycle is a lifecycle that represents a command that can be executed.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class CommandExecutionLifecycle<T> extends Lifecycle {
	private final String name;
	private final List<Parameter> parameters = new ArrayList<>();
	private Subject<T> subject;

	private CommandExecutionLifecycle(Builder<T> builder) {
		this.name = Objects.requireNonNull(builder.name, "name has to be set");
		this.parameters.addAll(builder.parameters);
		this.subject = new Subject<>(builder.subject);
	}

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the parameters of the command as an unmodifiable list.
	 *
	 * @return The parameters of the command.
	 */
	public List<Parameter> getParameters() {
		return Collections.unmodifiableList(this.parameters);
	}

	/**
	 * Gets the subject of the command.
	 *
	 * @return The subject of the command.
	 */
	public Optional<T> getSubject() {
		return Optional.ofNullable(this.subject.subject());
	}

	/**
	 * Adds the specified parameters to the list of parameters.
	 *
	 * @param parameters The parameters to add.
	 */
	public void addParameters(Parameter... parameters) {
		if (parameters == null || parameters.length == 0) {
			return;
		}

		this.parameters.addAll(List.of(parameters));
		publish(new CommandExecutionLifecycleEvent.ParametersModified(this));
	}

	/**
	 * Sets the subject of the command.
	 *
	 * @param subject The subject of the command.
	 */
	public void setSubject(T subject) {
		if (!Objects.equals(this.subject.subject(), subject)) {
			this.subject = new Subject<>(subject);
			publish(new CommandExecutionLifecycleEvent.SubjectChanged(this));
		}
	}

	public static class Builder<T> {
		private String name;
		private final List<Parameter> parameters = new ArrayList<>();
		private T subject;

		public Builder<T> name(String name) {
			this.name = name;
			return this;
		}

		public Builder<T> parameter(String tag, String name) {
			return addParameters(new Parameter(tag, name));
		}

		public Builder<T> addParameters(Parameter... parameters) {
			this.parameters.addAll(List.of(parameters));
			return this;
		}

		public Builder<T> addParameters(String... parameters) {
			Stream.of(parameters)
					.map(Parameter::new)
					.forEach(this.parameters::add);
			return this;
		}

		public Builder<T> subject(T subject) {
			this.subject = subject;
			return this;
		}

		public CommandExecutionLifecycle<T> build() {
			return new CommandExecutionLifecycle<>(this);
		}
	}
}
