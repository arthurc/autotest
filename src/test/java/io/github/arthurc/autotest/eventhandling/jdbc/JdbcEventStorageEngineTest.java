package io.github.arthurc.autotest.eventhandling.jdbc;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.github.arthurc.autotest.eventhandling.EventStream;
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
class JdbcEventStorageEngineTest {

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
	private JdbcEventStorageEngine eventStorageEngine;

	@BeforeEach
	void setUp() {
		eventStorageEngine.createSchema();
	}

	@Test
	void Should_get_an_empty_event_stream_by_default() {
		assertThat(eventStorageEngine.streamEvents(UUID.randomUUID())).isEmpty();
	}

	@Test
	void Can_add_events_to_the_stream() {
		UUID id = UUID.randomUUID();

		eventStorageEngine.write(id, 0, Stream.of(CLOUD_EVENT_1));

		assertThat(eventStorageEngine.streamEvents(id))
				.singleElement()
				.satisfies(e -> assertThat(e.getData())
						.extracting(CloudEventData::toBytes, BYTE_ARRAY)
						.isEqualTo(DATA))
				.usingRecursiveComparison(CLOUD_EVENT_COMPARISON)
				.isEqualTo(CLOUD_EVENT_1);
	}

	@Test
	void Can_add_multiple_events_to_the_stream() {
		UUID id = UUID.randomUUID();

		eventStorageEngine.write(id, 0, Stream.of(CLOUD_EVENT_1, CLOUD_EVENT_2));

		assertThat(eventStorageEngine.streamEvents(id))
				.usingRecursiveFieldByFieldElementComparator(CLOUD_EVENT_COMPARISON)
				.containsExactly(CLOUD_EVENT_1, CLOUD_EVENT_2);
	}

	@Test
	void Fails_to_update_the_same_stream_simultaneously() {
		UUID id = UUID.randomUUID();

		assertThatNoException().isThrownBy(() -> eventStorageEngine.write(id, 0, Stream.of(CLOUD_EVENT_1)));
		assertThatThrownBy(() -> eventStorageEngine.write(id, 0, Stream.of(CLOUD_EVENT_2)))
				.hasMessageContaining("Unique index or primary key violation");
	}

	@Configuration
	static class TestConfig {
		@Bean
		JdbcEventStorageEngine eventStreamRepository(JdbcTemplate jdbcTemplate) {
			return JdbcEventStorageEngine.builder()
					.statementExecutor(new SpringJdbcStatementExecutor(jdbcTemplate))
					.build();
		}
	}
}
