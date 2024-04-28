/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventhandling;

import io.cloudevents.CloudEvent;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository for event streams.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class EventStreamRepository {

	private final EventStorageEngine eventStorageEngine;

	public EventStreamRepository(EventStorageEngine eventStorageEngine) {
		this.eventStorageEngine = eventStorageEngine;
	}

	/**
	 * Finds an event stream by its ID.
	 *
	 * @param id the ID of the event stream
	 * @return the event stream
	 */
	public EventStream findById(UUID id) {
		return new EventStream(this, id);
	}

	int streamVersion(UUID id) {
		return this.eventStorageEngine.streamVersion(id);
	}

	int write(UUID streamId, int expectedVersion, Stream<CloudEvent> events) {
		return this.eventStorageEngine.write(streamId, expectedVersion, events);
	}

	Stream<CloudEvent> streamEvents(UUID streamId) {
		return this.eventStorageEngine.streamEvents(streamId);
	}

}
