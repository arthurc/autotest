/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventing;

import java.util.UUID;

/**
 * Repository for event streams.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public interface EventStreamRepository {

	/**
	 * Finds an event stream by its ID.
	 *
	 * @param id the ID of the event stream
	 * @return the event stream
	 */
	EventStream findById(UUID id);
}
