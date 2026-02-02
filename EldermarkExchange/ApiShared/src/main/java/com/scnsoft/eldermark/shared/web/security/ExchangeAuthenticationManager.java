package com.scnsoft.eldermark.shared.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by phomal on 7/27/2017.
 */
@Component
public class ExchangeAuthenticationManager extends ProviderManager {

    @Autowired
    public ExchangeAuthenticationManager(List<AuthenticationProvider> providers) {
        super(providers);
    }

}