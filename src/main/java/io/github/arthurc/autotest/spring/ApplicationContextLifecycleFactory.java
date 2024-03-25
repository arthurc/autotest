/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleFactory;

public class ApplicationContextLifecycleFactory implements LifecycleFactory {
	@Override
	public Lifecycle createLifecycle() {
		return new ApplicationContextLifecycle();
	}
}
