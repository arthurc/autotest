/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import io.cloudevents.CloudEvent;

import java.util.stream.Stream;

/**
 * Interface for event streams.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface EventStream {

	/**
	 * Streams the events in the event stream.
	 *
	 * @return a stream of events
	 */
	Stream<CloudEvent> events();

	/**
	 * Writes a stream of events to the end of the event stream.
	 *
	 * @param events the events to write
	 */
	void write(Stream<CloudEvent> events);
}
