package io.github.arthurc.autotest.testexecution.junit.jupiter;

import io.github.arthurc.autotest.test.utils.EventCollector;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.launcher.TestIdentifier;

import static org.assertj.core.api.Assertions.assertThat;

class TestExecutionLifecycleTestExecutionListenerTest {

	private final TestDescriptor testDescriptor = new AbstractTestDescriptor(UniqueId.parse("[foo:bar]"), "displayName") {

		@Override
		public TestDescriptor.Type getType() {
			return TestDescriptor.Type.TEST;
		}
	};
	private final TestIdentifier testIdentifier = TestIdentifier.from(testDescriptor);

	private final TestExecutionLifecycleTestExecutionListener listener = new TestExecutionLifecycleTestExecutionListener();

	@Nested
	class When_a_TestPlanLifecycle_is_active {

		@Test
		void The_TestExecutionLifecycle_should_be_able_to_start() {
			EventCollector eventCollector = new EventCollector();

			new TestPlanLifecycle(new TestPlanModel()).run(() -> eventCollector.run(() -> {
				listener.executionStarted(testIdentifier);
				listener.executionFinished(testIdentifier, null);
			}));

			assertThat(eventCollector.getEvents())
					.filteredOn(event -> event.lifecycle() instanceof TestExecutionLifecycle)
					.hasSize(2);
		}
	}

	@Nested
	class When_there_is_no_TestPlanLifecycle_active {

		@Test
		void The_TestExecutionLifecycle_should_be_able_to_start() {
			EventCollector eventCollector = new EventCollector();

			eventCollector.run(() -> {
				listener.executionStarted(testIdentifier);
				listener.executionFinished(testIdentifier, null);
			});

			assertThat(eventCollector.getEvents()).noneMatch(event -> event.lifecycle() instanceof TestExecutionLifecycle);
		}
	}

}