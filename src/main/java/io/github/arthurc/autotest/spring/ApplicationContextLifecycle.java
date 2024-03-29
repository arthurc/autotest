/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * A lifecycle that is attached to an {@link ApplicationContext} and autowires the value of the result
 * of all nested lifecycles.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ApplicationContextLifecycle extends Lifecycle {
	private ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		if (event instanceof LifecycleEvent.AfterBegin afterBegin
				&& afterBegin.lifecycle() instanceof TestPlanLifecycle
				&& this.applicationContext == null) {
			new SpringApplicationBuilder(ApplicationContextLifecycleApplication.class).run();
			Assert.notNull(this.applicationContext, "ApplicationContext was not set");
		}
	}

	public static class Registrar {
		private final ApplicationContext applicationContext;

		public Registrar(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@PostConstruct
		void init() {
			Lifecycle.find(ApplicationContextLifecycle.class).ifPresent(l -> l.setApplicationContext(this.applicationContext));
		}

		@PreDestroy
		void destroy() {
			Lifecycle.find(ApplicationContextLifecycle.class).filter(l -> l.applicationContext == this.applicationContext).ifPresent(l -> l.setApplicationContext(null));
		}
	}
}
