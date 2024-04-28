/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arthurc.autotest.app.model.AppCloudEventConverter;
import io.github.arthurc.autotest.app.model.Event;
import io.github.arthurc.autotest.eventhandling.ApplicationService;
import io.github.arthurc.autotest.eventhandling.CloudEventConverter;
import io.github.arthurc.autotest.eventhandling.EventStorageEngine;
import io.github.arthurc.autotest.eventhandling.EventStreamRepository;
import io.github.arthurc.autotest.eventhandling.ReflectiveCloudEventTypeMapper;
import io.github.arthurc.autotest.eventhandling.jdbc.JdbcEventStorageEngine;
import io.github.arthurc.autotest.spring.jdbc.SpringJdbcStatementExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@ComponentScan
public class AppConfig {
	@Bean
	@ConditionalOnMissingBean
	EventStorageEngine eventStorageEngine() throws Exception {
		JdbcEventStorageEngine eventStorageEngine = JdbcEventStorageEngine.builder()
				.statementExecutor(new SpringJdbcStatementExecutor(cloudEventDataSource()))
				.build();
		eventStorageEngine.createSchema();
		return eventStorageEngine;
	}

	@Bean
	@ConditionalOnMissingBean
	EventStreamRepository eventStreamRepository(EventStorageEngine eventStorageEngine) {
		return new EventStreamRepository(eventStorageEngine);
	}

	@Bean
	CloudEventConverter<Event> cloudEventConverter(ObjectMapper objectMapper) {
		return new AppCloudEventConverter(new ReflectiveCloudEventTypeMapper<>(Event.class.getClassLoader()), objectMapper);
	}

	@Bean
	ApplicationService<Event> applicationService(EventStreamRepository eventStreamRepository, CloudEventConverter<Event> cloudEventConverter) {
		return new ApplicationService<>(eventStreamRepository, cloudEventConverter);
	}

	@Bean
	@ConfigurationProperties("spring.datasource.cloud-event")
	DataSourceProperties cloudEventDataSourceProperties() throws Exception {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.afterPropertiesSet();
		return dataSourceProperties;
	}

	@Bean
	DataSource cloudEventDataSource() throws Exception {
		return cloudEventDataSourceProperties().initializeDataSourceBuilder().build();
	}
}
