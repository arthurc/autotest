/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventhandling;

import io.cloudevents.CloudEvent;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Interface for event streams.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class EventStream {
	private final EventStreamRepository repository;
	private final UUID id;
	private int version;

	EventStream(EventStreamRepository repository, UUID id) {
		this.repository = repository;
		this.id = id;
		this.version = repository.streamVersion(id);
	}

	/**
	 * Streams the events in the event stream.
	 *
	 * @return a stream of events
	 */
	public Stream<CloudEvent> events() {
		return this.repository.streamEvents(this.id);
	}

	/**
	 * Writes a stream of events to the end of the event stream.
	 *
	 * @param events the events to write
	 */
	public void write(Stream<CloudEvent> events) {
		this.version += this.repository.write(this.id, this.version, events);
	}
}
