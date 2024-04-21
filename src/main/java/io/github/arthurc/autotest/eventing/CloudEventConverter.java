/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import io.cloudevents.CloudEvent;

/**
 * Converts between domain events and CloudEvents.
 *
 * @param <T> the type of domain events
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface CloudEventConverter<T> {

	/**
	 * Converts a domain event to a CloudEvent.
	 *
	 * @param event the domain event
	 * @return the CloudEvent
	 */
	CloudEvent toCloudEvent(T event);

	/**
	 * Converts a CloudEvent to a domain event.
	 *
	 * @param cloudEvent the CloudEvent
	 * @return the domain event
	 */
	T toDomainEvent(CloudEvent cloudEvent);

}
