/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class Attributes {
	private static final Logger log = Logger.getLogger(Attributes.class.getName());

	static final String DESTRUCTION_CALLBACK_NAME_PREFIX = Attributes.class.getName() + ".DESTRUCTION_CALLBACK.";

	private final Map<String, Object> attributes = new HashMap<>();

	Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	Object removeAttribute(String name) {
		return this.attributes.remove(name);
	}

	void registerDestructionCallback(String name, Runnable callback) {
		this.attributes.put(DESTRUCTION_CALLBACK_NAME_PREFIX + name, callback);
	}

	void executeDestructionCallbacks() {
		this.attributes.entrySet().stream()
				.filter(e -> e.getKey().startsWith(DESTRUCTION_CALLBACK_NAME_PREFIX))
				.forEach(e -> {
					try {
						((Runnable) e.getValue()).run();
					} catch (Throwable ex) {
						log.log(Level.WARNING, "Uncaught error in session attribute destruction callback", ex);
					}
				});
	}
}
