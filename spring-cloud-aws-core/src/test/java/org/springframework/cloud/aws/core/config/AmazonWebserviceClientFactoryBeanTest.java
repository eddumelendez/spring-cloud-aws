/*
 * Copyright 2013-2014 the original author or authors.
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

package org.springframework.cloud.aws.core.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.aws.core.region.StaticRegionProvider;

/**
 * @author Agim Emruli
 */
public class AmazonWebserviceClientFactoryBeanTest {

	@Test
	public void getObject_withCustomRegion_returnsClientWithCustomRegion() throws Exception {

		//Arrange
		AmazonWebserviceClientFactoryBean<AmazonTestWebserviceClient> factoryBean = new AmazonWebserviceClientFactoryBean<>(AmazonTestWebserviceClient.class,
				new StaticCredentialsProvider(new BasicAWSCredentials("aaa", "bbb")));
		factoryBean.setCustomRegion("eu-west-1");


		//Act
		factoryBean.afterPropertiesSet();
		AmazonTestWebserviceClient webserviceClient = factoryBean.getObject();

		//Assert
		Assert.assertEquals("eu-west-1", webserviceClient.getRegion().getName());

	}

	@Test
	public void getObject_withRegionProvider_returnsClientWithRegionReturnedByProvider() throws Exception {

		//Arrange
		AmazonWebserviceClientFactoryBean<AmazonTestWebserviceClient> factoryBean = new AmazonWebserviceClientFactoryBean<>(AmazonTestWebserviceClient.class,
				new StaticCredentialsProvider(new BasicAWSCredentials("aaa", "bbb")));
		factoryBean.setRegionProvider(new StaticRegionProvider("eu-west-1"));


		//Act
		factoryBean.afterPropertiesSet();
		AmazonTestWebserviceClient webserviceClient = factoryBean.getObject();

		//Assert
		Assert.assertEquals("eu-west-1", webserviceClient.getRegion().getName());

	}
}