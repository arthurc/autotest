package io.github.arthurc.autotest.spring.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.spring.TestContextLifecycle;
import io.github.arthurc.autotest.test.utils.EventCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class TestContextLifecycleExtensionTest {

	@Test
	void Begins_and_ends_the_test_context_lifecycle() {
		EventCollector eventCollector = new EventCollector();
		eventCollector.run(() -> EngineTestKit
				.engine("junit-jupiter")
				.selectors(DiscoverySelectors.selectClass(TestContextLifecycleExtensionTestExample.class))
				.execute());

		assertThat(eventCollector.getEvents())
				.anyMatch(event -> event instanceof LifecycleEvent.AfterBegin
						&& event.lifecycle() instanceof TestContextLifecycle)
				.anyMatch(event -> event instanceof LifecycleEvent.BeforeEnd
						&& event.lifecycle() instanceof TestContextLifecycle);
	}

	@Nested
	class Handling_BeforeEnd_lifecycle_event {
		private TestContextLifecycle testContextLifecycle;

		@BeforeEach
		void setUp() {
			var applicationContext = new AnnotationConfigApplicationContext();
			applicationContext.refresh();

			this.testContextLifecycle = new TestContextLifecycle(applicationContext);
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
}