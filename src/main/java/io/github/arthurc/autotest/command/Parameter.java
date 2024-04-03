/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.command;

/**
 * A parameter has a value and an optional tag. The tag can be used to identify the parameter,
 * such as "url" or "username".
 *
 * @param tag   The tag of the parameter. May be null.
 * @param value The value of the parameter.
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public record Parameter(String tag, String value) {
	public Parameter(String value) {
		this(null, value);
	}

	@Override
	public String toString() {
		return tag() != null
				? tag() + ":" + value()
				: value();
	}
}
