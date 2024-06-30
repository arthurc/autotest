/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
			protected void runSpringApplication() {
				SpringApplication.run(TestConfig.class);
			}
		});
	}

	@AfterEach
	void tearDown() {
		Lifecycle.find(ApplicationContextLifecycle.class).ifPresent(lifecycle -> lifecycle.setApplicationContext(null));
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

		verify(applicationContextLifecycle).runSpringApplication();
		assertThat(applicationContextLifecycle.getApplicationContext()).isNotNull();
	}


	@Nested
	class Handling_BeforeEnd_lifecycle_event {
		private ApplicationContextLifecycle testContextLifecycle;

		@BeforeEach
		void setUp() {
			var applicationContext = new AnnotationConfigApplicationContext();
			applicationContext.refresh();

			this.testContextLifecycle = new ApplicationContextLifecycle(applicationContext);
		}

		@Test
		void Autowires_the_value_from_the_return_value() {
			Foo foo = testContextLifecycle.call(Foo::new);

			assertThat(foo.applicationContext).isNotNull();
		}

		@Test
		void Autowires_the_value_from_nested_return_value() {
			testContextLifecycle.run(() -> {
				Foo foo = new TestLifecycle().call(Foo::new);

				assertThat(foo.applicationContext).isNotNull();
			});
		}
	}

	static class TestLifecycle extends Lifecycle {
	}

	static class Foo {
		@Autowired
		ApplicationContext applicationContext;
	}

	@Configuration
	@Import(ApplicationContextLifecycle.Registrar.class)
	static class TestConfig {
	}
}
