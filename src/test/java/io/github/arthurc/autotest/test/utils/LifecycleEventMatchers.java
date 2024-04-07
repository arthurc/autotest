/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.test.utils;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public class LifecycleEventMatchers {
	private LifecycleEventMatchers() {
	}

	public static Matcher<LifecycleEvent> afterBegin(Matcher<LifecycleEvent> matcher) {
		return allOf(instanceOf(LifecycleEvent.AfterBegin.class), matcher);
	}

	public static Matcher<LifecycleEvent> beforeEnd(Matcher<LifecycleEvent> matcher) {
		return allOf(instanceOf(LifecycleEvent.BeforeEnd.class), matcher);
	}

	public static Matcher<LifecycleEvent> hasLifecycle(Matcher<Lifecycle> matcher) {
		return new HasLifecycle<>(matcher);
	}

	public static Matcher<LifecycleEvent> hasLifecycle(Class<? extends Lifecycle> type) {
		return hasLifecycle(instanceOf(type));
	}

	private static class HasLifecycle<T extends Lifecycle> extends TypeSafeMatcher<LifecycleEvent> {
		private final Matcher<T> matcher;

		HasLifecycle(Matcher<T> matcher) {
			this.matcher = matcher;
		}

		@Override
		protected boolean matchesSafely(LifecycleEvent item) {
			return matcher.matches(item.lifecycle());
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("has lifecycle ").appendDescriptionOf(matcher);
		}
	}
}
