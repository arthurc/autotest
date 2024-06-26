package io.github.arthurc.autotest.command;

import io.github.arthurc.autotest.test.utils.EventCollector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandTest {

	@Nested
	class Creating_a_command_execution_lifecycle {
		@Test
		void Requires_a_name() {
			var builder = Command.builder();

			assertThatThrownBy(builder::build)
					.isInstanceOf(NullPointerException.class)
					.hasMessage("name has to be set");
		}

		@Test
		void Has_a_name() {
			var lifecycle = Command.builder().name("arne").build();

			assertThat(lifecycle.getName()).isEqualTo("arne");
		}

		@Test
		void Has_no_parameters_by_default() {
			var lifecycle = Command.builder().name("arne").build();

			assertThat(lifecycle.getParameters()).isEmpty();
		}

		@Test
		void Has_no_subject_by_default() {
			var lifecycle = Command.builder().name("arne").build();

			assertThat(lifecycle.getSubject()).isEmpty();
		}
	}

	@Nested
	class Changing_parameters {
		@Test
		void Adds_a_parameter_to_the_map_of_parameters() {
			var lifecycle = Command.builder().name("arne").build();

			lifecycle.addParameters(Map.of("foo", "bar", "bar", "baz"));

			assertThat(lifecycle.getParameters())
					.containsExactlyEntriesOf(Map.of("foo", "bar", "bar", "baz"));
		}

		@Test
		void Publishes_a_ParametersModified_event() {
			var eventCollector = new EventCollector();
			var lifecycle = Command.builder().name("arne").build();

			eventCollector.run(() ->
					lifecycle.run(() ->
							lifecycle.addParameters(Map.of("foo", "bar"))));

			assertThat(eventCollector.getEvents()).contains(new CommandEvent.ParametersModified(lifecycle));
		}

		@Test
		void Does_not_publish_a_ParametersModified_event_when_parameters_are_not_changed() {
			var eventCollector = new EventCollector();
			var lifecycle = Command.builder().name("arne").parameter("foo", "bar").build();

			eventCollector.run(() ->
					lifecycle.run(() -> lifecycle.addParameters(Map.of())));

			assertThat(eventCollector.getEvents()).doesNotHaveAnyElementsOfTypes(CommandEvent.class);
		}
	}

	@Nested
	class Changing_subject {
		@Test
		void Publishes_a_SubjectChanged_event() {
			var eventCollector = new EventCollector();
			var lifecycle = Command.builder().name("arne").build();

			eventCollector.run(() -> lifecycle.run(() -> lifecycle.setSubject("value")));

			assertThat(eventCollector.getEvents()).contains(new CommandEvent.SubjectChanged(lifecycle));
		}

		@Test
		void Does_not_publish_a_SubjectChanged_event_when_subject_is_not_changed() {
			var eventCollector = new EventCollector();
			var lifecycle = Command.builder().name("arne").subject("value").build();

			eventCollector.run(() -> lifecycle.run(() -> lifecycle.setSubject("value")));

			assertThat(eventCollector.getEvents()).doesNotHaveAnyElementsOfTypes(CommandEvent.class);
		}
	}

}