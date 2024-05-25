package io.github.arthurc.autotest.app.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.eventhandling.CommandFunction;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import io.github.arthurc.autotest.teststage.TestStage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RunModelTest {
	private final RunId runId = new RunId();

	@Nested
	class SetTestPlan {

		@Test
		void Returns_the_correct_events() {
			CommandFunction<Event> function = RunModel.setTestPlan(runId, new TestPlanModel());

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.TestPlanSet(runId, new TestPlanModel()));
		}
	}

	@Nested
	class SetTestExecutionStarted {

		@Test
		void Returns_the_correct_events() {
			TestId testId = new TestId("test-id");
			CommandFunction<Event> function = RunModel.setTestExecutionStarted(runId, testId);

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.TestExecutionStarted(runId, testId));
		}
	}

	@Nested
	class SetTestExecutionEnded {

		@Test
		void Returns_the_correct_events() {
			TestId testId = new TestId("test-id");
			CommandFunction<Event> function = RunModel.setTestExecutionEnded(runId, testId);

			var stream = function.apply(Stream.of());

			assertThat(stream).containsExactly(new RunEvent.TestExecutionEnded(runId, testId));
		}
	}

	@Nested
	class SetCommandStarted {

		@Test
		void Returns_the_correct_events() {
			var commandId = new CommandId();
			var parentId = new CommandId();
			CommandFunction<Event> function = RunModel.setCommandStarted(runId, new TestId("testid"), TestStage.TEST_BODY, commandId, parentId, "command", Map.of("a", "b"), "subject");

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.CommandStarted(runId, new TestId("testid"), TestStage.TEST_BODY, commandId, parentId, "command", Map.of("a", "b"), "subject"));
		}
	}

	@Nested
	class SetCommandEnded {
		@Test
		void Returns_the_correct_events() {
			CommandId commandId = new CommandId();
			CommandFunction<Event> function = RunModel.setCommandEnded(runId, commandId, "subject");

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.CommandEnded(runId, commandId, "subject"));
		}
	}

}
