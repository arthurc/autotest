/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;

/**
 * A lifecycle for the execution of a test plan.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestPlanLifecycle extends Lifecycle {

	private Run run;
	private final TestPlanModel testPlan;

	public TestPlanLifecycle(TestPlanModel testPlan) {
		this.testPlan = testPlan;
	}

	/**
	 * Get the test plan.
	 *
	 * @return the test plan
	 */
	public TestPlanModel getTestPlan() {
		return testPlan;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		if (event instanceof LifecycleEvent.BeforeBegin
				&& event.lifecycle() == this
				&& find(Run.class).isEmpty()) {
			this.run = new Run();
			this.run.begin();
		} else if (event instanceof LifecycleEvent.AfterEnd
				&& event.lifecycle() == this
				&& this.run != null) {
			this.run.end();
			this.run = null;
		}
	}
}
