/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * An application service that executes a command function on an event stream.
 * The event stream is loaded from a repository and persists the events produced by the command function.
 *
 * @param <T> the type of domain events
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ApplicationService<T> {
	private final EventStreamRepository eventStreamRepository;
	private final CloudEventConverter<T> cloudEventConverter;

	public ApplicationService(EventStreamRepository eventStreamRepository, CloudEventConverter<T> cloudEventConverter) {
		this.eventStreamRepository = eventStreamRepository;
		this.cloudEventConverter = cloudEventConverter;
	}

	/**
	 * Executes a command function on an event stream identified by its ID.
	 *
	 * @param id      the ID of the event stream
	 * @param command the command function
	 */
	@Transactional
	public void execute(UUID id, CommandFunction<T> command) {
		EventStream eventStream = this.eventStreamRepository.findById(id);

		Stream<T> newEvents;
		try (Stream<T> domainEvents = eventStream.events().map(this.cloudEventConverter::toDomainEvent)) {
			newEvents = command.apply(domainEvents);
		}

		eventStream.write(newEvents.map(this.cloudEventConverter::toCloudEvent));
	}
}
