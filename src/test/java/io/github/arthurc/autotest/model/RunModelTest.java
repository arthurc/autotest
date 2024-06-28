package io.github.arthurc.autotest.model;

import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.lifecycle.LifecycleResult;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import io.github.arthurc.autotest.teststage.TestStage;
import io.github.arthurc.autotest.teststage.TestStageLifecycle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RunModelTest {
	private final RunId runId = new RunId();
	private final TestId testId = new TestId("test-id");

	@Nested
	class SetTestPlan {

		@Test
		void Returns_the_correct_events() {
			var function = RunModel.on(runId, new LifecycleEvent.AfterBegin(new TestPlanLifecycle(new TestPlanModel())));

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.TestPlanSet(runId, new TestPlanModel()));
		}
	}

	@Nested
	class SetTestExecutionStarted {

		@Test
		void Returns_the_correct_events() {
			var function = RunModel.on(runId, new LifecycleEvent.AfterBegin(new TestExecutionLifecycle(testId)));

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new RunEvent.TestExecutionStarted(runId, testId));
		}
	}

	@Nested
	class SetTestExecutionEnded {

		@Test
		void Returns_the_correct_events() {
			var function = RunModel.on(runId, new LifecycleEvent.BeforeEnd(new TestExecutionLifecycle(testId), LifecycleResult.VOID));

			var stream = function.apply(Stream.of());

			assertThat(stream).containsExactly(new RunEvent.TestExecutionEnded(runId, testId));
		}
	}

	@Nested
	class SetCommandStarted {

		@Test
		void Returns_the_correct_events() {
			var command = Command.builder()
					.name("command")
					.parameters(Map.of("a", "b"))
					.subject("subject")
					.build();

			var stream = new TestExecutionLifecycle(testId).call(() ->
					new TestStageLifecycle(TestStage.TEST_BODY).call(() ->
							command.call(() ->
									RunModel.on(runId, new LifecycleEvent.AfterBegin(command)).apply(Stream.empty()))));

			assertThat(stream)
					.usingRecursiveFieldByFieldElementComparatorIgnoringFields("run", "test", "commandId")
					.containsExactly(new RunEvent.CommandStarted(runId,
							testId,
							TestStage.TEST_BODY,
							null,
							null,
							"command",
							Map.of("a", "b"),
							Integer.toString("subject".hashCode())));
		}
	}

	@Nested
	class SetCommandEnded {
		@Test
		void Returns_the_correct_events() {
			var command = Command.builder()
					.name("command")
					.parameters(Map.of("a", "b"))
					.subject("subject")
					.build();

			var stream = new TestExecutionLifecycle(testId).call(() ->
					new TestStageLifecycle(TestStage.TEST_BODY).call(() ->
							command.call(() ->
									RunModel.on(runId, new LifecycleEvent.BeforeEnd(command, LifecycleResult.VOID)).apply(Stream.empty()))));

			assertThat(stream)
					.usingRecursiveFieldByFieldElementComparatorIgnoringFields("run", "commandId")
					.containsExactly(new RunEvent.CommandEnded(null, null, Integer.toString("subject".hashCode())));
		}
	}
}
