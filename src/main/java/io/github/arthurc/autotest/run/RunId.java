/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.run;

import java.util.UUID;

public record RunId(UUID id) {
	public RunId() {
		this(UUID.randomUUID());
	}
}
