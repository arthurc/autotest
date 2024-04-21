/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc.statement;

import io.github.arthurc.autotest.eventing.jdbc.EventSchema;

import java.util.UUID;

@FunctionalInterface
public interface GetStreamVersionStatement {

	static GetStreamVersionStatement compile(EventSchema schema) {
		String sql = schema.compile("""
				SELECT COUNT(1) FROM {event-table-name}
				WHERE {stream-id-column-name} = ?
				GROUP BY {stream-id-column-name}
				""");

		return (statementExecutor, id) -> statementExecutor.query(
				sql,
				ps -> ps.setObject(1, id),
				rs -> rs.getInt(1),
				0);
	}

	int apply(StatementExecutor statementExecutor, UUID id);
}
