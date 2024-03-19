/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TestLifecycle extends Lifecycle {
	@Override
	public void onLifecycleEvent(LifecycleEvent event) {
	}
}

class LifecycleTest {

	@Nested
	class A_Lifecycle_that_is_run {
		@Test
		void Publishes_lifeycle_events_in_the_correct_order() {
			var lifecycle = spy(new TestLifecycle());
			var task = mock(Runnable.class);

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
			var lifecycle = spy(new TestLifecycle());
			var task = (Runnable) () -> {
				throw new RuntimeException();
			};

			assertThatThrownBy(() -> lifecycle.run(task)).isInstanceOf(RuntimeException.class);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

	}

}
