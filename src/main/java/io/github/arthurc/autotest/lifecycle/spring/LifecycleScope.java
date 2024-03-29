/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.lifecycle.spring;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * A Spring scope that is tied to a {@link Lifecycle}.
 *
 * @author Arthur Hartwig Carlsson
 * @since 1.0.0
 */
public class LifecycleScope implements Scope {
	private final Class<? extends Lifecycle> lifecycleClass;

	public LifecycleScope(Class<? extends Lifecycle> lifecycleClass) {
		this.lifecycleClass = lifecycleClass;
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Lifecycle lifecycle = Lifecycle.get(this.lifecycleClass);
		Object scopedObject = lifecycle.getAttribute(name);
		if (scopedObject != null) {
			return scopedObject;
		}

		synchronized (lifecycle) {
			scopedObject = lifecycle.getAttribute(name);
			if (scopedObject == null) {
				scopedObject = objectFactory.getObject();
				lifecycle.setAttribute(name, scopedObject);
			}
			return scopedObject;
		}
	}

	@Override
	public Object remove(String name) {
		Lifecycle lifecycle = Lifecycle.get(this.lifecycleClass);
		synchronized (lifecycle) {
			return lifecycle.removeAttribute(name);
		}
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		Lifecycle lifecycle = Lifecycle.get(this.lifecycleClass);
		synchronized (lifecycle) {
			lifecycle.registerDestructionCallback(name, callback);
		}
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Lifecycle.get(this.lifecycleClass).getId().toString();
	}
}
