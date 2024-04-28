package io.github.arthurc.autotest.app.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.github.arthurc.autotest.eventhandling.ReflectiveCloudEventTypeMapper;
import io.github.arthurc.autotest.testplan.TestId;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.InstanceOfAssertFactories.BYTE_ARRAY;

class AppCloudEventConverterTest {

	private static final byte[] DATA = "{\"testId\":\"test-id\"}".getBytes();
	private static final CloudEvent CLOUD_EVENT = CloudEventBuilder.v1()
			.withId("id")
			.withSource(URI.create("urn:autotest"))
			.withType("type")
			.withTime(OffsetDateTime.now())
			.withData((CloudEventData) null)
			.build();

	private final AppCloudEventConverter appCloudEventConverter = new AppCloudEventConverter(
			new ReflectiveCloudEventTypeMapper<>(getClass().getClassLoader()),
			new ObjectMapper());

	@Test
	void Can_convert_to_cloud_event() {
		var cloudEvent = appCloudEventConverter.toCloudEvent(new Event.TestExecutionStarted(new TestId("test-id")));

		assertThat(cloudEvent.getId()).isNotNull();
		assertThat(cloudEvent.getSource()).asString().isEqualTo("urn:autotest");
		assertThat(cloudEvent.getType()).asString().isEqualTo("io.github.arthurc.autotest.app.model.Event$TestExecutionStarted");
		assertThat(cloudEvent.getDataContentType()).asString().isEqualTo("application/json");
		assertThat(cloudEvent.getTime()).isCloseToUtcNow(within(5, ChronoUnit.SECONDS)).isNotNull();
		assertThat(cloudEvent.getData())
				.extracting(CloudEventData::toBytes)
				.asInstanceOf(BYTE_ARRAY)
				.asString()
				.isEqualTo("{\"testId\":\"test-id\"}");
	}

	@Test
	void Can_convert_to_domain_event() {
		var cloudEvent = CloudEventBuilder.from(CLOUD_EVENT)
				.withType("io.github.arthurc.autotest.app.model.Event$TestExecutionStarted")
				.withData(DATA)
				.build();

		var event = appCloudEventConverter.toDomainEvent(cloudEvent);

		assertThat(event).isEqualTo(new Event.TestExecutionStarted(new TestId("test-id")));
	}

	@Test
	void Returns_null_when_the_data_is_null() {
		var cloudEvent = CloudEventBuilder.from(CLOUD_EVENT)
				.withData((CloudEventData) null)
				.build();

		assertThat(appCloudEventConverter.toDomainEvent(cloudEvent)).isNull();
	}

	@Test
	void Throws_exception_when_the_data_is_not_valid_json() {
		var cloudEvent = CloudEventBuilder.from(CLOUD_EVENT)
				.withType("io.github.arthurc.autotest.app.model.Event$TestExecutionStarted")
				.withData("application/json", "not a json".getBytes())
				.build();

		assertThatThrownBy(() -> appCloudEventConverter.toDomainEvent(cloudEvent))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Failed to parse the data as JSON");
	}

	@Test
	void Throws_exception_when_the_source_is_not_autotest() {
		var cloudEvent = CloudEventBuilder.from(CLOUD_EVENT)
				.withSource(URI.create("urn:other"))
				.withData(DATA)
				.build();

		assertThatThrownBy(() -> appCloudEventConverter.toDomainEvent(cloudEvent))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unexpected source: urn:other");
	}
}
