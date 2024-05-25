/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.app;

import io.github.arthurc.autotest.app.model.Event;
import io.github.arthurc.autotest.app.model.RunModel;
import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.run.Run;
import io.github.arthurc.autotest.run.RunId;
import io.github.arthurc.autotest.testexecution.TestExecutionLifecycle;
import io.github.arthurc.autotest.testplan.TestPlanLifecycle;
import io.github.arthurc.autotest.teststage.TestStageLifecycle;
import org.occurrent.application.service.blocking.ApplicationService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LifecycleApplicationEventListener implements ApplicationContextAware {

	private final ApplicationService<Event> applicationService;
	private ApplicationContext applicationContext;

	public LifecycleApplicationEventListener(ApplicationService<Event> applicationService) {
		this.applicationService = applicationService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@EventListener
	public void on(LifecycleEvent event) {
		RunId runId = event.lifecycle().findSelfOrParent(Run.class).map(Run::getId).orElse(null);
		if (runId == null) {
			return;
		}

		var commandFn = switch (event) {
			case LifecycleEvent.AfterBegin afterBegin -> switch (afterBegin.lifecycle()) {
				case Run run -> RunModel.setRunStarted(run.getId());
				case TestPlanLifecycle lifecycle -> RunModel.setTestPlan(runId, lifecycle.getTestPlan());
				case TestExecutionLifecycle lifecycle -> RunModel.setTestExecutionStarted(runId, lifecycle.getTestId());
				case TestStageLifecycle lifecycle -> RunModel.setTestStageStarted(runId,
						lifecycle.getTestId(),
						lifecycle.getTestStage());
				case Command command -> RunModel.setCommandStarted(runId,
						command.getTest().getTestId(),
						command.getTestStage().map(TestStageLifecycle::getTestStage).orElse(null),
						command.getId(),
						command.getParentCommand().map(Command::getId).orElse(null),
						command.getName(),
						command.getParameters(),
						command.getSubject().map(o -> Integer.toString(o.hashCode())).orElse(null));
				default -> null;
			};
			case LifecycleEvent.BeforeEnd beforeEnd -> switch (beforeEnd.lifecycle()) {
				case Run __ -> RunModel.setRunEnded(runId);
				case TestExecutionLifecycle lifecycle -> RunModel.setTestExecutionEnded(runId, lifecycle.getTestId());
				case Command command -> RunModel.setCommandEnded(runId,
						command.getId(),
						command.getSubject().map(o -> Integer.toString(o.hashCode())).orElse(null));
				default -> null;
			};
			default -> null;
		};

		if (commandFn != null) {
			this.applicationService.execute(runId.id(), commandFn, events -> events.forEach(this.applicationContext::publishEvent));
		}
	}
}
