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

package org.springframework.cloud.aws.autoconfigure.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link AwsParameterStoreBootstrapConfiguration}.
 *
 * @author Matej Nedic
 * @author Eddú Meléndez
 */
class AwsParameterStoreBootstrapConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(AwsParameterStoreBootstrapConfiguration.class));

	@Test
	void testWithDefaultRegion() {
		this.contextRunner.run(context -> {
			AWSSimpleSystemsManagementClient client = context
					.getBean(AWSSimpleSystemsManagementClient.class);
			Object region = ReflectionTestUtils.getField(client, "signingRegion");
			assertThat(region).isEqualTo(Regions.DEFAULT_REGION.getName());
		});
	}

	@Test
	void testWithStaticRegion() {
		this.contextRunner
				.withPropertyValues("spring.cloud.aws.parameterstore.region:us-east-1")
				.run(context -> {
					AWSSimpleSystemsManagementClient client = context
							.getBean(AWSSimpleSystemsManagementClient.class);
					Object region = ReflectionTestUtils.getField(client, "signingRegion");
					assertThat(region).isEqualTo("us-east-1");
				});
	}

	@Test
	void testUserAgent() {
		this.contextRunner.run(context -> {
			AWSSimpleSystemsManagementClient client = context
					.getBean(AWSSimpleSystemsManagementClient.class);
			assertThat(client.getClientConfiguration().getUserAgentSuffix())
					.startsWith("spring-cloud-aws/");
		});
	}

}