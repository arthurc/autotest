/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc;

import io.cloudevents.CloudEvent;
import io.github.arthurc.autotest.eventing.EventStream;

import java.util.UUID;
import java.util.stream.Stream;

class JdbcEventStream implements EventStream {
	private final JdbcEventStreamRepository repository;
	private final UUID id;
	private int version;

	JdbcEventStream(JdbcEventStreamRepository repository, UUID id) {
		this.repository = repository;
		this.id = id;
		this.version = repository.getStreamVersion(id);
	}

	@Override
	public Stream<CloudEvent> events() {
		return this.repository.streamEvents(this.id);
	}

	@Override
	public void write(Stream<CloudEvent> events) {
		this.version += this.repository.write(this.id, this.version, events);
	}
}
