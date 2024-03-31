package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.test.utils.EventCollector;
import io.github.arthurc.autotest.web.BaseUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeleniumBrowserTest {

	private WebDriver webDriver;
	private SeleniumBrowser seleniumBrowser;

	@BeforeEach
	void setUp() {
		this.webDriver = mock();

		this.seleniumBrowser = SeleniumBrowser.builder()
				.webDriver(this.webDriver)
				.baseUrl(new BaseUrl("https://example.com"))
				.build();
	}

	@Test
	void visitShouldNavigateToGivenUrl() {
		when(this.webDriver.navigate()).thenReturn(mock());

		this.seleniumBrowser.visit("/foo");

		verify(this.webDriver.navigate()).to("https://example.com/foo");
	}

	@Test
	void visitShouldThrowAnExceptionIfTheUrlIsNull() {
		assertThatThrownBy(() -> this.seleniumBrowser.visit(null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void closeShouldQuitWebDriver() {
		this.seleniumBrowser.close();

		verify(this.webDriver).quit();
	}

	@Test
	void visitShouldPublishAnEvent() {
		EventCollector eventCollector = new EventCollector();

		eventCollector.run(() -> this.seleniumBrowser.visit("/foo"));

		assertThat(eventCollector.getEvents()).anyMatch(event -> event instanceof LifecycleEvent.AfterBegin
				&& event.lifecycle() instanceof CommandExecutionLifecycle command
				&& command.getName().equals("visit")
				&& command.getParameters().equals(List.of(new Parameter("url", "https://example.com/foo")))
				&& command.getSubject().equals(Optional.of(this.seleniumBrowser)));
	}
}
