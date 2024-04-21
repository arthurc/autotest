package io.github.arthurc.autotest.eventing;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectiveCloudEventTypeMapperTest {

	private static final CloudEvent CLOUD_EVENT = CloudEventBuilder.v1()
			.withId("id")
			.withSource(URI.create("urn:foo"))
			.withType(Foo.class.getName())
			.build();

	private final ReflectiveCloudEventTypeMapper<Foo> mapper = new ReflectiveCloudEventTypeMapper<>(getClass().getClassLoader());

	@Test
	void Should_map_domain_event_to_cloud_event_type() {
		assertThat(mapper.toCloudEventType(new Foo())).isEqualTo(Foo.class.getName());
	}

	@Test
	void Should_map_cloud_event_to_domain_event_type() {
		assertThat(mapper.toJavaType(CLOUD_EVENT)).isEqualTo(Foo.class);
	}

	@Test
	void Should_throw_IllegalArgumentException_when_cloud_event_type_not_found() {
		CloudEvent cloudEvent = CloudEventBuilder.from(CLOUD_EVENT)
				.withType("foo.bar.Baz")
				.build();

		assertThatThrownBy(() -> mapper.toJavaType(cloudEvent))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Failed to load class: foo.bar.Baz");
	}

	static class Foo {
	}

}
