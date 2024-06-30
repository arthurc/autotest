/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilTest {

	@Test
	void Map_should_call_the_function_and_return_the_result() {
		String result = ExceptionUtil.map(t -> t + "bar", RuntimeException::new).apply("foo");

		assertEquals("foobar", result);
	}

	@Test
	void Map_should_throw_a_runtime_exception_when_the_function_throws_an_exception() {
		assertThrows(IllegalStateException.class, () -> ExceptionUtil.map(t -> {
			throw new Exception("foo");
		}, IllegalStateException::new).apply("bar"));
	}

}