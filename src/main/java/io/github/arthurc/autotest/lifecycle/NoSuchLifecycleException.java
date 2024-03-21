/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

/**
 * Exception that is thrown when a lifecycle is not found on the callstack.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class NoSuchLifecycleException extends LifecycleException {
	public NoSuchLifecycleException() {
	}
}
