/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc.statement;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.github.arthurc.autotest.eventing.jdbc.EventSchema;

import java.util.UUID;
import java.util.stream.Stream;

public interface InsertEventStatement {
	static InsertEventStatement compile(EventSchema schema) {
		String sql = schema.compile("""
				INSERT INTO {event-table-name} ({event-id-column-name}, {stream-id-column-name}, {sequence-number-column-name}, {data-column-name})
				VALUES (?, ?, ?, ?)
				""");

		return (statementExecutor, streamId, eventFormat, expectedVersion, events) ->
				statementExecutor.batchUpdate(sql, events, (event, index) -> new Object[]{
						event.getId(),
						streamId,
						expectedVersion + index + 1,
						eventFormat.serialize(event)
				});
	}

	int[] apply(StatementExecutor statementExecutor, UUID streamId, EventFormat eventFormat, int expectedVersion, Stream<CloudEvent> events);
}
