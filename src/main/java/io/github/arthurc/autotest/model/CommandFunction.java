package io.github.arthurc.autotest.model;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A functional interface that represents a command function that can be applied to a stream of events.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
@FunctionalInterface
public interface CommandFunction extends Function<Stream<Event>, Stream<Event>> {
}
