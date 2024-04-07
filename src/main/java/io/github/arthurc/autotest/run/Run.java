/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.run;

import io.github.arthurc.autotest.lifecycle.Lifecycle;

/**
 * A run is a lifecycle that represents a run of a test. It is automatically created when a test plan is executed
 * if no run is already active.
 */
public class Run extends Lifecycle {

	private final RunId id = new RunId();

	/**
	 * Gets the id of the run.
	 *
	 * @return The id of the run.
	 */
	public RunId getId() {
		return this.id;
	}
}
