/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

/**
 * Exception thrown when a lifecycle operation fails.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class LifecycleException extends RuntimeException {

	public LifecycleException(String message) {
		super(message);
	}

	public LifecycleException(String message, Throwable cause) {
		super(message, cause);
	}

}
