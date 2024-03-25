/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
class ApplicationContextLifecycleApplication {
}
