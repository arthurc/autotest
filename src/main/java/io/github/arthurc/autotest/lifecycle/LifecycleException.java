/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import java.io.Serial;

/**
 * Exception thrown when a lifecycle operation fails.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class LifecycleException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1383205608544200273L;

	public LifecycleException() {
	}

	public LifecycleException(String message) {
		super(message);
	}

	public LifecycleException(String message, Throwable cause) {
		super(message, cause);
	}

}
