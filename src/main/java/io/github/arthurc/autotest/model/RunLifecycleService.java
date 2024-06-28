/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.model;

import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import org.occurrent.application.service.blocking.ApplicationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
class RunLifecycleService {

	private final ApplicationService<Event> applicationService;
	private final ApplicationContext applicationContext;

	public RunLifecycleService(ApplicationService<Event> applicationService, ApplicationContext applicationContext) {
		this.applicationService = applicationService;
		this.applicationContext = applicationContext;
	}

	@EventListener
	public void on(LifecycleEvent event) {
		event.lifecycle().findSelfOrParent(Run.class)
				.map(Run::getId)
				.ifPresent(runId -> this.applicationService.execute(runId.id(),
						RunModel.on(runId, event),
						events -> events.forEach(this.applicationContext::publishEvent)));
	}
}
