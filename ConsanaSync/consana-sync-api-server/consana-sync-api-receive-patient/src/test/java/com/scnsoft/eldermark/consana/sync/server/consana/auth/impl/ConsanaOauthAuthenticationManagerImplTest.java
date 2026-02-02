package com.scnsoft.eldermark.consana.sync.server.consana.auth.impl;

import ca.uhn.fhir.rest.client.api.IBasicClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2.ConsanaOauth2TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaOauthAuthenticationManagerImplTest {

    @Mock
    private ConsanaOauth2TokenProvider consanaOauth2TokenProvider;

    @InjectMocks
    private ConsanaOauthAuthenticationManagerImpl consanaOauthAuthenticationManager;

    @Test
    void authenticate_whenCalled_WillSetOauthTokenHeader() {
        var token = "token";
        IBasicClient basicClient = Mockito.mock(IBasicClient.class);

        when(consanaOauth2TokenProvider.getActiveToken()).thenReturn(token);

        consanaOauthAuthenticationManager.authenticate(basicClient);

        verify(basicClient).registerInterceptor(argThat((iClientInterceptor ->
                ((BearerTokenAuthInterceptor) iClientInterceptor).getToken().equals(token)
        )));
    }
}