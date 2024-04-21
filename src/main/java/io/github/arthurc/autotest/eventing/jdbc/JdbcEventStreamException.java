/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing.jdbc;

import java.io.Serial;

public class JdbcEventStreamException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1346179735420846939L;

	public JdbcEventStreamException(String message) {
		super(message);
	}

	public JdbcEventStreamException(String message, Throwable cause) {
		super(message, cause);
	}

}
