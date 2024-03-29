package io.github.arthurc.autotest.lifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AttributesTest {
	private Attributes attributes;

	@BeforeEach
	void setUp() {
		this.attributes = new Attributes();
	}

	@Test
	void getAttributeReturnsNullWhenAttributeDoesNotExist() {
		assertThat(this.attributes.getAttribute("nonexistent")).isNull();
	}

	@Test
	void getAttributeReturnsValueWhenAttributeExists() {
		this.attributes.setAttribute("key", "value");
		assertThat(this.attributes.getAttribute("key")).isEqualTo("value");
	}

	@Test
	void setAttributeStoresValue() {
		this.attributes.setAttribute("key", "value");

		assertThat(this.attributes.getAttribute("key")).isEqualTo("value");
	}

	@Test
	void removeAttributeRemovesValue() {
		this.attributes.setAttribute("key", "value");

		this.attributes.removeAttribute("key");

		assertThat(this.attributes.getAttribute("key")).isNull();
	}

	@Test
	void removeAttributeReturnsRemovedValue() {
		this.attributes.setAttribute("key", "value");

		assertThat(this.attributes.removeAttribute("key")).isEqualTo("value");
	}

	@Test
	void registerDestructionCallbackStoresCallback() {
		Runnable callback = () -> {
		};

		this.attributes.registerDestructionCallback("key", callback);

		assertThat(this.attributes.getAttribute(Attributes.DESTRUCTION_CALLBACK_NAME_PREFIX + "key")).isEqualTo(callback);
	}

	@Test
	void executeDestructionCallbacksExecutesCallbacks() {
		Runnable callback = mock();
		this.attributes.registerDestructionCallback("key", callback);

		this.attributes.executeDestructionCallbacks();

		verify(callback).run();
	}

	@Test
	void registeringACallbackWithTheSameNameAsTheAttributeShouldNotOverrideTheAttribute() {
		Runnable callback = mock();

		this.attributes.setAttribute("key", "value");
		this.attributes.registerDestructionCallback("key", callback);

		assertThat(this.attributes.getAttribute("key")).isEqualTo("value");
		assertThat(this.attributes.getAttribute(Attributes.DESTRUCTION_CALLBACK_NAME_PREFIX + "key")).isEqualTo(callback);
	}
}
