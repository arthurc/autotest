/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import io.github.arthurc.autotest.lifecycle.spring.LifecycleScope;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AutotestAutoConfiguration {

	@Bean
	public static CustomScopeConfigurer autotestScopesConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope("testplan", new LifecycleScope(TestPlanLifecycle.class));
		return configurer;
	}

	@Bean
	ApplicationContextLifecycle.Registrar applicationContextLifecycleRegistrar(ApplicationContext applicationContext) {
		return new ApplicationContextLifecycle.Registrar(applicationContext);
	}

}
