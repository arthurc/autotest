/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import io.cloudevents.CloudEvent;

/**
 * Maps between domain events and CloudEvents using reflection.
 * The domain event full class name is used as the CloudEvent type, i.e. using {@link Class#getName()}.
 *
 * @param <T> the type of domain events
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ReflectiveCloudEventTypeMapper<T> implements CloudEventTypeMapper<T> {

	private final ClassLoader classLoader;

	public ReflectiveCloudEventTypeMapper(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public String toCloudEventType(T domainEvent) {
		return domainEvent.getClass().getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> toJavaType(CloudEvent cloudEvent) {
		String type = cloudEvent.getType();
		try {
			return (Class<? extends T>) this.classLoader.loadClass(type);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Failed to load class: " + type, e);
		}
	}
}
