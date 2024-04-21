/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc.statement;

import io.cloudevents.CloudEvent;
import io.github.arthurc.autotest.eventing.jdbc.EventSchema;

import java.util.UUID;
import java.util.stream.Stream;

@FunctionalInterface
public interface SelectEventsStatement {

	String DATA_NAME = "data";

	static SelectEventsStatement compile(EventSchema schema) {
		String sql = schema.compile("""
				SELECT {data-column-name} AS data
				FROM {event-table-name}
				WHERE {stream-id-column-name} = ?
				ORDER BY {global-sequence-number-column-name}
				""");

		return (statementExecutor, id, mapper) -> statementExecutor.stream(
				sql,
				statement -> statement.setObject(1, id),
				mapper);
	}

	Stream<CloudEvent> apply(StatementExecutor statementExecutor, UUID id, StatementExecutor.ResultSetMapper<CloudEvent> mapper);
}
