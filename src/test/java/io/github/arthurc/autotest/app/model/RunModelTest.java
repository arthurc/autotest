package io.github.arthurc.autotest.app.model;

import io.github.arthurc.autotest.command.CommandId;
import io.github.arthurc.autotest.eventing.CommandFunction;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RunModelTest {

	@Nested
	class SetTestPlan {

		@Test
		void Returns_the_correct_events() {
			CommandFunction<Event> function = RunModel.setTestPlan(new TestPlanModel());

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new Event.TestPlanSet(new TestPlanModel()));
		}
	}

	@Nested
	class SetTestExecutionStarted {

		@Test
		void Returns_the_correct_events() {
			TestId testId = new TestId("test-id");
			CommandFunction<Event> function = RunModel.setTestExecutionStarted(testId);

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new Event.TestExecutionStarted(testId));
		}
	}

	@Nested
	class SetTestExecutionEnded {

		@Test
		void Returns_the_correct_events() {
			TestId testId = new TestId("test-id");
			CommandFunction<Event> function = RunModel.setTestExecutionEnded(testId);

			var stream = function.apply(Stream.of());

			assertThat(stream).containsExactly(new Event.TestExecutionEnded(testId));
		}
	}

	@Nested
	class SetCommandStarted {

		@Test
		void Returns_the_correct_events() {
			var commandId = new CommandId();
			var parentId = new CommandId();
			CommandFunction<Event> function = RunModel.setCommandStarted(commandId, parentId, "command", Map.of("a", "b"));

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new Event.CommandStarted(commandId, parentId, "command", Map.of("a", "b")));
		}
	}

	@Nested
	class SetCommandEnded {
		@Test
		void Returns_the_correct_events() {
			CommandId commandId = new CommandId();
			CommandFunction<Event> function = RunModel.setCommandEnded(commandId);

			var stream = function.apply(Stream.empty());

			assertThat(stream).containsExactly(new Event.CommandEnded(commandId));
		}
	}

}
