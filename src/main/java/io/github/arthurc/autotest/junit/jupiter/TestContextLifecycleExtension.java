/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.spring.ApplicationContextLifecycle;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.Closeable;

/**
 * A JUnit extension that manages the test context lifecycle and scopes it
 * to the lifecycle of {@link BeforeAllCallback} and {@link AfterAllCallback}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestContextLifecycleExtension implements BeforeAllCallback, AfterAllCallback {

	private static final ExtensionContext.Namespace SPRING_NAMESPACE = ExtensionContext.Namespace.create(SpringExtension.class);
	private static final ExtensionContext.Namespace CLEANUP_NAMESPACE = ExtensionContext.Namespace.create(TestContextLifecycleExtension.class);

	@Override
	public void beforeAll(ExtensionContext context) {
		new ApplicationContextLifecycle(SpringExtension.getApplicationContext(context)).begin();
	}

	@Override
	public void afterAll(final ExtensionContext context) {
		Lifecycle.get(ApplicationContextLifecycle.class).end();

		var store = context.getRoot().getStore(SPRING_NAMESPACE);
		var testClass = context.getRequiredTestClass();
		var testContextManager = store.get(testClass, TestContextManager.class);

		var cleanupStore = context.getRoot().getStore(CLEANUP_NAMESPACE);
		cleanupStore.getOrComputeIfAbsent(testClass, k -> cleanupTestContext(testContextManager));
	}

	private ExtensionContext.Store.CloseableResource cleanupTestContext(final TestContextManager testContextManager) {
		var testContext = testContextManager.getTestContext();
		return () -> {
			var applicationContext = testContext.getApplicationContext();

			testContext.markApplicationContextDirty(DirtiesContext.HierarchyMode.EXHAUSTIVE);
			if (applicationContext instanceof Closeable closeable) {
				closeable.close();
			}
		};
	}
}
