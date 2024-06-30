/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest;

import io.github.arthurc.autotest.junit.jupiter.TestStageExtension;
import io.github.arthurc.autotest.spring.AutotestTestContextBootstrapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
@Tag(AutotestTest.TAG)
@BootstrapWith(AutotestTestContextBootstrapper.class)
@ExtendWith({SpringExtension.class, TestStageExtension.class})
@ContextConfiguration
public @interface AutotestTest {
	String TAG = "io.github.arthurc.autotest";

	@AliasFor(annotation = ContextConfiguration.class)
	Class<?>[] classes() default {};
}
