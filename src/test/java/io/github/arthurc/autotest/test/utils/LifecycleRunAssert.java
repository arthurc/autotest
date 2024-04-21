/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.test.utils;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import org.assertj.core.api.FactoryBasedNavigableListAssert;
import org.assertj.core.api.HamcrestCondition;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.List;

public class LifecycleRunAssert extends FactoryBasedNavigableListAssert<LifecycleRunAssert, List<? extends LifecycleEvent>, LifecycleEvent, ObjectAssert<LifecycleEvent>> {
	public LifecycleRunAssert(List<LifecycleEvent> actual) {
		super(actual, LifecycleRunAssert.class, new ObjectAssertFactory<>());
	}

	@SafeVarargs
	public final LifecycleRunAssert publishedEventsMatches(Matcher<LifecycleEvent>... matchers) {
		return is(new HamcrestCondition<>(Matchers.contains(matchers)));
	}

	public LifecycleRunAssert noPublishedEvent(Matcher<? extends LifecycleEvent> matcher) {
		return has(new HamcrestCondition<>(Matchers.hasItem(Matchers.not(matcher))));
	}
}
