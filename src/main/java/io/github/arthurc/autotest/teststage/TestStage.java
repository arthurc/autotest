/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.teststage;

/**
 * An enum representing the different stages of a test.
 */
public enum TestStage {
	/**
	 * The stage before the test or test container is executed. I.e. {@code @BeforeAll} or {@code @BeforeEach}.
	 */
	BEFORE_TEST,
	/**
	 * The stage after the test or test container is executed. I.e. {@code @AfterAll} or {@code @AfterEach}.
	 */
	AFTER_TEST,
	/**
	 * The stage where the test body is executed.
	 */
	TEST_BODY
}
