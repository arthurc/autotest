/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.ContextLoader;

public class AutotestTestContextBootstrapper extends SpringBootTestContextBootstrapper {
	@Override
	protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
		return AutotestContextLoader.class;
	}
}
