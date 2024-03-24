/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AutotestAutoConfiguration {

	@Bean
	ApplicationContextLifecycle.Registrar applicationContextLifecycleRegistrar(ApplicationContext applicationContext) {
		return new ApplicationContextLifecycle.Registrar(applicationContext);
	}

}
