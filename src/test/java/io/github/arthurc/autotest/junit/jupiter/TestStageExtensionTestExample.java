package io.github.arthurc.autotest.junit.jupiter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestStageExtension.class)
class TestStageExtensionTestExample {

	@BeforeAll
	static void beforeAll() {
	}

	@BeforeEach
	void beforeEach() {
	}

	@AfterEach
	void afterEach() {
	}

	@AfterAll
	static void afterAll() {
	}

	@Test
	void foo() {
	}

	@Test
	void bar() {
	}

}
