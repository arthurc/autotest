/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring.boot.autoconfigure;

import io.github.arthurc.autotest.app.AppConfig;
import io.github.arthurc.autotest.lifecycle.spring.LifecycleScope;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration(proxyBeanMethods = false)
@Import({AppConfig.class, ChromeOptionsProperties.class, ApplicationContextLifecycle.Registrar.class})
public class AutotestAutoConfiguration {

	@Bean
	public static CustomScopeConfigurer autotestScopesConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope("testplan", new LifecycleScope(TestPlanLifecycle.class));
		return configurer;
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
			ChromeOptions chromeOptions = new ChromeOptions();
			properties.toChromeOptions(chromeOptions);
			return chromeOptions;
		}
	}
}
