/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.lifecycle.LifecycleResult;
import org.springframework.context.ApplicationContext;

/**
 * A lifecycle that is attached to an {@link ApplicationContext} and autowires the value of the result
 * of all nested lifecycles.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ApplicationContextLifecycle extends Lifecycle {
	private final ApplicationContext applicationContext;

	public ApplicationContextLifecycle(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		if (event instanceof LifecycleEvent.BeforeEnd beforeEnd
				&& beforeEnd.result() instanceof LifecycleResult.Ok result
				&& result.value() != null) {
			this.applicationContext.getAutowireCapableBeanFactory().autowireBean(result.value());
		}
	}
}
