package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.aot.AotContextLoader;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AutotestContextLoaderTest {
	private final GenericApplicationContext applicationContext = new GenericApplicationContext();

	private AotContextLoader delegate;
	private MergedContextConfiguration contextConfiguration;
	private AutotestContextLoader contextLoader;
	private ApplicationContextInitializer<ConfigurableApplicationContext> applicationContextInitializer;

	private final ApplicationContextLifecycle applicationContextLifecycle = new ApplicationContextLifecycle(applicationContext);

	@BeforeEach
	void setUp() {
		applicationContext.refresh();

		contextConfiguration = mock();
		delegate = mock();
		applicationContextInitializer = mock();
		contextLoader = new AutotestContextLoader(delegate);
	}

	@Test
	void loadContextForAotProcessing_should_get_the_application_context_from_the_lifecycle_if_one_exists() {
		testWithApplicationContext(() -> contextLoader.loadContextForAotProcessing(contextConfiguration));
	}

	@Test
	void loadContextForAotProcessing_should_get_the_application_context_from_the_delegate_if_no_lifecycle_has_a_context() throws Exception {
		when(delegate.loadContextForAotProcessing(any())).thenReturn(applicationContext);

		testWithoutApplicationContext(() -> contextLoader.loadContextForAotProcessing(contextConfiguration));
	}

	@Test
	void loadContext_should_get_the_application_context_from_the_lifecycle_if_one_exists() {
		testWithApplicationContext(() -> contextLoader.loadContext("foo"));
	}

	@Test
	void loadContext_should_get_the_application_context_from_the_delegate_if_no_lifecycle_has_a_context() throws Exception {
		when(delegate.loadContext(any(String[].class))).thenReturn(applicationContext);

		testWithoutApplicationContext(() -> contextLoader.loadContext("foo"));
	}

	@Test
	void loadContextForAotRuntime_should_get_the_application_context_from_the_lifecycle_if_one_exists() {
		testWithApplicationContext(() -> contextLoader.loadContextForAotRuntime(contextConfiguration, applicationContextInitializer));
	}

	@Test
	void loadContextForAotRuntime_should_get_the_application_context_from_the_delegate_if_no_lifecycle_has_a_context() throws Exception {
		when(delegate.loadContextForAotRuntime(any(), any())).thenReturn(applicationContext);

		testWithoutApplicationContext(() -> contextLoader.loadContextForAotRuntime(contextConfiguration, applicationContextInitializer));
	}

	@Test
	void loadContext_with_contextConfiguration_should_get_the_application_context_from_the_lifecycle_if_one_exists() {
		testWithApplicationContext(() -> contextLoader.loadContext(contextConfiguration));
	}

	@Test
	void loadContext_with_contextConfiguration_should_get_the_application_context_from_the_delegate_if_no_lifecycle_has_a_context() throws Exception {
		when(delegate.loadContext(any(MergedContextConfiguration.class))).thenReturn(applicationContext);

		testWithoutApplicationContext(() -> contextLoader.loadContext(contextConfiguration));
	}

	private void testWithApplicationContext(Callable<ApplicationContext> test) {
		var returnedApplicationContext = applicationContextLifecycle.call(test);

		verifyNoInteractions(delegate);
		assertThat(returnedApplicationContext).isSameAs(applicationContext);
	}

	private void testWithoutApplicationContext(Callable<ApplicationContext> test) throws Exception {
		var returnedApplicationContext = test.call();

		assertThat(returnedApplicationContext).isSameAs(applicationContext);
	}
}
