/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.aws.autoconfigure.context;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageProtocolResolver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ContextResourceLoaderAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(ContextResourceLoaderAutoConfiguration.class));

	@Test
	public void createResourceLoader_withCustomTaskExecutorSettings_executorConfigured() {
		this.contextRunner.withPropertyValues("cloud.aws.loader.corePoolSize:10",
				"cloud.aws.loader.maxPoolSize:20", "cloud.aws.loader.queueCapacity:0")
				.run(context -> {
					SimpleStorageProtocolResolver simpleStorageProtocolResolver = (SimpleStorageProtocolResolver) context
							.getSourceApplicationContext(
									AnnotationConfigApplicationContext.class)
							.getProtocolResolvers().iterator().next();
					ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) ReflectionTestUtils
							.getField(simpleStorageProtocolResolver, "taskExecutor");
					assertThat(taskExecutor).isNotNull();

					assertThat(taskExecutor.getCorePoolSize()).isEqualTo(10);
					assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(20);
					assertThat(
							ReflectionTestUtils.getField(taskExecutor, "queueCapacity"))
									.isEqualTo(0);
				});
	}

	@Test
	public void createResourceLoaderWithCustomS3Client() {
		this.contextRunner.withUserConfiguration(TestConfig.class).run(context -> {
			AmazonS3Client s3Client = context.getBean(AmazonS3Client.class);
			assertThat(s3Client.getBucketLocation("spring-bucket"))
					.isEqualTo("s3://spring-bucket");

			SimpleStorageProtocolResolver simpleStorageProtocolResolver =
					(SimpleStorageProtocolResolver) context
							.getSourceApplicationContext(AnnotationConfigApplicationContext.class)
							.getProtocolResolvers().iterator().next();
			SyncTaskExecutor taskExecutor = (SyncTaskExecutor) ReflectionTestUtils
					.getField(simpleStorageProtocolResolver, "taskExecutor");
			assertThat(taskExecutor).isNotNull();
		});
	}

	@Test
	public void createResourceLoader_withoutExecutorSettings_executorConfigured() {
		this.contextRunner.run(context -> {
			SimpleStorageProtocolResolver simpleStorageProtocolResolver = (SimpleStorageProtocolResolver) context
					.getSourceApplicationContext(AnnotationConfigApplicationContext.class)
					.getProtocolResolvers().iterator().next();
			SyncTaskExecutor taskExecutor = (SyncTaskExecutor) ReflectionTestUtils
					.getField(simpleStorageProtocolResolver, "taskExecutor");
			assertThat(taskExecutor).isNotNull();
		});
	}

	@Configuration(proxyBeanMethods = false)
	static class TestConfig {

		@Bean
		public AmazonS3 amazonS3Client() {
			AmazonS3Client s3Client = mock(AmazonS3Client.class);
			given(s3Client.getBucketLocation("spring-bucket"))
					.willReturn("s3://spring-bucket");
			return s3Client;
		}

	}

}
