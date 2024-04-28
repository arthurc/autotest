package io.github.arthurc.autotest.eventhandling;

import io.github.arthurc.autotest.eventhandling.jdbc.JdbcEventStorageEngine;
import io.github.arthurc.autotest.spring.jdbc.SpringJdbcStatementExecutor;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
class EventStreamRepositoryWithJdbcStorageEngineTest extends AbstractEventStreamRepositoryWithStorageEngineTest {
	@Configuration
	static class TestConfig extends AbstractTestConfig {
		@Bean
		JdbcEventStorageEngine jdbcEventStorageEngine(JdbcTemplate jdbcTemplate) {
			JdbcEventStorageEngine storageEngine = JdbcEventStorageEngine.builder()
					.statementExecutor(new SpringJdbcStatementExecutor(jdbcTemplate))
					.build();
			storageEngine.createSchema();
			return storageEngine;
		}
	}
}
