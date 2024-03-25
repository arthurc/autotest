/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.lifecycle.LifecycleResult;
import org.springframework.context.ApplicationContext;

/**
 * A {@link Lifecycle} that performs Spring autowiring on the result of a lifecycle.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestContextLifecycle extends Lifecycle {

	private final ApplicationContext applicationContext;

	public TestContextLifecycle(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		if (event instanceof LifecycleEvent.BeforeEnd beforeEnd
				&& this.applicationContext != null
				&& beforeEnd.result() instanceof LifecycleResult.Ok result
				&& result.value() != null) {
			this.applicationContext.getAutowireCapableBeanFactory().autowireBean(result.value());
		}
	}

}
