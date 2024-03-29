/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest;

import io.github.arthurc.autotest.spring.junit.jupiter.TestContextLifecycleExtension;
import io.github.arthurc.autotest.teststage.junit.jupiter.TestStageExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to be used on test classes to indicate that they are autotest tests.
 *
 * @author Arthur Hartwig Carlsson
 * @see SpringBootTest
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(AutotestTest.TAG)
@ExtendWith({TestContextLifecycleExtension.class, TestStageExtension.class})
public @interface AutotestTest {
	String TAG = "io.github.arthurc.autotest";

	@AliasFor(annotation = SpringBootTest.class)
	Class<?>[] classes() default {};
}
