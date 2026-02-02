package com.scnsoft.eldermark.shared.config.exchange;

import com.scnsoft.eldermark.authentication.ExchangeDaoAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * @author phomal
 * Created on 7/26/2017.
 */
@Configuration
public class SecurityConfig {

    /*
    <beans:bean id="authenticationProvider"
                class="com.scnsoft.eldermark.authentication.ExchangeDaoAuthenticationProvider">
        <beans:property name="passwordEncoder">
            <beans:bean class="org.springframework.security.crypto.password.StandardPasswordEncoder"/>
        </beans:property>
        <beans:property name="userDetailsService" ref="exchangeUserDetailsService"/>
    </beans:bean>
    <beans:bean id="exchangeUserDetailsService" class="com.scnsoft.eldermark.service.security.SimpleExchangeUserDetailsService">
    </beans:bean>
    */

    private final UserDetailsService exchangeUserDetailsService;

    // expected to autowire a bean of SimpleExchangeUserDetailsService type
    @Autowired
    public SecurityConfig(UserDetailsService exchangeUserDetailsService) {
        this.exchangeUserDetailsService = exchangeUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new ExchangeDaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(exchangeUserDetailsService);
        return authenticationProvider;
    }

}