package io.github.arthurc.autotest.teststage.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.test.utils.EventCollector;
import io.github.arthurc.autotest.teststage.TestStage;
import io.github.arthurc.autotest.teststage.TestStageLifecycle;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class TestStageExtensionTest {

	@Test
	void Publishes_event_lifecycle_events_in_the_correct_order() {
		EventCollector eventCollector = new EventCollector();
		eventCollector.run(() -> EngineTestKit.engine("junit-jupiter")
				.selectors(selectClass(TestStageExtensionTestExample.class))
				.execute());

		assertThat(eventCollector.getEvents())
				.filteredOn(event -> event instanceof LifecycleEvent.AfterBegin && event.lifecycle() instanceof TestStageLifecycle)
				.extracting(event -> ((TestStageLifecycle) event.lifecycle()).getTestStage())
				.containsExactly(
						TestStage.BEFORE_TEST,
						TestStage.BEFORE_TEST, TestStage.TEST_BODY, TestStage.AFTER_TEST, // foo()
						TestStage.BEFORE_TEST, TestStage.TEST_BODY, TestStage.AFTER_TEST, // bar()
						TestStage.AFTER_TEST);
	}

}
