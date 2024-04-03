package io.github.arthurc.autotest.spring.boot.autoconfigure;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

class ChromeOptionsPropertiesTest {

	@Test
	void toChromeOptionsShouldMapTheProperties() {
		ChromeOptionsProperties properties = new ChromeOptionsProperties();
		properties.setBinary("binary");
		properties.getArguments().add("arg1");
		properties.getArguments().add("arg2");

		ChromeOptions chromeOptions = new ChromeOptions();
		properties.toChromeOptions(chromeOptions);

		assertThat(chromeOptions.asMap())
				.extractingByKey("goog:chromeOptions", map(String.class, Object.class))
				.containsAllEntriesOf(Map.of(
						"binary", "binary",
						"args", List.of("arg1", "arg2")
				));
	}

	@Test
	void toChromeOptionsShouldAllowNullBinary() {
		ChromeOptionsProperties properties = new ChromeOptionsProperties();
		properties.setBinary(null);

		ChromeOptions chromeOptions = new ChromeOptions();
		assertThatNoException().isThrownBy(() -> properties.toChromeOptions(chromeOptions));
	}

}