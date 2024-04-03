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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A command is a lifecycle that represents a command that can be executed.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class Command extends Lifecycle {
	private final String name;
	private final List<Parameter> parameters = new ArrayList<>();
	private Object subject;

	private Command(Builder builder) {
		this.name = Objects.requireNonNull(builder.name, "name has to be set");
		this.parameters.addAll(builder.parameters);
		this.subject = builder.subject;
	}

	public static Builder builder() {
		return new Builder();
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
	public Optional<Object> getSubject() {
		return Optional.ofNullable(this.subject);
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

	public void run(Consumer<Command> c) {
		run(() -> c.accept(this));
	}

	public <T> T call(Function<Command, T> f) {
		return call(() -> f.apply(this));
	}

	public static class Builder {
		private String name;
		private final List<Parameter> parameters = new ArrayList<>();
		private Object subject;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder parameter(String tag, String name) {
			return addParameters(new Parameter(tag, name));
		}

		public Builder addParameters(Parameter... parameters) {
			this.parameters.addAll(List.of(parameters));
			return this;
		}

		public Builder addParameters(String... parameters) {
			Stream.of(parameters)
					.map(Parameter::new)
					.forEach(this.parameters::add);
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
