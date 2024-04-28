/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.jdbc;

import io.github.arthurc.autotest.eventhandling.jdbc.statement.StatementExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpringJdbcStatementExecutor implements StatementExecutor {

	private final JdbcTemplate jdbcTemplate;

	public SpringJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public SpringJdbcStatementExecutor(DataSource dataSource) {
		this(new JdbcTemplate(dataSource));
	}

	@Override
	public int update(String sql, PreparedStatementConfigurer configurer) {
		return this.jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(sql);
			configurer.configure(statement);
			return statement;
		});
	}

	@Override
	public <T> T query(String sql, PreparedStatementConfigurer configurer, ResultSetMapper<T> mapper, T defaultValue) {
		return this.jdbcTemplate.execute(connection -> {
			PreparedStatement statement = connection.prepareStatement(sql);
			configurer.configure(statement);
			return statement;
		}, (PreparedStatementCallback<T>) ps -> {
			ResultSet resultSet = ps.executeQuery();
			return resultSet.next() ? mapper.map(resultSet) : defaultValue;
		});
	}

	@Override
	public <T> Stream<T> stream(String sql, PreparedStatementConfigurer configurer, ResultSetMapper<T> mapper) {
		return this.jdbcTemplate.queryForStream(sql, configurer::configure, (rs, __) -> mapper.map(rs));
	}

	@Override
	public <T> int[] batchUpdate(String sql, Stream<T> stream, BatchFunction<T> f) {
		var index = IntStream.iterate(0, i -> i + 1).iterator();
		var batchArgs = stream.sequential().map(e -> f.apply(e, index.next())).toList();

		return this.jdbcTemplate.batchUpdate(sql, batchArgs);
	}
}
