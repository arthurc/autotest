package io.github.arthurc.autotest.junit.jupiter;

import io.github.arthurc.autotest.AutotestTest;
import io.github.arthurc.autotest.testplan.TestPlanModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class TestPlanModelMapperTest {

	@Nested
	class Mapping_a_test_plan {

		@Test
		void Maps_root_test_identifier() {
			TestPlanModel testPlanModel = new TestPlanModelMapper(LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(TestPlanModelMapperTestAutotestExample.class))
					.build()))
					.toTestPlanModel();

			assertThat(testPlanModel.tests()).first().satisfies(testModel -> {
				assertThat(testModel.id().value()).isEqualTo("[engine:junit-jupiter]/[class:io.github.arthurc.autotest.junit.jupiter.TestPlanModelMapperTest$TestPlanModelMapperTestAutotestExample]");
				assertThat(testModel.parentId()).isNull();
				assertThat(testModel.displayName()).isEqualTo("TestPlanModelMapperTest$TestPlanModelMapperTestAutotestExample");
				assertThat(testModel.children()).hasSize(1);
			});
		}

		@Test
		void Maps_child_test_identifier() {
			TestPlanModel testPlanModel = new TestPlanModelMapper(LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(TestPlanModelMapperTestAutotestExample.class))
					.build()))
					.toTestPlanModel();

			assertThat(testPlanModel.tests().getFirst().children()).first().satisfies(testModel -> {
				assertThat(testModel.id().value()).isEqualTo("[engine:junit-jupiter]/[class:io.github.arthurc.autotest.junit.jupiter.TestPlanModelMapperTest$TestPlanModelMapperTestAutotestExample]/[method:test()]");
				assertThat(testModel.parentId().value()).isEqualTo("[engine:junit-jupiter]/[class:io.github.arthurc.autotest.junit.jupiter.TestPlanModelMapperTest$TestPlanModelMapperTestAutotestExample]");
				assertThat(testModel.displayName()).isEqualTo("test");
				assertThat(testModel.children()).isEmpty();
			});
		}

		@Test
		void Only_includes_test_identifiers_with_the_autotest_tag() {
			TestPlanModel testPlanModel = new TestPlanModelMapper(LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(TestPlanModelMapperTestNoAutotestExample.class))
					.build()))
					.toTestPlanModel();

			assertThat(testPlanModel.tests()).isEmpty();
		}

		@Test
		void Can_handle_nested_autotest_classes() {
			TestPlanModel testPlanModel = new TestPlanModelMapper(LauncherFactory.create().discover(LauncherDiscoveryRequestBuilder.request()
					.selectors(selectClass(TestPlanModelMapperTestNestedAutotestExample.class))
					.build()))
					.toTestPlanModel();

			assertThat(testPlanModel.tests()).hasSize(1);
		}
	}

	@AutotestTest
	static class TestPlanModelMapperTestAutotestExample {

		@Test
		void test() {
		}
	}

	static class TestPlanModelMapperTestNestedAutotestExample {

		@Test
		void test() {
		}

		@AutotestTest
		@Nested
		class NestedAutotestTest {

			@Test
			void test() {
			}
		}
	}


	static class TestPlanModelMapperTestNoAutotestExample {

		@Test
		void test() {
		}

		@Nested
		class NestedTest {

			@Test
			void test() {
			}
		}
	}


	@TestConfiguration
	@SpringBootConfiguration
	static class TestApplication {
	}

}
