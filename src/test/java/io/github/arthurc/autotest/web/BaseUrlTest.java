package io.github.arthurc.autotest.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseUrlTest {

	private final BaseUrl baseUrl = new BaseUrl("http://example.com/foo");

	@Test
	void resolveShouldReturnTheResolvedUrl() {
		assertThat(this.baseUrl.resolve("/bar")).hasToString("http://example.com/foo/bar");
	}

	@Test
	void resolveShouldReturnTheResolvedUrlWithQuery() {
		assertThat(this.baseUrl.resolve("/bar?query=1")).hasToString("http://example.com/foo/bar?query=1");
	}

	@Test
	void resolveShouldReturnTheSameUrlWhenPathIsEmpty() {
		assertThat(this.baseUrl.resolve("")).hasToString("http://example.com/foo");
	}

	@Test
	void resolveShouldReturnTheSameUrlWhenPathIsNull() {
		assertThat(this.baseUrl.resolve(null)).hasToString("http://example.com/foo");
	}

	@Test
	void resolveShouldReturnTheSameUrlWhenPathIsAlreadyAbsolute() {
		assertThat(this.baseUrl.resolve("http://example.com/bar")).hasToString("http://example.com/bar");
	}

	@Test
	void resolveShouldReturnTheSameUrlWhenPathIsSlash() {
		assertThat(this.baseUrl.resolve("/")).hasToString("http://example.com/foo");
	}

	@Test
	void resolveShouldReturnAUrlWithoutDoubleSlashes() {
		assertThat(new BaseUrl("https://example.com/foo/").resolve("/bar")).hasToString("https://example.com/foo/bar");
	}

}
