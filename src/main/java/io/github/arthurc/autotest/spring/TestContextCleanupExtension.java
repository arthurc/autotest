/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.spring;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.Closeable;

/**
 * An extension that cleans up the test context after all tests have run.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class TestContextCleanupExtension implements AfterAllCallback {

	private static final ExtensionContext.Namespace SPRING_NAMESPACE = ExtensionContext.Namespace.create(SpringExtension.class);
	private static final ExtensionContext.Namespace CLEANUP_NAMESPACE = ExtensionContext.Namespace.create(TestContextCleanupExtension.class);

	@Override
	public void afterAll(final ExtensionContext context) {
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
