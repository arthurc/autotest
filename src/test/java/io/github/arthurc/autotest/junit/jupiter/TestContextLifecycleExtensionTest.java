package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.test.utils.EventCollector;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;

class TestContextLifecycleExtensionTest {

	@Test
	void Begins_and_ends_an_application_context_lifecycle() {
		EventCollector eventCollector = new EventCollector();
		eventCollector.run(() -> EngineTestKit
				.engine("junit-jupiter")
				.selectors(DiscoverySelectors.selectClass(TestContextLifecycleExtensionTestExample.class))
				.execute());

		assertThat(eventCollector.getEvents())
				.anyMatch(event -> event instanceof LifecycleEvent.AfterBegin
						&& event.lifecycle() instanceof ApplicationContextLifecycle)
				.anyMatch(event -> event instanceof LifecycleEvent.BeforeEnd
						&& event.lifecycle() instanceof ApplicationContextLifecycle);
	}

}