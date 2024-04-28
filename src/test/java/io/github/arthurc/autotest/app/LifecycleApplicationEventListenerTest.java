package io.github.arthurc.autotest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.github.arthurc.autotest.app.model.AppCloudEventConverter;
import io.github.arthurc.autotest.app.model.Event;
import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.eventhandling.ApplicationService;
import io.github.arthurc.autotest.eventhandling.EventStream;
import io.github.arthurc.autotest.eventhandling.EventStreamRepository;
import io.github.arthurc.autotest.eventhandling.ReflectiveCloudEventTypeMapper;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({
		ApplicationContextLifecycle.Registrar.class,
		ApplicationService.class,
		LifecycleApplicationEventListener.class,
		AppCloudEventConverter.class,
		LifecycleApplicationEventListenerTest.TestConfig.class
})
class LifecycleApplicationEventListenerTest {

	private final RecursiveComparisonConfiguration recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
			.withIgnoredFields("commandId")
			.build();

	@MockBean
	private EventStreamRepository eventStreamRepository;

	private EventStream eventStream;

	@BeforeEach
	void setUp() {
		eventStream = mock();

		when(eventStreamRepository.findById(any())).thenReturn(eventStream);
	}

	@Test
	void Should_publish_a_TestPlanSet_event_when_a_test_plan_is_set() {
		new TestPlanLifecycle(new TestPlanModel()).run(mock());

		assertThat(getEvents()).containsExactly(
				List.of(new Event.TestPlanSet(new TestPlanModel())));
	}

	@Test
	void Should_publish_a_TestExecutionStarted_and_Ended_event_when_a_test_execution_is_started() {
		new TestPlanLifecycle(new TestPlanModel()).run(() ->
				new TestExecutionLifecycle(new TestId("foo")).run(mock()));

		assertThat(getEvents()).containsExactly(
				List.of(new Event.TestPlanSet(new TestPlanModel())),
				List.of(new Event.TestExecutionStarted(new TestId("foo"))),
				List.of(new Event.TestExecutionEnded(new TestId("foo"))));
	}

	@Test
	void Should_publish_a_CommandStarted_and_Ended_event_when_a_command_is_started_and_ended() {
		new TestPlanLifecycle(new TestPlanModel()).run(() ->
				new TestExecutionLifecycle(new TestId("foo")).run(() ->
						Command.run("foo-command", Map.of("a", "b"), mock())));

		assertThat(getEvents())
				.usingRecursiveFieldByFieldElementComparator(recursiveComparisonConfiguration)
				.containsExactly(
						List.of(new Event.TestPlanSet(new TestPlanModel())),
						List.of(new Event.TestExecutionStarted(new TestId("foo"))),
						List.of(new Event.CommandStarted(null, null, "foo-command", Map.of("a", "b"))),
						List.of(new Event.CommandEnded(null)),
						List.of(new Event.TestExecutionEnded(new TestId("foo"))));
	}

	private Stream<List<Event>> getEvents() {
		AppCloudEventConverter converter = new AppCloudEventConverter(new ReflectiveCloudEventTypeMapper<>(Event.class.getClassLoader()), new ObjectMapper());

		ArgumentCaptor<Stream<CloudEvent>> eventCaptor = ArgumentCaptor.captor();
		verify(eventStream, atLeastOnce()).write(eventCaptor.capture());
		return eventCaptor.getAllValues().stream().map(s -> s.map(converter::toDomainEvent).toList());
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		ReflectiveCloudEventTypeMapper<Event> reflectiveCloudEventTypeMapper() {
			return new ReflectiveCloudEventTypeMapper<>(Event.class.getClassLoader());
		}

		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}
}
