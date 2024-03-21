/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextLifecycleTest {

	@Nested
	class Handling_BeforeEnd_lifecycle_event {
		@Test
		void Autowires_the_value_from_the_return_value() {
			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
			applicationContext.refresh();

			Foo foo = new ApplicationContextLifecycle(applicationContext).call(Foo::new);

			assertThat(foo.applicationContext).isNotNull();
		}

		@Test
		void Autowires_the_value_from_nested_return_value() {
			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
			applicationContext.refresh();

			new ApplicationContextLifecycle(applicationContext).run(() -> {
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
}
