/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.junit.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestContextLifecycleExtension.class})
public class TestContextLifecycleExtensionTestExample {

	@Test
	void itWorks() {
	}

	@TestConfiguration
	static class TestConfig {
	}

}
