/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ApplicationContextLifecycleTest {

	private AnnotationConfigApplicationContext applicationContext;
	private ApplicationContextLifecycle applicationContextLifecycle;

	@BeforeEach
	void setUp() {
		this.applicationContext = new AnnotationConfigApplicationContext();
		this.applicationContext.refresh();

		this.applicationContextLifecycle = spy(new ApplicationContextLifecycle() {
			@Override
			protected SpringApplication getSpringApplication() {
				return new SpringApplication(TestConfig.class);
			}
		});
	}

	@Nested
	class The_registrar {
		@Test
		void Registers_the_application_context_on_PostConstruct() {
			applicationContextLifecycle.run(() -> {
				ApplicationContextLifecycle.Registrar registrar = new ApplicationContextLifecycle.Registrar(applicationContext);

				registrar.init();

				assertThat(applicationContextLifecycle.getApplicationContext()).isSameAs(applicationContext);
			});
		}

		@Test
		void Does_not_register_the_application_context_if_it_is_already_set() {
			ApplicationContext otherApplicationContext = mock();
			applicationContextLifecycle.setApplicationContext(applicationContext);

			applicationContextLifecycle.run(() ->
					new ApplicationContextLifecycle.Registrar(otherApplicationContext).init());

			assertThat(applicationContextLifecycle.getApplicationContext()).isSameAs(applicationContext);
		}

		@Test
		void Unregisters_the_application_context_on_PreDestroy() {
			ApplicationContextLifecycle.Registrar registrar = new ApplicationContextLifecycle.Registrar(applicationContext);

			applicationContextLifecycle.run(() -> {
				registrar.init();
				assertThat(applicationContextLifecycle.getApplicationContext()).isSameAs(applicationContext);
				registrar.destroy();
			});

			assertThat(applicationContextLifecycle.getApplicationContext()).isNull();
		}

		@Test
		void Does_not_unregister_if_the_application_context_and_the_context_being_closed_are_not_the_same() {
			ApplicationContext other = mock();
			applicationContextLifecycle.setApplicationContext(applicationContext);

			applicationContextLifecycle.run(() ->
					new ApplicationContextLifecycle.Registrar(other).destroy());

			assertThat(applicationContextLifecycle.getApplicationContext()).isSameAs(applicationContext);
		}
	}

	@Test
	void Lifecycle_events_gets_published_to_the_application_context() {
		LifecycleEvent event = mock();
		Consumer<Object> applicationListener = mock();
		applicationContextLifecycle.setApplicationContext(applicationContext);
		applicationContext.addApplicationListener(ApplicationListener.forPayload(applicationListener));

		applicationContextLifecycle.run(() ->
				applicationContextLifecycle.onLifecycleEvent(event));

		verify(applicationListener).accept(event);
	}

	@Test
	void Lifecycle_events_does_not_get_published_to_the_application_context_if_it_is_not_set() {
		LifecycleEvent event = mock();

		applicationContextLifecycle.run(() ->
				assertThatNoException().isThrownBy(() -> applicationContextLifecycle.onLifecycleEvent(event)));
	}

	@Test
	void Creates_a_new_application_context_if_not_set_when_a_run_begins() {
		applicationContextLifecycle.run(() ->
				applicationContextLifecycle.onLifecycleEvent(new LifecycleEvent.AfterBegin(new Run())));

		verify(applicationContextLifecycle).getSpringApplication();
		assertThat(applicationContextLifecycle.getApplicationContext()).isNotNull();
	}

	@Configuration
	@Import(ApplicationContextLifecycle.Registrar.class)
	static class TestConfig {
	}
}
