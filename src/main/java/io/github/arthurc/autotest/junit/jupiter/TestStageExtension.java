/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.LifecycleResult;
import io.github.arthurc.autotest.teststage.TestStage;
import io.github.arthurc.autotest.teststage.TestStageLifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

public class TestStageExtension implements InvocationInterceptor {

	@Override
	public void interceptBeforeAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		proceedInLifecycle(TestStage.BEFORE_TEST, invocation);
	}

	@Override
	public void interceptBeforeEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		proceedInLifecycle(TestStage.BEFORE_TEST, invocation);
	}

	@Override
	public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		proceedInLifecycle(TestStage.TEST_BODY, invocation);
	}

	@Override
	public void interceptAfterEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		proceedInLifecycle(TestStage.AFTER_TEST, invocation);
	}

	@Override
	public void interceptAfterAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		proceedInLifecycle(TestStage.AFTER_TEST, invocation);
	}

	private static void proceedInLifecycle(TestStage testStage, Invocation<Void> invocation) throws Throwable {
		TestStageLifecycle lifecycle = new TestStageLifecycle(testStage);
		lifecycle.begin();
		try {
			invocation.proceed();
			lifecycle.end(LifecycleResult.VOID);
		} catch (Throwable e) {
			lifecycle.end(new LifecycleResult.Error(e));
			throw e;
		}
	}

}
