/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

/**
 * A lifecycle for the execution of a test plan.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestPlanLifecycle extends Lifecycle {

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

}
