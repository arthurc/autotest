/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import io.github.arthurc.autotest.teststage.TestStage;

import java.util.Map;

public sealed interface RunEvent extends Event {
	RunId run();

	record RunStarted(
			RunId run
	) implements RunEvent {
	}

	record TestPlanSet(
			RunId run,
			TestPlanModel testPlan
	) implements RunEvent {
	}

	record TestExecutionStarted(
			RunId run,
			TestId testId
	) implements RunEvent {
	}

	record TestExecutionEnded(
			RunId run,
			TestId testId
	) implements RunEvent {
	}

	record CommandStarted(
			RunId run,
			TestId test,
			TestStage testStage,
			CommandId commandId,
			CommandId parentId,
			String name,
			Map<String, String> parameters,
			String subject
	) implements RunEvent {
	}

	record CommandEnded(
			RunId run,
			CommandId commandId,
			String subject
	) implements RunEvent {
	}

	record RunEnded(
			RunId run
	) implements RunEvent {
	}

	record TestStageStarted(
			RunId run,
			TestId testId,
			TestStage stage
	) implements RunEvent {
	}
}
