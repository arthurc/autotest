/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * A test execution listener for the JUnit Platform that listens for test plan
 * execution events and ties the lifecycle of a test plan execution to a {@link TestPlanLifecycle}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class AutotestTestExecutionListener implements TestExecutionListener {

	@Override
	public void testPlanExecutionStarted(TestPlan testPlan) {
		new TestPlanLifecycle(new TestPlanModelMapper(testPlan).toTestPlanModel()).begin();
	}

	@Override
	public void testPlanExecutionFinished(TestPlan testPlan) {
		Lifecycle.get(TestPlanLifecycle.class).end();
	}
}
