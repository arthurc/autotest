package io.github.arthurc.autotest.web;

/**
 * An input token is a token that is used to represent a part of an input string.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public sealed interface InputToken {
	/**
	 * Gets the string representation of the input token.
	 *
	 * @return The string representation of the input token.
	 */
	String string();

	/**
	 * A text token represents a part of an input string that is not a key.
	 */
	record Text(String string) implements InputToken {
	}

	/**
	 * A key token represents a key in an input string.
	 */
	record Key(String string) implements InputToken {
	}
}
