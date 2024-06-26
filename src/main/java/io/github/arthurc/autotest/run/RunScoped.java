/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.run;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a bean is run scoped.
 * This means that the bean is only available during the execution of an autotest run.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Scope(RunScoped.NAME)
public @interface RunScoped {

	String NAME = "run";

	@AliasFor(annotation = Scope.class)
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
