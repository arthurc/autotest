/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.eventhandling.CommandFunction;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Provides functions to set the state of the run model.
 *
 * @since 1.0.0
 * @author Arthur Hartwig Carlsson
 */
public class RunModel {

	public static CommandFunction<Event> setTestPlan(TestPlanModel testPlan) {
		return events -> Stream.of(new Event.TestPlanSet(testPlan));
	}

	public static CommandFunction<Event> setTestExecutionStarted(TestId testId) {
		return events -> Stream.of(new Event.TestExecutionStarted(testId));
	}

	public static CommandFunction<Event> setTestExecutionEnded(TestId testId) {
		return events -> Stream.of(new Event.TestExecutionEnded(testId));
	}

	public static CommandFunction<Event> setCommandStarted(CommandId commandId, CommandId parentId, String name, Map<String, String> parameters) {
		return events -> Stream.of(new Event.CommandStarted(commandId, parentId, name, parameters));
	}

	public static CommandFunction<Event> setCommandEnded(CommandId commandId) {
		return events -> Stream.of(new Event.CommandEnded(commandId));
	}

	public static CommandFunction<Event> endRun() {
		return events -> Stream.of(new Event.RunEnded());
	}
}
