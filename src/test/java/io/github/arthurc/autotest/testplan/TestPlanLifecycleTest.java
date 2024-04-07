package io.github.arthurc.autotest.testplan;

import io.github.arthurc.autotest.run.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.arthurc.autotest.test.utils.LifecycleAssertions.assertThat;
import static io.github.arthurc.autotest.test.utils.LifecycleEventMatchers.afterBegin;
import static io.github.arthurc.autotest.test.utils.LifecycleEventMatchers.beforeEnd;
import static io.github.arthurc.autotest.test.utils.LifecycleEventMatchers.hasLifecycle;
import static org.hamcrest.Matchers.equalTo;

class TestPlanLifecycleTest {
	private TestPlanLifecycle testPlanLifecycle;

	@BeforeEach
	void setUp() {
		testPlanLifecycle = new TestPlanLifecycle(new TestPlanModel());
	}

	@Test
	void shouldStartARunIfThereIsNoParentRun() {
		assertThat(testPlanLifecycle).run().publishedEventsMatches(
				afterBegin(hasLifecycle(Run.class)),
				afterBegin(hasLifecycle(equalTo(testPlanLifecycle))),
				beforeEnd(hasLifecycle(equalTo(testPlanLifecycle))),
				beforeEnd(hasLifecycle(Run.class)));
	}

	@Test
	void shouldNotStartARunIfThereIsAParentRun() {
		new Run().call(() -> assertThat(testPlanLifecycle).run()).noPublishedEvent(hasLifecycle(Run.class));
	}

}
