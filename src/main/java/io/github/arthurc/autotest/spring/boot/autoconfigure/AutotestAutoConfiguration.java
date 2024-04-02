/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import io.github.arthurc.autotest.lifecycle.spring.LifecycleScope;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration(proxyBeanMethods = false)
@Import({ChromeOptionsProperties.class})
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

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(ChromeDriver.class)
	static class ChromeDriverConfiguration {

		@Bean
		@ConditionalOnMissingBean
		@Scope("prototype")
		ChromeDriver chromeDriver(ChromeOptions chromeOptions) {
			return new ChromeDriver(chromeOptions);
		}

		@Bean
		@ConditionalOnMissingBean
		ChromeOptions chromeOptions(ChromeOptionsProperties properties) {
			ChromeOptions chromeOptions = new ChromeOptions()
					.addArguments(properties.getArguments());
			if (properties.getBinary() != null) {
				chromeOptions.setBinary(properties.getBinary());
			}
			return chromeOptions;
		}
	}
}
