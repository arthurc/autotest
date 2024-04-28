/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.eventhandling.jdbc;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EventSchema {
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([A-Za-z-_]+)}");

	public enum Key {
		EVENT_TABLE_NAME("CloudEvent"),
		EVENT_ID_COLUMN_NAME("eventId"),
		STREAM_ID_COLUMN_NAME("streamId"),
		GLOBAL_SEQUENCE_NUMBER_COLUMN_NAME("globalSequenceNumber"),
		GLOBAL_SEQUENCE_NUMBER_TYPE("BIGSERIAL"),
		SEQUENCE_NUMBER_COLUMN_NAME("sequenceNumber"),
		DATA_COLUMN_NAME("data"),
		DATA_TYPE("JSON");

		final String defaultValue;

		Key(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	private final Map<Key, String> schema = new EnumMap<>(Key.class);

	public String compile(CharSequence input) {
		return VARIABLE_PATTERN.matcher(input).replaceAll(matchResult ->
				get(Key.valueOf(matchResult.group(1).toUpperCase().replaceAll("-", "_"))));
	}

	public String get(Key key) {
		return schema.getOrDefault(key, key.defaultValue);
	}

	public void set(Key key, String value) {
		schema.put(key, value);
	}

}
