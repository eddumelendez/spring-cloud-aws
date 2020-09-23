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

package org.springframework.cloud.aws.secretsmanager;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import org.junit.jupiter.api.Test;

import org.springframework.core.env.PropertySource;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AwsSecretsManagerPropertySourceLocator}.
 *
 * @author Anthony Foulfoin
 * @author Matej Nedic
 * @author Eddú Meléndez
 */
class AwsSecretsManagerPropertySourceLocatorTest {

	private AWSSecretsManager smClient = mock(AWSSecretsManager.class);

	private MockEnvironment env = new MockEnvironment();

	@Test
	void locate_nameNotSpecifiedInConstructor_returnsPropertySourceWithDefaultName() {
		GetSecretValueResult secretValueResult = new GetSecretValueResult();
		secretValueResult.setSecretString("{\"key1\": \"value1\", \"key2\": \"value2\"}");
		when(smClient.getSecretValue(any(GetSecretValueRequest.class)))
				.thenReturn(secretValueResult);

		AwsSecretsManagerPropertySourceLocator locator = new AwsSecretsManagerPropertySourceLocator(
				smClient);
		PropertySource propertySource = locator.locate(env);

		assertThat(propertySource.getName()).isEqualTo("aws-secrets-manager");
	}

	@Test
	public void contextExpectedToHave2Elements() {
		GetSecretValueResult secretValueResult = new GetSecretValueResult();
		secretValueResult.setSecretString("{\"key1\": \"value1\", \"key2\": \"value2\"}");
		when(smClient.getSecretValue(any(GetSecretValueRequest.class)))
				.thenReturn(secretValueResult);

		AwsSecretsManagerPropertySourceLocator locator = new AwsSecretsManagerPropertySourceLocator(
				smClient);
		locator.setDefaultContext("application");
		locator.setName("application");
		env.setActiveProfiles("test");
		locator.locate(env);

		assertThat(locator.getContexts()).hasSize(2);
	}

	@Test
	public void contextExpectedToHave4Elements() {
		GetSecretValueResult secretValueResult = new GetSecretValueResult();
		secretValueResult.setSecretString("{\"key1\": \"value1\", \"key2\": \"value2\"}");
		when(smClient.getSecretValue(any(GetSecretValueRequest.class)))
				.thenReturn(secretValueResult);

		AwsSecretsManagerPropertySourceLocator locator = new AwsSecretsManagerPropertySourceLocator(
				smClient);
		locator.setDefaultContext("application");
		locator.setName("messaging-service");
		env.setActiveProfiles("test");
		locator.locate(env);

		assertThat(locator.getContexts()).hasSize(4);
	}

	@Test
	public void contextSpecificOrderExpected() {
		GetSecretValueResult secretValueResult = new GetSecretValueResult();
		secretValueResult.setSecretString("{\"key1\": \"value1\", \"key2\": \"value2\"}");
		when(smClient.getSecretValue(any(GetSecretValueRequest.class)))
				.thenReturn(secretValueResult);

		AwsSecretsManagerPropertySourceLocator locator = new AwsSecretsManagerPropertySourceLocator(
				smClient);
		locator.setDefaultContext("application");
		locator.setName("messaging-service");
		env.setActiveProfiles("test");
		locator.locate(env);

		List<String> contextToBeTested = new ArrayList<>(locator.getContexts());

		assertThat(contextToBeTested.get(0)).isEqualTo("/secret/messaging-service_test");
		assertThat(contextToBeTested.get(1)).isEqualTo("/secret/messaging-service");
		assertThat(contextToBeTested.get(2)).isEqualTo("/secret/application_test");
		assertThat(contextToBeTested.get(3)).isEqualTo("/secret/application");

	}

}
