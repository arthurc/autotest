/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arthurc.autotest.app.model.Event;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.application.service.blocking.ApplicationService;
import org.occurrent.application.service.blocking.generic.GenericApplicationService;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.inmemory.InMemoryEventStore;
import org.occurrent.subscription.inmemory.InMemorySubscriptionModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration(proxyBeanMethods = false)
@ComponentScan
public class AppConfig {

	@Bean
	@ConditionalOnMissingBean
	EventStore eventStore(InMemorySubscriptionModel subscriptionModel) {
		return new InMemoryEventStore(subscriptionModel);
	}

	@Bean
	CloudEventConverter<Event> cloudEventConverter(ObjectMapper objectMapper) {
		return new JacksonCloudEventConverter<>(objectMapper, URI.create("urn:autotest"));
	}

	@Bean
	@ConditionalOnMissingBean
	ApplicationService<Event> applicationService(EventStore eventStore, CloudEventConverter<Event> cloudEventConverter) {
		return new GenericApplicationService<>(eventStore, cloudEventConverter);
	}

	@Bean
	InMemorySubscriptionModel inMemorySubscriptionModel() {
		return new InMemorySubscriptionModel();
	}
}
