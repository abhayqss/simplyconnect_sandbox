package com.scnsoft.eldermark.consana.sync.server.consana.auth.impl;

import ca.uhn.fhir.rest.client.api.IBasicClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2TokenProvider;
import com.scnsoft.eldermark.consana.sync.server.consana.auth.ConsanaAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsanaOauthAuthenticationManagerImpl implements ConsanaAuthenticationManager {

    private final ConsanaOauth2TokenProvider consanaOauth2TokenProvider;

    @Autowired
    public ConsanaOauthAuthenticationManagerImpl(ConsanaOauth2TokenProvider consanaOauth2TokenProvider) {
        this.consanaOauth2TokenProvider = consanaOauth2TokenProvider;
    }

    @Override
    public void authenticate(IBasicClient client) {
        client.registerInterceptor(new BearerTokenAuthInterceptor(consanaOauth2TokenProvider.getActiveToken()));
    }
}
