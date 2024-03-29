/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.commandexecution;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A command lifecycle is a lifecycle that represents a command that can be executed.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class CommandExecutionLifecycle extends Lifecycle {
	private final String name;
	private final List<Parameter> parameters = new ArrayList<>();

	/**
	 * Creates a new command lifecycle with the specified name and no parameters.
	 *
	 * @param name The name of the command.
	 */
	public CommandExecutionLifecycle(String name) {
		this(name, List.of());
	}

	/**
	 * Creates a new command lifecycle with the specified name and parameters.
	 *
	 * @param name       The name of the command.
	 * @param parameters The parameters of the command.
	 */
	public CommandExecutionLifecycle(String name, List<Parameter> parameters) {
		this.name = name;
		this.parameters.addAll(parameters);
	}

	/**
	 * Creates a new command lifecycle with the specified name and parameters.
	 * This constructor is a convenience constructor that takes an array of positional parameters.
	 *
	 * @param name       The name of the command.
	 * @param parameters The parameters of the command.
	 */
	public CommandExecutionLifecycle(String name, String[] parameters) {
		this(name, Stream.of(parameters)
				.map(Parameter::new)
				.toList());
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

}
