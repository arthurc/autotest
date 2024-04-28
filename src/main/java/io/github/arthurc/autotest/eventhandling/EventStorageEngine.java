/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventhandling;

import io.cloudevents.CloudEvent;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Interface for event storage engines which holds cloud events.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface EventStorageEngine {
	/**
	 * Get the version of the stream.
	 *
	 * @param streamId the ID of the stream
	 * @return the version of the stream
	 */
	int streamVersion(UUID streamId);

	/**
	 * Write a stream of events to an event stream identified by its ID.
	 *
	 * @param streamId       the ID of the stream
	 * @param currentVersion the current version of the stream which is used for optimistic locking
	 * @param events         the events to write
	 * @return the new version of the stream
	 */
	int write(UUID streamId, int currentVersion, Stream<CloudEvent> events);

	/**
	 * Stream the events in an event stream identified by its ID. The events are ordered by their occurrence
	 * and the stream is empty if the stream does not exist.
	 *
	 * @param streamId the ID of the stream
	 * @return a stream of events
	 */
	Stream<CloudEvent> streamEvents(UUID streamId);
}
