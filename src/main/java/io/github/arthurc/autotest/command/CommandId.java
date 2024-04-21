/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.command;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record CommandId(@JsonValue UUID id) {
	public CommandId() {
		this(UUID.randomUUID());
	}
}
