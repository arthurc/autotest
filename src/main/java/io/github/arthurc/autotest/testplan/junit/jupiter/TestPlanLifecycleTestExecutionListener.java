/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * A test execution listener for the JUnit Platform that listens for test plan
 * execution events and ties the lifecycle of a test plan execution to a {@link TestPlanLifecycle}.
 * If the test plan contains no tests, the lifecycle is not started.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestPlanLifecycleTestExecutionListener implements TestExecutionListener {

	@Override
	public void testPlanExecutionStarted(TestPlan testPlan) {
		TestPlanModel testPlanModel = new TestPlanModelMapper(testPlan).toTestPlanModel();
		if (!testPlanModel.isEmpty()) {
			new TestPlanLifecycle(testPlanModel).begin();
		}
	}

	@Override
	public void testPlanExecutionFinished(TestPlan testPlan) {
		Lifecycle.find(TestPlanLifecycle.class).ifPresent(Lifecycle::end);
	}

}
