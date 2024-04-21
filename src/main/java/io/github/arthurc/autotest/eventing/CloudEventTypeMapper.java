/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import io.cloudevents.CloudEvent;

/**
 * Maps types between the cloud event's type and the domain event's Java type.
 *
 * @param <T> the type of domain events.
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface CloudEventTypeMapper<T> {

	/**
	 * Maps a domain event to a cloud event type.
	 *
	 * @param domainEvent the domain event.
	 * @return the cloud event type.
	 */
	String toCloudEventType(T domainEvent);

	/**
	 * Maps a cloud event to the domain event's Java type.
	 *
	 * @param cloudEvent the cloud event.
	 * @return the Java type.
	 */
	Class<? extends T> toJavaType(CloudEvent cloudEvent);

}
