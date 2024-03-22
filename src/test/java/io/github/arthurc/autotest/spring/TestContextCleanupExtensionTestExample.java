/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestContextCleanupExtension.class})
public class TestContextCleanupExtensionTestExample {

	@Test
	void itWorks() {
	}

	@TestConfiguration
	static class TestConfig {
		@Bean(initMethod = "begin", destroyMethod = "end")
		ApplicationContextLifecycle applicationContextLifecycle(ApplicationContext applicationContext) {
			return new ApplicationContextLifecycle(applicationContext);
		}
	}

}
