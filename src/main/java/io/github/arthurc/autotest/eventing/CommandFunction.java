package io.github.arthurc.autotest.eventing;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A functional interface that represents a command function that can be applied to a stream of events.
 *
 * @param <T> the type of the objects in the stream
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
@FunctionalInterface
public interface CommandFunction<T> extends Function<Stream<T>, Stream<T>> {
}
