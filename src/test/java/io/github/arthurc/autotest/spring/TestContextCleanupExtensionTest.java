package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestContextCleanupExtensionTest {

	@Test
	void The_application_context_gets_closed_after_the_test_finishes() {
		class EventCollector extends Lifecycle {
			private final List<LifecycleEvent> events = new ArrayList<>();

			public List<LifecycleEvent> getEvents() {
				return events;
			}

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				this.events.add(event);
			}
		}

		EventCollector eventCollector = new EventCollector();
		eventCollector.run(() -> EngineTestKit
				.engine("junit-jupiter")
				.selectors(DiscoverySelectors.selectClass(TestContextCleanupExtensionTestExample.class))
				.execute());

		assertThat(eventCollector.getEvents()).anySatisfy(event -> {
			assertThat(event).isInstanceOf(LifecycleEvent.BeforeEnd.class);
			assertThat(event.lifecycle()).isInstanceOf(ApplicationContextLifecycle.class);
		});
	}

}