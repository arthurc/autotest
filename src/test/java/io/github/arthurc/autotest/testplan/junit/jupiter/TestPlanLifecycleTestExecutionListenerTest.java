package io.github.arthurc.autotest.testplan.junit.jupiter;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.autotest.example.ExampleApp;
import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class TestPlanLifecycleTestExecutionListenerTest {

	private final TestPlanLifecycleTestExecutionListener listener = new TestPlanLifecycleTestExecutionListener();

	@Nested
	class Starting_a_test_plan_execution {
		@Test
		void Begins_a_test_plan_lifecycle() {
			TestPlan testPlan = LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(TestPlanModelMapperTest.TestPlanModelMapperTestAutotestExample.class))
					.build());

			listener.testPlanExecutionStarted(testPlan);
			try {
				assertThat(Lifecycle.find(TestPlanLifecycle.class)).isPresent();
			} finally {
				listener.testPlanExecutionFinished(testPlan);
			}
		}

		@Test
		void Does_not_begin_a_test_plan_lifecycle_for_a_test_without_autotests() {
			TestPlan testPlan = LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(ExampleWithoutAutotest.class))
					.build());

			listener.testPlanExecutionStarted(testPlan);
			try {
				assertThat(Lifecycle.find(TestPlanLifecycle.class)).isEmpty();
			} finally {
				listener.testPlanExecutionFinished(testPlan);
			}
		}
	}

	@AutotestTest(classes = ExampleApp.class)
	static class ExampleWithAutotest {
		@Test
		void test() {
		}
	}

	static class ExampleWithoutAutotest {
		@Test
		void test() {
		}
	}

}