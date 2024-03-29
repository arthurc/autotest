/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a bean is test plan scoped.
 * This means that the bean is only available during the execution of a test plan.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Scope(TestPlanScoped.NAME)
public @interface TestPlanScoped {

	String NAME = "testplan";

	@AliasFor(annotation = Scope.class)
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
