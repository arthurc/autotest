/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testexecution;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.testplan.TestId;

public class TestExecutionLifecycle extends Lifecycle {
	private final TestId testId;

	public TestExecutionLifecycle(TestId testId) {
		this.testId = testId;
	}

	public TestId getTestId() {
		return testId;
	}
}
