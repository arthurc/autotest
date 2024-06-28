/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.teststage;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestId;

/**
 * A lifecycle for the execution of a test stage,
 * whether it is before the test, after the test, or the test body itself.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestStageLifecycle extends Lifecycle {
	private final TestStage testStage;

	public TestStageLifecycle(TestStage testStage) {
		this.testStage = testStage;
	}

	/**
	 * Gets the test stage that this lifecycle represents.
	 *
	 * @return The test stage that this lifecycle represents.
	 */
	public TestStage getTestStage() {
		return testStage;
	}

	/**
	 * Gets the test ID of the test that this stage belongs to.
	 * If the stage is not part of a test, this method returns null.
	 *
	 * @return The test ID of the test that this stage belongs to.
	 */
	public TestId getTestId() {
		return findParent(TestExecutionLifecycle.class).map(TestExecutionLifecycle::getTestId).orElse(null);
	}
}
