/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.teststage;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

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

	public TestStage getTestStage() {
		return testStage;
	}
}
