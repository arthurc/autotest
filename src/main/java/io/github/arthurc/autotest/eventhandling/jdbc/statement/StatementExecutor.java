/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventhandling.jdbc.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Abstraction for executing SQL statements and handling connection resources.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface StatementExecutor {

	/**
	 * Executes an update statement.
	 *
	 * @param sql        the SQL statement
	 * @param configurer the {@link PreparedStatementConfigurer} to configure the statement
	 */
	int update(String sql, PreparedStatementConfigurer configurer);

	/**
	 * Executes a query statement. If the query returns no results, the default value is returned.
	 *
	 * @param sql          the SQL statement
	 * @param configurer   the {@link PreparedStatementConfigurer} to configure the statement
	 * @param mapper       the {@link ResultSetMapper} to map the result set to a value
	 * @param defaultValue the default value to return if the query returns no results
	 * @param <T>          the type of the result
	 * @return the result of the query or the default value
	 */
	<T> T query(String sql, PreparedStatementConfigurer configurer, ResultSetMapper<T> mapper, T defaultValue);

	/**
	 * Streams the results of a query statement. The stream should be closed after use using try-with-resources.
	 *
	 * @param sql        the SQL statement
	 * @param configurer the {@link PreparedStatementConfigurer} to configure the statement
	 * @param mapper     the {@link ResultSetMapper} to map the result set to a value
	 * @param <T>        the type of the result
	 * @return a stream of the results
	 */
	<T> Stream<T> stream(String sql, PreparedStatementConfigurer configurer, ResultSetMapper<T> mapper);

	/**
	 * Executes a batch update statement. The batch function maps the objects to batch arguments.
	 *
	 * @param sql           the SQL statement
	 * @param events        the stream of objects to update
	 * @param batchFunction the {@link BatchFunction} to map objects to batch arguments
	 * @param <T>           the type of the objects
	 * @return an array of update counts for each statement
	 */
	<T> int[] batchUpdate(String sql, Stream<T> events, BatchFunction<T> batchFunction);

	/**
	 * Executes an update statement without any configuration.
	 *
	 * @param sql the SQL statement
	 */
	default int update(String sql) {
		return update(sql, ps -> {
		});
	}

	interface BatchFunction<T> {
		Object[] apply(T object, int index);
	}

	interface ResultSetMapper<T> {
		T map(ResultSet resultSet) throws SQLException;
	}

	interface PreparedStatementConfigurer {
		void configure(PreparedStatement preparedStatement) throws SQLException;
	}
}
