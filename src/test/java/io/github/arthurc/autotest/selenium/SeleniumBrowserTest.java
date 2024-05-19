package io.github.arthurc.autotest.selenium;

import io.github.arthurc.autotest.command.Command;
import io.github.arthurc.autotest.lifecycle.LifecycleEvent;
import io.github.arthurc.autotest.test.utils.EventCollector;
import io.github.arthurc.autotest.web.BaseUrl;
import io.github.arthurc.autotest.web.Element;
import io.github.arthurc.autotest.web.ElementNotFoundException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeleniumBrowserTest {

	private WebDriver webDriver;
	private SeleniumBrowser seleniumBrowser;

	@BeforeEach
	void setUp() {
		this.webDriver = mock(Answers.RETURNS_MOCKS);

		this.seleniumBrowser = SeleniumBrowser.builder()
				.webDriver(this.webDriver)
				.baseUrl(new BaseUrl("https://example.com"))
				.build();
	}

	@AfterEach
	void tearDown() {
		Awaitility.reset();
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
				&& event.lifecycle() instanceof Command command
				&& command.getName().equals("visit")
				&& command.getParameters().equals(Map.of("url", "https://example.com/foo"))
				&& command.getSubject().isEmpty());
	}

	@Test
	void findShouldFindElement() {
		WebElement webElement = mock();
		when(this.webDriver.findElement(any())).thenReturn(webElement);

		Element element = this.seleniumBrowser.find("selector");

		assertThat(element).isNotNull();
	}

	@Test
	void findShouldPublishAnEvent() {
		EventCollector eventCollector = new EventCollector();
		WebElement webElement = mock();
		when(this.webDriver.findElement(any())).thenReturn(webElement);

		eventCollector.run(() -> this.seleniumBrowser.find("selector"));

		assertThat(eventCollector.getEvents()).anyMatch(event -> event instanceof LifecycleEvent.AfterBegin
				&& event.lifecycle() instanceof Command command
				&& command.getName().equals("find")
				&& command.getParameters().equals(Map.of("css", "selector")));
	}

	@Test
	void findShouldThrowAnExceptionIfElementIsNotFound() {
		Awaitility.setDefaultTimeout(Duration.ofMillis(1));
		Awaitility.setDefaultPollInterval(Duration.ZERO);
		when(this.webDriver.findElement(any())).thenThrow(new NoSuchElementException("foo"));

		assertThatThrownBy(() -> this.seleniumBrowser.find("selector"))
				.isInstanceOf(ElementNotFoundException.class)
				.hasMessage("Element not found: selector");
	}

	@Test
	void getFocusedShouldReturnFocusedElement() {
		when(this.webDriver.switchTo()).thenReturn(mock(Answers.RETURNS_MOCKS));

		Element element = this.seleniumBrowser.getFocused();

		assertThat(element).isNotNull();
	}

	@Test
	void getFocusedShouldThrowAnExceptionIfNoElementIsFocused() {
		when(this.webDriver.switchTo()).thenReturn(mock());
		when(this.webDriver.switchTo().activeElement()).thenThrow(new NoSuchElementException("foo"));

		assertThatThrownBy(() -> this.seleniumBrowser.getFocused())
				.isInstanceOf(ElementNotFoundException.class)
				.hasMessage("No focused element found");
	}

}
