package io.github.arthurc.autotest.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputTextTest {

	@Test
	void shouldBeAbleToExtractText() {
		assertThat(new InputText("text").tokens()).containsExactly(
				new InputToken.Text("text"));
	}

	@Test
	void shouldBeAbleToParseKeys() {
		assertThat(new InputText("{enter}").tokens()).containsExactly(
				new InputToken.Key("ENTER"));
	}

	@Test
	void shouldBeAbleToParseMultipleKeys() {
		assertThat(new InputText("{enter}{shift}").tokens()).containsExactly(
				new InputToken.Key("ENTER"),
				new InputToken.Key("SHIFT"));
	}

	@Test
	void shouldBeAbleToParseTextAndKeys() {
		assertThat(new InputText("foo{enter}bar{shift}").tokens()).containsExactly(
				new InputToken.Text("foo"),
				new InputToken.Key("ENTER"),
				new InputToken.Text("bar"),
				new InputToken.Key("SHIFT"));
	}

	@Test
	void shouldReturnTheStringForToString() {
		assertThat(new InputText("foo{enter}bar{shift}")).hasToString("foo{enter}bar{shift}");
	}

}