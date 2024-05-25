/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.eventhandling.CommandFunction;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import io.github.arthurc.autotest.teststage.TestStage;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Provides functions to set the state of the run model.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class RunModel {

	public static CommandFunction<Event> setRunStarted(RunId run) {
		return events -> Stream.of(new RunEvent.RunStarted(run));
	}

	public static CommandFunction<Event> setTestPlan(RunId run, TestPlanModel testPlan) {
		return events -> Stream.of(new RunEvent.TestPlanSet(run, testPlan));
	}

	public static CommandFunction<Event> setTestExecutionStarted(RunId run, TestId testId) {
		return events -> Stream.of(new RunEvent.TestExecutionStarted(run, testId));
	}

	public static CommandFunction<Event> setTestExecutionEnded(RunId run, TestId testId) {
		return events -> Stream.of(new RunEvent.TestExecutionEnded(run, testId));
	}

	public static CommandFunction<Event> setTestStageStarted(RunId run, TestId testId, TestStage stage) {
		return events -> Stream.of(new RunEvent.TestStageStarted(run, testId, stage));
	}

	public static CommandFunction<Event> setCommandStarted(RunId run, TestId test, TestStage testStage, CommandId commandId, CommandId parentId, String name, Map<String, String> parameters, String subject) {
		return events -> Stream.of(new RunEvent.CommandStarted(run, test, testStage, commandId, parentId, name, parameters, subject));
	}

	public static CommandFunction<Event> setCommandEnded(RunId run, CommandId commandId, String subject) {
		return events -> Stream.of(new RunEvent.CommandEnded(run, commandId, subject));
	}

	public static CommandFunction<Event> setRunEnded(RunId run) {
		return events -> Stream.of(new RunEvent.RunEnded(run));
	}
}
