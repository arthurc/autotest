/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.lifecycle.LifecycleResult;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.util.ExceptionUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.boot.loader.tools.MainClassFinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * A lifecycle that is attached to an {@link ApplicationContext} and autowires the value of the result
 * of all nested lifecycles.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class ApplicationContextLifecycle extends Lifecycle {
	private ApplicationContext applicationContext;

	public ApplicationContextLifecycle() {
	}

	public ApplicationContextLifecycle(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		if (event instanceof LifecycleEvent.AfterBegin afterBegin
				&& afterBegin.lifecycle() instanceof Run
				&& this.applicationContext == null) {
			runSpringApplication();
			Assert.notNull(this.applicationContext, "ApplicationContext was not set");
		} else if (event instanceof LifecycleEvent.BeforeEnd beforeEnd
				&& beforeEnd.result() instanceof LifecycleResult.Ok result
				&& ObjectUtils.unwrapOptional(result.value()) != null
				&& find(ApplicationContextLifecycle.class).orElse(null) == this
				&& this.applicationContext != null) {
			this.applicationContext.getAutowireCapableBeanFactory().autowireBean(result.value());
		}

		if (this.applicationContext != null) {
			this.applicationContext.publishEvent(event);
		}
	}

	protected void runSpringApplication() {
		Class<?> mainClass = Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
				.map(File::new)
				.filter(File::isDirectory)
				.map(ExceptionUtil.map(MainClassFinder::findSingleMainClass, RuntimeException::new))
				.map(ExceptionUtil.map(mainClassName -> ClassUtils.forName(mainClassName, null), RuntimeException::new))
				.findFirst()
				.orElseThrow();

		Method mainMethod = Objects.requireNonNull(ReflectionUtils.findMethod(mainClass, "main", String[].class), () -> "Main method not found on class " + mainClass);
		ReflectionUtils.invokeMethod(mainMethod, null, (Object) new String[0]);
	}

	public static class Registrar implements ApplicationContextAware {
		private ApplicationContext applicationContext;

		public Registrar() {
		}

		public Registrar(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}

		@PostConstruct
		void init() {
			Lifecycle.find(ApplicationContextLifecycle.class).filter(l -> l.applicationContext == null).ifPresent(l -> l.setApplicationContext(this.applicationContext));
		}
	}
}
