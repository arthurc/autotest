/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.github.arthurc.autotest.eventing.EventStream;
import io.github.arthurc.autotest.eventing.EventStreamRepository;
import io.github.arthurc.autotest.eventing.jdbc.statement.CreateCloudEventTableStatement;
import io.github.arthurc.autotest.eventing.jdbc.statement.GetStreamVersionStatement;
import io.github.arthurc.autotest.eventing.jdbc.statement.InsertEventStatement;
import io.github.arthurc.autotest.eventing.jdbc.statement.SelectEventsStatement;
import io.github.arthurc.autotest.eventing.jdbc.statement.StatementExecutor;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * JDBC-based implementation of {@link EventStreamRepository}.
 * <p>
 * This implementation uses a {@link StatementExecutor} to execute SQL statements against a database.
 *
 * @author Arthur Hartwig Carlsson
 * @see EventStreamRepository
 * @since 1.0.0
 */
public class JdbcEventStreamRepository implements EventStreamRepository {
	private static final EventFormat EVENT_FORMAT = Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(ContentType.JSON));

	private final StatementExecutor statementExecutor;
	private final CreateCloudEventTableStatement createCloudEventTableStatement;
	private final GetStreamVersionStatement getStreamVersionStatement;
	private final InsertEventStatement insertEventStatement;
	private final SelectEventsStatement selectEventsStatement;

	private JdbcEventStreamRepository(Builder builder) {
		this.statementExecutor = builder.statementExecutor;
		this.createCloudEventTableStatement = Objects.requireNonNullElseGet(builder.createCloudEventTableStatement, () -> CreateCloudEventTableStatement.compile(builder.schema));
		this.getStreamVersionStatement = Objects.requireNonNullElseGet(builder.getStreamVersionStatement, () -> GetStreamVersionStatement.compile(builder.schema));
		this.insertEventStatement = Objects.requireNonNullElseGet(builder.insertEventStatement, () -> InsertEventStatement.compile(builder.schema));
		this.selectEventsStatement = Objects.requireNonNullElseGet(builder.selectEventsStatement, () -> SelectEventsStatement.compile(builder.schema));
	}

	/**
	 * Creates a new {@link Builder} for constructing a {@link JdbcEventStreamRepository}.
	 *
	 * @return the builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates the schema for the event stream repository using the {@link CreateCloudEventTableStatement}.
	 * This method is idempotent and can be called multiple times without side effects.
	 */
	public void createSchema() {
		this.createCloudEventTableStatement.apply(this.statementExecutor);
	}

	@Override
	public EventStream findById(UUID id) {
		return new JdbcEventStream(this, id);
	}

	int getStreamVersion(UUID id) {
		return this.getStreamVersionStatement.apply(this.statementExecutor, id);
	}

	int write(UUID streamId, int expectedVersion, Stream<CloudEvent> events) {
		int[] updateResults = this.insertEventStatement.apply(this.statementExecutor, streamId, EVENT_FORMAT, expectedVersion, events);
		if (Arrays.stream(updateResults).anyMatch(i -> i != 1)) {
			throw new JdbcEventStreamException("Failed to write all events");
		}
		return updateResults.length;
	}

	Stream<CloudEvent> streamEvents(UUID streamId) {
		return this.selectEventsStatement.apply(this.statementExecutor, streamId, r -> EVENT_FORMAT.deserialize(r.getBytes(SelectEventsStatement.DATA_NAME)));
	}

	/**
	 * Builder for constructing a {@link JdbcEventStreamRepository}.
	 */
	public static class Builder {
		private StatementExecutor statementExecutor;
		private EventSchema schema = new EventSchema();
		private CreateCloudEventTableStatement createCloudEventTableStatement;
		private GetStreamVersionStatement getStreamVersionStatement;
		private InsertEventStatement insertEventStatement;
		private SelectEventsStatement selectEventsStatement;

		private Builder() {
		}

		/**
		 * Sets the {@link StatementExecutor} to use for executing SQL statements.
		 *
		 * @param statementExecutor the statement executor
		 * @return this builder
		 */
		public Builder statementExecutor(StatementExecutor statementExecutor) {
			this.statementExecutor = statementExecutor;
			return this;
		}

		/**
		 * Sets the {@link EventSchema} to use for the event stream repository.
		 *
		 * @param schema the event schema
		 * @return this builder
		 */
		public Builder schema(EventSchema schema) {
			this.schema = schema;
			return this;
		}

		/**
		 * Sets the {@link CreateCloudEventTableStatement} to use for creating the event table.
		 *
		 * @param createCloudEventTableStatement the create table statement
		 * @return this builder
		 */
		public Builder createCloudEventTableStatement(CreateCloudEventTableStatement createCloudEventTableStatement) {
			this.createCloudEventTableStatement = createCloudEventTableStatement;
			return this;
		}

		/**
		 * Sets the {@link GetStreamVersionStatement} to use for getting the version of a stream.
		 *
		 * @param getStreamVersionStatement the get stream version statement
		 * @return this builder
		 */
		public Builder getStreamVersionStatement(GetStreamVersionStatement getStreamVersionStatement) {
			this.getStreamVersionStatement = getStreamVersionStatement;
			return this;
		}

		/**
		 * Sets the {@link InsertEventStatement} to use for inserting events into the event table.
		 *
		 * @param insertEventStatement the insert event statement
		 * @return this builder
		 */
		public Builder insertEventStatement(InsertEventStatement insertEventStatement) {
			this.insertEventStatement = insertEventStatement;
			return this;
		}

		/**
		 * Sets the {@link SelectEventsStatement} to use for selecting events from the event table.
		 *
		 * @param selectEventsStatement the select events statement
		 * @return this builder
		 */
		public Builder selectEventsStatement(SelectEventsStatement selectEventsStatement) {
			this.selectEventsStatement = selectEventsStatement;
			return this;
		}

		/**
		 * Builds the {@link JdbcEventStreamRepository}.
		 *
		 * @return the event stream repository
		 */
		public JdbcEventStreamRepository build() {
			return new JdbcEventStreamRepository(this);
		}
	}
}
