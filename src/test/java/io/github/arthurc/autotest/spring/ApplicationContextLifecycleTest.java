/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextLifecycleTest {

	private AnnotationConfigApplicationContext applicationContext;
	private ApplicationContextLifecycle applicationContextLifecycle;

	@BeforeEach
	void setUp() {
		this.applicationContext = new AnnotationConfigApplicationContext();
		this.applicationContext.refresh();

		this.applicationContextLifecycle = new ApplicationContextLifecycle();
		this.applicationContextLifecycle.setApplicationContext(this.applicationContext);

	}

	@Nested
	class Handling_BeforeEnd_lifecycle_event {
		@Test
		void Autowires_the_value_from_the_return_value() {
			Foo foo = applicationContextLifecycle.call(Foo::new);

			assertThat(foo.applicationContext).isNotNull();
		}

		@Test
		void Autowires_the_value_from_nested_return_value() {
			applicationContextLifecycle.run(() -> {
				Foo foo = new TestLifecycle().call(Foo::new);

				assertThat(foo.applicationContext).isNotNull();
			});
		}
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
		void Unregisters_the_application_context_on_PreDestroy() {
			applicationContextLifecycle.run(() -> {
				ApplicationContextLifecycle.Registrar registrar = new ApplicationContextLifecycle.Registrar(applicationContext);
				registrar.init();
				assertThat(applicationContextLifecycle.getApplicationContext()).isSameAs(applicationContext);

				registrar.destroy();

				assertThat(applicationContextLifecycle.getApplicationContext()).isNull();
			});
		}
	}

	static class TestLifecycle extends Lifecycle {
	}

	static class Foo {
		@Autowired
		ApplicationContext applicationContext;
	}
}
