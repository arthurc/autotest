/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import java.util.List;

/**
 * A model of a test, whether it is a test case or a test suite.
 *
 * @param id          The unique identifier of the test.
 * @param parentId    The unique identifier of the parent test.
 * @param displayName The display name of the test.
 * @param children    The children of the test, if any. Only applicable if the test is a test suite.
 */
public record TestModel(
		TestId id,
		TestId parentId,
		String displayName,
		boolean isContainer,
		boolean isTest,
		List<TestModel> children
) {
}
