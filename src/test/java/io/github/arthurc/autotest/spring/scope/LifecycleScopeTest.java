package io.github.arthurc.autotest.spring.scope;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.NoSuchLifecycleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LifecycleScopeTest {

	private final ObjectFactory<Object> objectFactory = Object::new;

	private final LifecycleScope scope = new LifecycleScope(ExampleLifecycle.class);

	@Nested
	class No_active_scope {
		@Test
		void Throws_an_exception_when_trying_to_get_an_object() {
			assertThatThrownBy(() -> scope.get("foo", objectFactory))
					.isInstanceOf(NoSuchLifecycleException.class);
		}

		@Test
		void Throws_an_exception_when_trying_to_register_a_destruction_callback() {
			Runnable callback = () -> {
			};

			assertThatThrownBy(() -> scope.registerDestructionCallback("foo", callback))
					.isInstanceOf(NoSuchLifecycleException.class);
		}
	}

	@Nested
	class With_an_active_scope {

		private ExampleLifecycle lifecycle;

		@BeforeEach
		void setUp() {
			this.lifecycle = new ExampleLifecycle();
			this.lifecycle.begin();
		}

		@AfterEach
		void tearDown() {
			if (this.lifecycle != null) {
				this.lifecycle.end();
			}
		}

		@Test
		void Returns_a_new_object_when_getting_an_object() {
			Object object = scope.get("foo", objectFactory);

			assertThat(object).isNotNull();
			assertThat(this.lifecycle.getAttribute("foo")).isSameAs(object);
		}

		@Test
		void Returns_the_same_object_when_getting_an_object_twice() {
			Object object1 = scope.get("foo", objectFactory);
			Object object2 = scope.get("foo", objectFactory);

			assertThat(object1).isSameAs(object2);
		}

		@Test
		void Removes_an_object_and_the_lifecycle_attribute() {
			Object object = scope.get("foo", objectFactory);

			Object removedObject = scope.remove("foo");

			assertThat(removedObject).isSameAs(object);
			assertThat(this.lifecycle.getAttribute("foo")).isNull();
		}

		@Test
		void Executes_destruction_callbacks_when_the_lifecycle_ends() {
			Runnable callback = mock();
			scope.registerDestructionCallback("foo", callback);

			this.lifecycle.end();
			this.lifecycle = null;

			verify(callback).run();
		}
	}

	static class ExampleLifecycle extends Lifecycle {
	}

}