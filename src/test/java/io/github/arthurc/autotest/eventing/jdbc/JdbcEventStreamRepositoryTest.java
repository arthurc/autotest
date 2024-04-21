package io.github.arthurc.autotest.eventing.jdbc;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.github.arthurc.autotest.eventing.EventStream;
import io.github.arthurc.autotest.spring.jdbc.SpringJdbcStatementExecutor;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.BYTE_ARRAY;

@JdbcTest
class JdbcEventStreamRepositoryTest {

	private static final RecursiveComparisonConfiguration CLOUD_EVENT_COMPARISON = RecursiveComparisonConfiguration.builder()
			.withIgnoredFields("data")
			.build();
	private static final byte[] DATA = "{\"foo\":\"bar\"}".getBytes();
	private static final CloudEvent CLOUD_EVENT_1 = CloudEventBuilder.v1()
			.withId("1")
			.withSource(URI.create("urn:foo"))
			.withType("bar")
			.withData("application/json", DATA)
			.build();
	private static final CloudEvent CLOUD_EVENT_2 = CloudEventBuilder.from(CLOUD_EVENT_1)
			.withId("2")
			.build();

	@Autowired
	private JdbcEventStreamRepository eventStreamRepository;

	@BeforeEach
	void setUp() {
		eventStreamRepository.createSchema();
	}

	@Test
	void Should_get_an_empty_event_stream_by_default() {
		EventStream eventStream = eventStreamRepository.findById(UUID.randomUUID());

		assertThat(eventStream.events()).isEmpty();
	}

	@Test
	void Can_add_events_to_the_stream() {
		EventStream eventStream = eventStreamRepository.findById(UUID.randomUUID());

		eventStream.write(Stream.of(CLOUD_EVENT_1));

		assertThat(eventStream.events())
				.singleElement()
				.satisfies(e -> assertThat(e.getData())
						.extracting(CloudEventData::toBytes, BYTE_ARRAY)
						.isEqualTo(DATA))
				.usingRecursiveComparison(CLOUD_EVENT_COMPARISON)
				.isEqualTo(CLOUD_EVENT_1);
	}

	@Test
	void Can_add_multiple_events_to_the_stream() {
		EventStream eventStream = eventStreamRepository.findById(UUID.randomUUID());

		eventStream.write(Stream.of(CLOUD_EVENT_1, CLOUD_EVENT_2));

		assertThat(eventStream.events())
				.usingRecursiveFieldByFieldElementComparator(CLOUD_EVENT_COMPARISON)
				.containsExactly(CLOUD_EVENT_1, CLOUD_EVENT_2);
	}

	@Test
	void Fails_to_update_the_same_stream_simultaneously() {
		var streamId = UUID.randomUUID();
		var eventStream1 = eventStreamRepository.findById(streamId);
		var eventStream2 = eventStreamRepository.findById(streamId);

		assertThatNoException().isThrownBy(() -> eventStream1.write(Stream.of(CLOUD_EVENT_1)));
		assertThatThrownBy(() -> eventStream2.write(Stream.of(CLOUD_EVENT_2)))
				.hasMessageContaining("Unique index or primary key violation");
	}

	@Test
	void Can_read_events_from_the_stream() {
		var streamId = UUID.randomUUID();
		var eventStream = eventStreamRepository.findById(streamId);

		eventStream.write(Stream.of(CLOUD_EVENT_1, CLOUD_EVENT_2));

		assertThat(eventStreamRepository.findById(streamId).events())
				.usingRecursiveFieldByFieldElementComparator(CLOUD_EVENT_COMPARISON)
				.containsExactly(CLOUD_EVENT_1, CLOUD_EVENT_2);
	}

	@Configuration
	static class TestConfig {
		@Bean
		JdbcEventStreamRepository eventStreamRepository(JdbcTemplate jdbcTemplate) {
			return JdbcEventStreamRepository.builder()
					.statementExecutor(new SpringJdbcStatementExecutor(jdbcTemplate))
					.build();
		}
	}
}
