/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arthurc.autotest.lifecycle.spring.LifecycleScope;
import io.github.arthurc.autotest.model.Event;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.run.RunScoped;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanScoped;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.application.service.blocking.ApplicationService;
import org.occurrent.application.service.blocking.generic.GenericApplicationService;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.inmemory.InMemoryEventStore;
import org.occurrent.subscription.inmemory.InMemorySubscriptionModel;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration(proxyBeanMethods = false)
@ComponentScan
class AutotestAutoConfiguration {

	@Bean
	public static CustomScopeConfigurer autotestScopesConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope(TestPlanScoped.NAME, new LifecycleScope(TestPlanLifecycle.class));
		configurer.addScope(RunScoped.NAME, new LifecycleScope(Run.class));
		return configurer;
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(EventStore.class)
	static class OccurrentConfiguration {

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
}
