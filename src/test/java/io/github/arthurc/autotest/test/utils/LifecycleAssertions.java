/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.test.utils;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

public class LifecycleAssertions {
	public static LifecycleAssert assertThat(Lifecycle lifecycle) {
		return new LifecycleAssert(lifecycle);
	}
}
