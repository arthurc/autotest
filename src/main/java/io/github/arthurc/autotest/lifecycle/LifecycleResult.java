/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;

/**
 * The result of a lifecycle operation that can be either successful or an error. If the operation is successful, the
 * result is {@link Ok} and contains the value of the operation. If the operation is an error, the result is
 * {@link Error} and contains the exception that caused the error.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public sealed interface LifecycleResult {
	Ok VOID = new Ok(null);

	/**
	 * A successful result of a lifecycle operation.
	 *
	 * @param value The value of the result.
	 */
	record Ok(Object value) implements LifecycleResult {
	}

	/**
	 * An error result of a lifecycle operation.
	 *
	 * @param exception The exception that caused the error.
	 */
	record Error(Throwable exception) implements LifecycleResult {
	}
}
