/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.command;

import java.util.UUID;

public record CommandId(UUID id) {
	public CommandId() {
		this(UUID.randomUUID());
	}
}
