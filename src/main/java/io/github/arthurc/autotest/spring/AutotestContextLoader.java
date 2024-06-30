/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.aot.AotContextLoader;

public class AutotestContextLoader implements AotContextLoader {

	private final AotContextLoader delegate;

	public AutotestContextLoader() {
		this(new SpringBootContextLoader());
	}

	public AutotestContextLoader(AotContextLoader delegate) {
		this.delegate = delegate;
	}

	@Override
	public ApplicationContext loadContextForAotProcessing(MergedContextConfiguration mergedConfig) throws Exception {
		return getApplicationContext(() -> this.delegate.loadContextForAotProcessing(mergedConfig));
	}

	@Override
	public ApplicationContext loadContextForAotRuntime(MergedContextConfiguration mergedConfig, ApplicationContextInitializer<ConfigurableApplicationContext> initializer) throws Exception {
		return getApplicationContext(() -> this.delegate.loadContextForAotRuntime(mergedConfig, initializer));
	}

	@Override
	public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
		return getApplicationContext(() -> this.delegate.loadContext(mergedConfig));
	}

	@Override
	public ApplicationContext loadContext(String... locations) throws Exception {
		return getApplicationContext(() -> this.delegate.loadContext(locations));
	}

	@Override
	public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
		this.delegate.processContextConfiguration(configAttributes);
	}

	@Override
	public String[] processLocations(Class<?> clazz, String... locations) {
		return this.delegate.processLocations(clazz, locations);
	}

	private ApplicationContext getApplicationContext(ApplicationContextProvider provider) throws Exception {
		ApplicationContext applicationContext = Lifecycle.get(ApplicationContextLifecycle.class).getApplicationContext();
		return applicationContext != null ? applicationContext : provider.getApplicationContext();
	}

	private interface ApplicationContextProvider {
		ApplicationContext getApplicationContext() throws Exception;
	}
}
