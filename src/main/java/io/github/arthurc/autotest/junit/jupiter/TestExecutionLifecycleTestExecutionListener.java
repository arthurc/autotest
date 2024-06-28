/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 * A {@link TestExecutionListener} that starts and ends a {@link TestExecutionLifecycle}
 * and is scoped .
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestExecutionLifecycleTestExecutionListener implements TestExecutionListener {

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		if (Lifecycle.find(TestPlanLifecycle.class).isPresent()) {
			new TestExecutionLifecycle(new TestId(testIdentifier.getUniqueId())).begin();
		}
	}

	@Override
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
		Lifecycle.find(TestExecutionLifecycle.class).ifPresent(Lifecycle::end);
	}
}
