/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.run;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record RunId(@JsonValue UUID id) {
	public RunId() {
		this(UUID.randomUUID());
	}

	public RunId(String id) {
		this(UUID.fromString(id));
	}
}
