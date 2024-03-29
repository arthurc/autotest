/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest;

import io.github.arthurc.autotest.testplan.TestPlanScoped;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a bean is autotest-scoped.
 * This is the same as {@link TestPlanScoped}, but with a different name for clarity.
 *
 * @author Arthur Hartwig Carlsson
 * @see TestPlanScoped
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@TestPlanScoped
public @interface AutotestScoped {
	@AliasFor(annotation = TestPlanScoped.class)
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
