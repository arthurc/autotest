/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.scope;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a bean is autotest-scoped.
 * This is the same as {@link RunScoped}, but with a different name for clarity.
 *
 * @author Arthur Hartwig Carlsson
 * @see RunScoped
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@RunScoped
public @interface AutotestScoped {
	@AliasFor(annotation = RunScoped.class)
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
