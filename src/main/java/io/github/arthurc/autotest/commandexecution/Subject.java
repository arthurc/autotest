/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.commandexecution;

/**
 * A subject is a value that is the subject of a command execution lifecycle.
 *
 * @param subject The subject object.
 * @param <T>     The type of the subject.
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public record Subject<T>(T subject) {
}
