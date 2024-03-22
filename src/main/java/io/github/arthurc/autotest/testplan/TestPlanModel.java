/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import java.util.ArrayList;
import java.util.List;

/**
 * A model of a test plan containing a list of tests.
 *
 * @param tests The tests that make up the test plan.
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public record TestPlanModel(List<TestModel> tests) {
	public TestPlanModel() {
		this(new ArrayList<>());
	}
}
