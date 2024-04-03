/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * An input text is a text that contains keys that can be replaced with values.
 * The keys are enclosed in curly braces, e.g. {@code {KEY}}.
 *
 * @author Arthur Hartwig Carlsson
 * @see InputToken
 * @since 1.0.0
 */
public class InputText {
	private static final Pattern PATTERN = Pattern.compile("\\{[a-zA-Z]+}");
	private final String text;

	public InputText(String text) {
		this.text = text;
	}

	/**
	 * Returns a stream of tokens from the input text.
	 *
	 * @return A stream of tokens from the input text.
	 */
	public Stream<InputToken> tokens() {
		Matcher matcher = PATTERN.matcher(this.text);

		var builder = Stream.<InputToken>builder();

		int start = 0;
		while (matcher.find()) {
			int end = matcher.start();
			if (start != end) {
				builder.add(new InputToken.Text(this.text.substring(start, end)));
			}

			String key = matcher.group();
			builder.add(new InputToken.Key(key.substring(1, key.length() - 1).toUpperCase()));

			start = matcher.end();
		}

		if (start < this.text.length()) {
			builder.add(new InputToken.Text(this.text.substring(start)));
		}

		return builder.build();
	}

	@Override
	public String toString() {
		return this.text;
	}
}
