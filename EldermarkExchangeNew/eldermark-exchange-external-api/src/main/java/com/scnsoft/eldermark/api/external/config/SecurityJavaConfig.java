package com.scnsoft.eldermark.api.external.config;

import com.scnsoft.eldermark.api.shared.web.security.AuthenticationFilter;
import com.scnsoft.eldermark.api.shared.web.security.TokenAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenAuthenticationManager authenticationManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                httpBasic().disable().
                formLogin().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().
                antMatchers("/register/**").permitAll().
                antMatchers("/auth/**").permitAll().
                antMatchers("/info/**").permitAll().
                // allow swagger resources
                        antMatchers("/v2/api-docs/**").permitAll().
                antMatchers("/swagger-resources/**").permitAll().
                antMatchers("/swagger-ui.html").permitAll().
                antMatchers("/webjars/springfox-swagger-ui/**").permitAll().
                anyRequest().authenticated().
                and().
                addFilterBefore(new AuthenticationFilter(authenticationManager), AnonymousAuthenticationFilter.class);
    }

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

}