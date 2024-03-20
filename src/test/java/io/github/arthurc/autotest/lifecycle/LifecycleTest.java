/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TestLifecycle extends Lifecycle {
	@Override
	public void onLifecycleEvent(LifecycleEvent event) {
	}
}

@MockitoSettings
class LifecycleTest {

	@Mock
	private Runnable task;

	@Nested
	class A_Lifecycle_that_is_run {
		@Spy
		private TestLifecycle lifecycle = new TestLifecycle();

		@Test
		void Publishes_lifeycle_events_in_the_correct_order() {
			lifecycle.run(task);

			var inOrder = inOrder(lifecycle, task);
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeBegin(lifecycle));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterBegin(lifecycle));
			inOrder.verify(task).run();
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

		@Test
		void Publishes_lifecycle_events_even_when_the_task_throws_an_exception() {
			doThrow(new RuntimeException()).when(task).run();

			assertThatThrownBy(() -> lifecycle.run(task)).isInstanceOf(RuntimeException.class);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}
	}

	@Nested
	class A_Lifecycle_that_is_nested {

		@Spy
		private TestLifecycle outer = new TestLifecycle();

		@Spy
		private TestLifecycle inner = new TestLifecycle();

		@Test
		void Publishes_lifecycle_events_in_the_correct_order_for_nested_lifecycles() {
			outer.run(() -> inner.run(task));

			var inOrder = inOrder(outer, inner, task);
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.BeforeBegin(outer));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.AfterBegin(outer));
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.BeforeBegin(inner));
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.AfterBegin(inner));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.AfterBegin(inner));
			inOrder.verify(task).run();
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.BeforeEnd(inner));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.BeforeEnd(inner));
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.AfterEnd(inner));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.BeforeEnd(outer));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.AfterEnd(outer));
		}

		@Test
		void Does_not_publish_BeforeBegin_to_parent_lifecycles() {
			outer.run(() -> inner.run(task));

			verify(outer, never()).onLifecycleEvent(new LifecycleEvent.BeforeBegin(inner));
		}

		@Test
		void Does_not_publish_AfterEnd_to_parent_lifecycles() {
			outer.run(() -> inner.run(task));

			verify(outer, never()).onLifecycleEvent(new LifecycleEvent.AfterEnd(inner));
		}

	}

}
