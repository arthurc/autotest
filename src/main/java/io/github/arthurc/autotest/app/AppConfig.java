/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arthurc.autotest.app.model.AppCloudEventConverter;
import io.github.arthurc.autotest.app.model.Event;
import io.github.arthurc.autotest.eventing.ApplicationService;
import io.github.arthurc.autotest.eventing.CloudEventConverter;
import io.github.arthurc.autotest.eventing.EventStreamRepository;
import io.github.arthurc.autotest.eventing.ReflectiveCloudEventTypeMapper;
import io.github.arthurc.autotest.eventing.jdbc.JdbcEventStreamRepository;
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
	EventStreamRepository eventStreamRepository() throws Exception {
		JdbcEventStreamRepository eventStreamRepository = JdbcEventStreamRepository.builder()
				.statementExecutor(new SpringJdbcStatementExecutor(cloudEventDataSource()))
				.build();
		eventStreamRepository.createSchema();
		return eventStreamRepository;
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
