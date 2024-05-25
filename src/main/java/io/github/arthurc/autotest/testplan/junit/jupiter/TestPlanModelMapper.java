/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan.junit.jupiter;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.autotest.testplan.TestId;
import io.github.arthurc.autotest.testplan.TestModel;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.junit.platform.engine.TestTag;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.Optional;

class TestPlanModelMapper {

	private final TestPlan testPlan;
	private TestPlanModel testPlanModel;

	public TestPlanModelMapper(TestPlan testPlan) {
		this.testPlan = testPlan;
	}

	public TestPlanModel toTestPlanModel() {
		this.testPlanModel = new TestPlanModel();

		this.testPlan.getRoots().forEach(root ->
				this.testPlan.getChildren(root).stream()
						.flatMap(t -> toTestModel(t).stream())
						.forEach(child -> this.testPlanModel.tests().add(child)));
		return this.testPlanModel;
	}

	private Optional<TestModel> toTestModel(TestIdentifier testIdentifier) {
		if (!testIdentifier.getTags().contains(TestTag.create(AutotestTest.TAG))
				&& this.testPlan.getDescendants(testIdentifier).stream().noneMatch(t -> t.getTags().contains(TestTag.create(AutotestTest.TAG)))) {
			return Optional.empty();
		}

		return Optional.of(new TestModel(
				new TestId(testIdentifier.getUniqueId()),
				this.testPlan.getParent(testIdentifier)
						.filter(parent -> !this.testPlan.getRoots().contains(parent))
						.map(t -> new TestId(t.getUniqueId()))
						.orElse(null),
				testIdentifier.getDisplayName(),
				testIdentifier.getType().isContainer(),
				testIdentifier.getType().isTest(),
				this.testPlan.getChildren(testIdentifier).stream().flatMap(t -> toTestModel(t).stream()).toList()));
	}
}
