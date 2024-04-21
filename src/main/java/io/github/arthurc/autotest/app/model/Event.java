/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;

import java.util.Map;

/**
 * Represents an event that can be emitted by the application.
 */
public sealed interface Event {

	record TestPlanSet(TestPlanModel testPlan) implements Event {
	}

	record TestExecutionStarted(TestId testId) implements Event {
	}

	record TestExecutionEnded(TestId testId) implements Event {
	}

	record CommandStarted(
			CommandId commandId,
			CommandId parentId,
			String name,
			Map<String, String> parameters
	) implements Event {
	}

	record CommandEnded(CommandId commandId) implements Event {
	}
}
