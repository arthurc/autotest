/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

import java.io.Serial;

/**
 * Exception that is thrown when a lifecycle is not found on the callstack.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class NoSuchLifecycleException extends LifecycleException {
	@Serial
	private static final long serialVersionUID = -206475236624083781L;

	public NoSuchLifecycleException() {
	}
}
