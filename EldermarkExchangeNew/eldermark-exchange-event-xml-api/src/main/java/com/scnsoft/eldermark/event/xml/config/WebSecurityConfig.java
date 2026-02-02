package com.scnsoft.eldermark.event.xml.config;

import com.scnsoft.eldermark.event.xml.security.EventsProviderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService eventsProviderDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public WebSecurityConfig(EventsProviderDetailsService eventsProviderDetailsService, PasswordEncoder passwordEncoder, AuthenticationEntryPoint authenticationEntryPoint) {
        this.eventsProviderDetailsService = eventsProviderDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(eventsProviderDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/events").authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
