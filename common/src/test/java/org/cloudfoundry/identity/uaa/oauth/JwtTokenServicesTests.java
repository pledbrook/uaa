/**
 * Cloud Foundry 2012.02.03 Beta Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
 * 
 * This product is licensed to you under the Apache License, Version 2.0 (the "License"). You may not use this product
 * except in compliance with the License.
 * 
 * This product includes a number of subcomponents with separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 */
package org.cloudfoundry.identity.uaa.oauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.identity.uaa.authentication.UaaAuthentication;
import org.cloudfoundry.identity.uaa.authentication.UaaAuthenticationTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;

/**
 * @author Dave Syer
 * 
 */
public class JwtTokenServicesTests {

	private JwtTokenServices tokenServices;

	private UaaAuthentication userAuthentication;

	private Map<String, String> authData;

	@Before
	public void setUp() throws Exception {
		tokenServices = new JwtTokenServices();
		tokenServices.setTokenStore(new InMemoryTokenStore());
		authData = new HashMap<String, String>();
		userAuthentication =UaaAuthenticationTestFactory.getAuthentication("foo@bar.com", "Foo Bar",
				"foo@bar.com");

	}

	@Test
	public void testCreateAccessToken() {
		authData.put("token", "FOO");
		OAuth2Authentication authentication = new OAuth2Authentication(
				new AuthorizationRequest("foo", null, null, null), userAuthentication);
		OAuth2AccessToken token = tokenServices
				.createAccessToken(authentication, new ExpiringOAuth2RefreshToken("BAR", new Date()));
		assertNotNull(token.getValue());
		assertNotNull(token.getRefreshToken());
	}

	@Test
	public void testDuplicateTokensOnRefresh() {
		authData.put("token", "FOO");
		tokenServices.setSupportRefreshToken(true);
		OAuth2Authentication authentication1 = new OAuth2Authentication(new AuthorizationRequest("id",
				Collections.singleton("read"), null, null), userAuthentication);
		OAuth2AccessToken token1 = tokenServices.createAccessToken(authentication1);
		OAuth2AccessToken token2 = tokenServices.refreshAccessToken(token1.getRefreshToken().getValue(), null);
		assertFalse(token1.equals(token2));
	}

	@Test
	public void testDuplicateTokensWithDifferentScope() {
		authData.put("token", "FOO");
		OAuth2Authentication authentication1 = new OAuth2Authentication(
				new AuthorizationRequest("id", null, null, null), userAuthentication);
		OAuth2AccessToken token1 = tokenServices.createAccessToken(authentication1);
		OAuth2Authentication authentication2 = new OAuth2Authentication(new AuthorizationRequest("id",
				Collections.singleton("read"), null, null), userAuthentication);
		OAuth2AccessToken token2 = tokenServices.createAccessToken(authentication2);
		assertFalse(token1.equals(token2));
	}

}
