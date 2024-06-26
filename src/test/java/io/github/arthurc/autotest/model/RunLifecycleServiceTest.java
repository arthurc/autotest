package io.github.arthurc.autotest.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.application.service.blocking.generic.GenericApplicationService;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.inmemory.InMemoryEventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig
class RunLifecycleServiceTest {

	private final RecursiveComparisonConfiguration recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
			.withIgnoredFields("commandId")
			.build();

	private final Run run = new Run();

	@Autowired
	private EventStore eventStore;

	@Autowired
	private CloudEventConverter<Event> cloudEventConverter;

	@Test
	void Should_publish_a_TestPlanSet_event_when_a_test_plan_is_set() {
		run.run(() ->
				new TestPlanLifecycle(new TestPlanModel()).run(mock()));

		assertThat(getEvents()).containsExactly(
				new RunEvent.RunStarted(run.getId()),
				new RunEvent.TestPlanSet(run.getId(), new TestPlanModel()),
				new RunEvent.RunEnded(run.getId()));
	}

	@Test
	void Should_publish_a_TestExecutionStarted_and_Ended_event_when_a_test_execution_is_started() {
		run.run(() ->
				new TestPlanLifecycle(new TestPlanModel()).run(() ->
						new TestExecutionLifecycle(new TestId("foo")).run(mock())));

		assertThat(getEvents()).containsExactly(
				new RunEvent.RunStarted(run.getId()),
				new RunEvent.TestPlanSet(run.getId(), new TestPlanModel()),
				new RunEvent.TestExecutionStarted(run.getId(), new TestId("foo")),
				new RunEvent.TestExecutionEnded(run.getId(), new TestId("foo")),
				new RunEvent.RunEnded(run.getId()));
	}

	@Test
	void Should_publish_a_CommandStarted_and_Ended_event_when_a_command_is_started_and_ended() {
		run.run(() ->
				new TestPlanLifecycle(new TestPlanModel()).run(() ->
						new TestExecutionLifecycle(new TestId("foo")).run(() ->
								Command.run("foo-command", Map.of("a", "b"), mock()))));

		assertThat(getEvents())
				.usingRecursiveFieldByFieldElementComparator(recursiveComparisonConfiguration)
				.containsExactly(
						new RunEvent.RunStarted(run.getId()),
						new RunEvent.TestPlanSet(run.getId(), new TestPlanModel()),
						new RunEvent.TestExecutionStarted(run.getId(), new TestId("foo")),
						new RunEvent.CommandStarted(run.getId(), new TestId("foo"), null, null, null, "foo-command", Map.of("a", "b"), null),
						new RunEvent.CommandEnded(run.getId(), null, null),
						new RunEvent.TestExecutionEnded(run.getId(), new TestId("foo")),
						new RunEvent.RunEnded(run.getId()));
	}

	private Stream<Event> getEvents() {
		return eventStore.read(run.getId().id().toString()).events().map(cloudEventConverter::toDomainEvent);
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		RunLifecycleService runLifecycleService(ApplicationContext applicationContext) {
			return new RunLifecycleService(new GenericApplicationService<>(eventStore(), cloudEventConverter()), applicationContext);
		}

		@Bean
		InMemoryEventStore eventStore() {
			return new InMemoryEventStore();
		}

		@Bean
		ApplicationContextLifecycle.Registrar registrar(ApplicationContext applicationContext) {
			return new ApplicationContextLifecycle.Registrar(applicationContext);
		}

		@Bean
		CloudEventConverter<Event> cloudEventConverter() {
			return new JacksonCloudEventConverter<>(new ObjectMapper(), URI.create("urn:test"));
		}
	}
}
