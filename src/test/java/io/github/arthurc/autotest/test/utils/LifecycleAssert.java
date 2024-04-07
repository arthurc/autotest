/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.test.utils;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.assertj.core.api.AbstractAssert;

public class LifecycleAssert extends AbstractAssert<LifecycleAssert, Lifecycle> {

	public LifecycleAssert(Lifecycle lifecycle) {
		super(lifecycle, LifecycleAssert.class);
	}

	public LifecycleRunAssert run() {
		EventCollector eventCollector = new EventCollector();
		eventCollector.run(() -> this.actual.run(() -> {
		}));
		return new LifecycleRunAssert(eventCollector.getEvents());
	}

}
