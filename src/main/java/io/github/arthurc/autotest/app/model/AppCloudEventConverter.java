/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.github.arthurc.autotest.eventing.CloudEventConverter;
import io.github.arthurc.autotest.eventing.CloudEventTypeMapper;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Converts between domain events and CloudEvents. The domain events are serialized as JSON
 * using Jackson and the source is set to "urn:autotest".
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class AppCloudEventConverter implements CloudEventConverter<Event> {
	private static final URI SOURCE = URI.create("urn:autotest");

	private final CloudEventTypeMapper<Event> cloudEventTypeMapper;
	private final ObjectMapper objectMapper;

	public AppCloudEventConverter(CloudEventTypeMapper<Event> cloudEventTypeMapper, ObjectMapper objectMapper) {
		this.cloudEventTypeMapper = cloudEventTypeMapper;
		this.objectMapper = objectMapper;
	}

	@Override
	public CloudEvent toCloudEvent(Event event) {
		byte[] data;
		try {
			data = this.objectMapper.writeValueAsBytes(event);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

		return CloudEventBuilder.v1()
				.withId(UUID.randomUUID().toString())
				.withSource(SOURCE)
				.withType(this.cloudEventTypeMapper.toCloudEventType(event))
				.withData("application/json", data)
				.withTime(OffsetDateTime.now(ZoneOffset.UTC))
				.build();
	}

	@Override
	public Event toDomainEvent(CloudEvent cloudEvent) {
		if (cloudEvent.getData() == null) {
			return null;
		} else if (!SOURCE.equals(cloudEvent.getSource())) {
			throw new IllegalArgumentException("Unexpected source: " + cloudEvent.getSource());
		}

		var bytes = cloudEvent.getData().toBytes();
		var type = this.cloudEventTypeMapper.toJavaType(cloudEvent);

		try {
			return this.objectMapper.readValue(bytes, type);
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to parse the data as JSON", e);
		}
	}
}
