package io.github.arthurc.autotest.eventhandling;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventStreamRepositoryTest {
	private static final byte[] DATA = "{\"foo\":\"bar\"}".getBytes();
	private static final CloudEvent CLOUD_EVENT_1 = CloudEventBuilder.v1()
			.withId("1")
			.withSource(URI.create("urn:foo"))
			.withType("bar")
			.withData("application/json", DATA)
			.build();

	private EventStorageEngine eventStorageEngine;
	private EventStreamRepository eventStreamRepository;

	@BeforeEach
	void setUp() {
		eventStorageEngine = mock();

		eventStreamRepository = new EventStreamRepository(eventStorageEngine);
	}

	@Test
	void Should_create_an_empty_event_stream_by_default() {
		assertThat(eventStreamRepository.findById(UUID.randomUUID())).isNotNull();
	}

	@Test
	void Can_add_events_to_the_stream() {
		var id = UUID.randomUUID();
		var eventStream = eventStreamRepository.findById(id);
		var stream = Stream.of(CLOUD_EVENT_1);

		eventStream.write(stream);

		verify(eventStorageEngine).write(id, 0, stream);
	}

	@Test
	void Should_pass_the_current_version_when_writing_to_the_storage_engine() {
		var id = UUID.randomUUID();
		var stream = Stream.of(CLOUD_EVENT_1);
		when(eventStorageEngine.streamVersion(id)).thenReturn(5);

		EventStream eventStream = eventStreamRepository.findById(id);
		eventStream.write(stream);

		verify(eventStorageEngine).write(id, 5, stream);
	}

}
