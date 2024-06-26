/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.model;

/**
 * Represents an event that can be emitted by the application.
 */
public sealed interface Event permits RunEvent {
}
