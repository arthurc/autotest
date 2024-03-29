package io.github.arthurc.autotest.commandexecution;

import io.github.arthurc.autotest.test.utils.EventCollector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommandExecutionLifecycleTest {

	@Nested
	class Changing_parameters {
		@Test
		void Adds_a_parameter_to_the_list_of_parameters() {
			CommandExecutionLifecycle lifecycle = new CommandExecutionLifecycle("arne");

			lifecycle.addParameters(new Parameter("key", "value"), new Parameter("value"));

			assertThat(lifecycle.getParameters()).containsExactly(
					new Parameter("key", "value"),
					new Parameter("value"));
		}

		@Test
		void Publishes_a_ParametersModified_event() {
			EventCollector eventCollector = new EventCollector();
			CommandExecutionLifecycle lifecycle = new CommandExecutionLifecycle("arne");

			eventCollector.run(() ->
					lifecycle.run(() ->
							lifecycle.addParameters(new Parameter("value"))));

			assertThat(eventCollector.getEvents()).contains(new CommandExecutionLifecycleEvent.ParametersModified(lifecycle));
		}

		@Test
		void Does_not_publish_a_ParametersModified_event_when_parameters_are_not_changed() {
			EventCollector eventCollector = new EventCollector();
			CommandExecutionLifecycle lifecycle = new CommandExecutionLifecycle("arne");
			lifecycle.addParameters(new Parameter("value"));

			eventCollector.run(() ->
					lifecycle.run(lifecycle::addParameters));

			assertThat(eventCollector.getEvents()).doesNotHaveAnyElementsOfTypes(CommandExecutionLifecycleEvent.class);
		}

	}

}