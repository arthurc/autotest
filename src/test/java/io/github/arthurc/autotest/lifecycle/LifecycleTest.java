/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TestLifecycle extends Lifecycle {
	@Override
	public void onLifecycleEvent(LifecycleEvent event) {
	}
}

class OtherLifecycle extends Lifecycle {
}

@MockitoSettings
class LifecycleTest {

	@Mock
	private Runnable task;

	@Nested
	class A_Lifecycle_that_is_run {
		@Spy
		private TestLifecycle lifecycle;

		@Test
		void Publishes_lifeycle_events_in_the_correct_order() {
			lifecycle.run(task);

			var inOrder = inOrder(lifecycle, task);
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeBegin(lifecycle));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterBegin(lifecycle));
			inOrder.verify(task).run();
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, LifecycleResult.VOID));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

		@Test
		void Publishes_lifecycle_events_even_when_the_task_throws_an_exception() {
			RuntimeException exception = new RuntimeException();
			doThrow(exception).when(task).run();

			assertThatThrownBy(() -> lifecycle.run(task)).isInstanceOf(RuntimeException.class);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, new LifecycleResult.Error(exception)));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}
	}

	@Nested
	class Calling_an_action_on_a_lifecycle {

		@Spy
		private TestLifecycle lifecycle;

		@Mock
		private Callable<String> action;

		@Test
		void Returns_the_result_of_the_action() {
			var result = lifecycle.call(() -> 42);

			assertThat(result).isEqualTo(42);
		}

		@Test
		void Publishes_lifeycle_events_in_the_correct_order() throws Exception {
			lifecycle.call(action);

			var inOrder = inOrder(lifecycle, action);
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeBegin(lifecycle));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterBegin(lifecycle));
			inOrder.verify(action).call();
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, new LifecycleResult.Ok(null)));
			inOrder.verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

		@Test
		void Throws_checked_exceptions_as_a_LifecycleException() throws Exception {
			doThrow(new Exception("Foo")).when(action).call();

			assertThatThrownBy(() -> lifecycle.call(action))
					.isInstanceOf(LifecycleException.class)
					.hasMessage("An exception occurred during the lifecycle action");
		}

		@Test
		void Rethrows_runtime_exceptions() throws Exception {
			RuntimeException exception = new RuntimeException("Foo");
			doThrow(exception).when(action).call();

			assertThatThrownBy(() -> lifecycle.call(action)).isEqualTo(exception);
		}

		@Test
		void Propagates_the_result_of_the_action_via_the_lifecycle_events() throws Exception {
			when(action.call()).thenReturn("Foo");

			lifecycle.call(action);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, new LifecycleResult.Ok("Foo")));
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
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.BeforeEnd(inner, LifecycleResult.VOID));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.BeforeEnd(inner, LifecycleResult.VOID));
			inOrder.verify(inner).onLifecycleEvent(new LifecycleEvent.AfterEnd(inner));
			inOrder.verify(outer).onLifecycleEvent(new LifecycleEvent.BeforeEnd(outer, LifecycleResult.VOID));
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

	@Nested
	class Beginning_a_lifecycle {

		@Spy
		private TestLifecycle lifecycle = new TestLifecycle();

		@Spy
		private TestLifecycle other = new TestLifecycle();

		@Test
		void Publishes_BeforeBegin_and_AfterBegin_events() {
			lifecycle.begin();

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeBegin(lifecycle));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterBegin(lifecycle));
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_begun_twice() {
			lifecycle.begin();

			assertThatThrownBy(lifecycle::begin)
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Lifecycle is already the current lifecycle on the callstack");
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_has_begun_further_down_in_the_callstack() {
			lifecycle.begin();
			other.begin();

			assertThatThrownBy(lifecycle::begin)
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Lifecycle is already the current lifecycle on the callstack");
		}

		@Test
		void Can_handle_that_the_lifecycle_event_callback_throws_an_exception() {
			doThrow(new RuntimeException()).when(lifecycle).onLifecycleEvent(isA(LifecycleEvent.BeforeBegin.class));

			lifecycle.begin();

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeBegin(lifecycle));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterBegin(lifecycle));
		}
	}

	@Nested
	class Ending_a_lifecycle {

		@Mock(answer = Answers.CALLS_REAL_METHODS)
		private TestLifecycle lifecycle;

		@Spy
		private TestLifecycle other = new TestLifecycle();

		@Test
		void Publishes_BeforeEnd_and_AfterEnd_events() {
			lifecycle.begin();
			lifecycle.end(LifecycleResult.VOID);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, LifecycleResult.VOID));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_ended_without_being_started() {
			assertThatThrownBy(() -> lifecycle.end(LifecycleResult.VOID))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Lifecycle is not the current lifecycle on the callstack");
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_ended_when_it_is_not_the_current_lifecycle_on_the_callstack() {
			lifecycle.begin();
			other.begin();

			assertThatThrownBy(() -> lifecycle.end(LifecycleResult.VOID))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Lifecycle is not the current lifecycle on the callstack");
		}

		@Test
		void Can_handle_that_the_lifecycle_event_callback_throws_an_exception() {
			doThrow(new RuntimeException()).when(lifecycle).onLifecycleEvent(isA(LifecycleEvent.BeforeEnd.class));

			lifecycle.begin();
			lifecycle.end(LifecycleResult.VOID);

			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.BeforeEnd(lifecycle, LifecycleResult.VOID));
			verify(lifecycle).onLifecycleEvent(new LifecycleEvent.AfterEnd(lifecycle));
		}

		@Test
		void Pops_the_lifecycle_from_the_callstack_even_if_an_exception_is_thrown_from_the_lifecycle_callback() {
			RuntimeException exception = new RuntimeException("Expected");
			lenient().doThrow(exception).when(other).onLifecycleEvent(isA(LifecycleEvent.BeforeEnd.class));

			lifecycle.run(() -> other.run(() -> {
			}));

			assertThat(Lifecycle.find(Lifecycle.class)).isEmpty();
		}
	}

	@Nested
	class Ending_a_lifecycle_without_a_result {
		@Mock(answer = Answers.CALLS_REAL_METHODS)
		private TestLifecycle lifecycle;

		@Test
		void Calls_end_with_a_void_result() {
			lifecycle.begin();
			lifecycle.end();

			verify(lifecycle).end(LifecycleResult.VOID);
		}
	}

	@Nested
	class Finding_a_lifecycle {
		@Test
		void Returns_the_first_lifecycle_of_a_type() {
			var lifecycle = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThat(Lifecycle.find(TestLifecycle.class)).hasValue(lifecycle);
				}
			});

			lifecycle.run(task);

			verify(task).run();
		}

		@Test
		void Returns_an_empty_optional_if_the_callstack_is_empty() {
			assertThat(Lifecycle.find(TestLifecycle.class)).isEmpty();
		}

		@Test
		void Returns_an_empty_optional_if_the_lifecycle_is_not_on_the_callstack() {
			var lifecycle = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThat(Lifecycle.find(OtherLifecycle.class)).isEmpty();
				}
			});

			lifecycle.run(task);

			verify(task).run();
		}

		@Test
		void Returns_the_first_lifecycle_of_a_type_in_a_nested_callstack() {
			var outer = new TestLifecycle();
			var inner = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThat(Lifecycle.find(TestLifecycle.class)).hasValue(inner);
				}
			});

			outer.run(() -> inner.run(task));

			verify(task).run();
		}

		@Test
		void Returns_empty_optional_if_the_predicates_does_not_match() {
			var lifecycle = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThat(Lifecycle.find(TestLifecycle.class, l -> false)).isEmpty();
				}
			});

			lifecycle.run(task);

			verify(task).run();
		}
	}

	@Nested
	class Getting_a_lifecycle {
		@Test
		void Returns_the_first_lifecycle_of_a_type() {
			var lifecycle = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThat(Lifecycle.get(TestLifecycle.class)).isSameAs(lifecycle);
				}
			});

			lifecycle.run(task);

			verify(task).run();
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_not_on_the_callstack() {
			assertThatThrownBy(() -> Lifecycle.get(TestLifecycle.class))
					.isInstanceOf(NoSuchLifecycleException.class);
		}

		@Test
		void Throws_an_exception_if_the_lifecycle_is_not_on_the_callstack_and_the_predicate_does_not_match() {
			var lifecycle = new TestLifecycle();
			var task = spy(new Runnable() {
				@Override
				public void run() {
					assertThatThrownBy(() -> Lifecycle.get(TestLifecycle.class, l -> false))
							.isInstanceOf(NoSuchLifecycleException.class);
				}
			});

			lifecycle.run(task);

			verify(task).run();
		}
	}
}
