/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.util;

import java.util.function.Function;

public final class ExceptionUtil {
	private ExceptionUtil() {
	}

	public static <T, R> Function<T, R> map(ThrowingFunction<T, R> fn, Function<Exception, RuntimeException> f) {
		return t -> {
			try {
				return fn.apply(t);
			} catch (Exception e) {
				throw f.apply(e);
			}
		};
	}

	@FunctionalInterface
	public interface ThrowingFunction<T, R> {
		R apply(T t) throws Exception;
	}
}
