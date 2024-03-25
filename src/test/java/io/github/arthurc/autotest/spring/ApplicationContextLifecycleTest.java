/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

}
