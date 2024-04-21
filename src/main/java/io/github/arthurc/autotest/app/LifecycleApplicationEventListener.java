/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app;

import io.github.arthurc.autotest.app.model.Event;
import io.github.arthurc.autotest.app.model.RunModel;
import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.eventing.ApplicationService;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LifecycleApplicationEventListener {

	private final ApplicationService<Event> applicationService;

	public LifecycleApplicationEventListener(ApplicationService<Event> applicationService) {
		this.applicationService = applicationService;
	}

	@EventListener
	public void on(LifecycleEvent event) {
		RunId runId = event.lifecycle() instanceof Run testRun
				? testRun.getId()
				: event.lifecycle().findParent(Run.class).map(Run::getId).orElse(null);
		if (runId == null) {
			return;
		}

		var commandFn = switch (event) {
			case LifecycleEvent.AfterBegin afterBegin -> switch (afterBegin.lifecycle()) {
				case TestPlanLifecycle lifecycle -> RunModel.setTestPlan(lifecycle.getTestPlan());
				case TestExecutionLifecycle lifecycle -> RunModel.setTestExecutionStarted(lifecycle.getTestId());
				case Command command -> RunModel.setCommandStarted(command.getId(),
						command.getParentCommand().map(Command::getId).orElse(null),
						command.getName(),
						command.getParameters());
				default -> null;
			};
			case LifecycleEvent.BeforeEnd beforeEnd -> switch (beforeEnd.lifecycle()) {
				case TestExecutionLifecycle lifecycle -> RunModel.setTestExecutionEnded(lifecycle.getTestId());
				case Command command -> RunModel.setCommandEnded(command.getId());
				default -> null;
			};
			default -> null;
		};

		if (commandFn != null) {
			this.applicationService.execute(runId.id(), commandFn);
		}
	}
}
