/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc.statement;

import io.github.arthurc.autotest.eventing.jdbc.EventSchema;

@FunctionalInterface
public interface CreateCloudEventTableStatement {
	static CreateCloudEventTableStatement compile(EventSchema schema) {
		String sql = schema.compile("""
				CREATE TABLE IF NOT EXISTS {event-table-name} (
					{event-id-column-name} VARCHAR(255) NOT NULL,
					{stream-id-column-name} VARCHAR(255) NOT NULL,
					{global-sequence-number-column-name} {global-sequence-number-type},
					{sequence-number-column-name} BIGINT NOT NULL,
					{data-column-name} {data-type} NOT NULL,
					PRIMARY KEY ({event-id-column-name}),
					UNIQUE ({stream-id-column-name}, {sequence-number-column-name}),
					UNIQUE ({global-sequence-number-column-name})
				)
				""");

		return statementExecutor -> statementExecutor.update(sql);
	}

	void apply(StatementExecutor statementExecutor);
}
