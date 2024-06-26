/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.model;

import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.teststage.TestStageLifecycle;

import java.util.stream.Stream;

/**
 * Provides functions to set the state of the run model.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class RunModel {

	public static CommandFunction on(RunId runId, LifecycleEvent event) {
		return events -> switch (event) {
			case LifecycleEvent.AfterBegin afterBegin -> switch (afterBegin.lifecycle()) {
				case Run __ -> Stream.of(new RunEvent.RunStarted(runId));
				case TestPlanLifecycle lifecycle -> Stream.of(new RunEvent.TestPlanSet(runId, lifecycle.getTestPlan()));
				case TestExecutionLifecycle lifecycle ->
						Stream.of(new RunEvent.TestExecutionStarted(runId, lifecycle.getTestId()));
				case TestStageLifecycle lifecycle ->
						Stream.of(new RunEvent.TestStageStarted(runId, lifecycle.getTestId(), lifecycle.getTestStage()));
				case Command command -> Stream.of(new RunEvent.CommandStarted(runId,
						command.getTest().getTestId(),
						command.getTestStage().map(TestStageLifecycle::getTestStage).orElse(null),
						command.getId(),
						command.getParentCommand().map(Command::getId).orElse(null),
						command.getName(),
						command.getParameters(),
						command.getSubject().map(o -> Integer.toString(o.hashCode())).orElse(null)));
				default -> Stream.empty();
			};
			case LifecycleEvent.BeforeEnd beforeEnd -> switch (beforeEnd.lifecycle()) {
				case Run __ -> Stream.of(new RunEvent.RunEnded(runId));
				case TestExecutionLifecycle lifecycle ->
						Stream.of(new RunEvent.TestExecutionEnded(runId, lifecycle.getTestId()));
				case Command command -> Stream.of(new RunEvent.CommandEnded(runId,
						command.getId(),
						command.getSubject().map(o -> Integer.toString(o.hashCode())).orElse(null)));
				default -> Stream.empty();
			};
			default -> Stream.empty();
		};
	}
}
