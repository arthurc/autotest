package io.github.arthurc.autotest.test.utils;

import io.github.arthurc.autotest.lifecycle.Lifecycle;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;

import java.util.ArrayList;
import java.util.List;

public class EventCollector extends Lifecycle {
	private final List<LifecycleEvent> events = new ArrayList<>();

	public List<LifecycleEvent> getEvents() {
		return events;
	}

	@Override
	protected void onLifecycleEvent(LifecycleEvent event) {
		this.events.add(event);
	}
}
