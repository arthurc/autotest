/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

import java.util.List;

/**
 * A {@link ContextCustomizerFactory} that creates a {@link ContextCustomizer} that sets the parent of the test
 * application context to the {@link ApplicationContextLifecycle} application context.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ApplicationContextLifecycleContextCustomizerFactory implements ContextCustomizerFactory {

	@Override
	public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
		return new ApplicationContextLifecycleContextCustomizer();
	}

	record ApplicationContextLifecycleContextCustomizer() implements ContextCustomizer {
		@Override
		public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
			Lifecycle.find(ApplicationContextLifecycle.class)
					.map(ApplicationContextLifecycle::getApplicationContext)
					.ifPresent(context::setParent);
		}
	}
}
