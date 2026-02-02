package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.shared.web.security.AuthenticationFilter;
import com.scnsoft.eldermark.shared.web.security.TokenAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    TokenAuthenticationManager authenticationManager;


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
            antMatchers("/opentok/event-listener/session-monitoring").permitAll(). // this for listen opentok event 
                // allow swagger resources             
            antMatchers("/v2/api-docs/**").permitAll().
            antMatchers("/swagger-resources/**").permitAll().
            antMatchers("/swagger-ui.html").permitAll().
            antMatchers("/webjars/springfox-swagger-ui/**").permitAll().
            anyRequest().authenticated().
            and().
            addFilterBefore(new AuthenticationFilter(authenticationManager), AnonymousAuthenticationFilter.class);
        }
    
    @Configuration
    @Order(1)                                                        
    public static class opentokConfiguration extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.
                csrf().disable().
                httpBasic().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().  
                authorizeRequests().anyRequest().authenticated().and().
                antMatcher("/opentok/**").x509().subjectPrincipalRegex("CN=(.*tokbox.com)");
        }
    }

/*    @Bean
    public UnauthorizedEntryPoint getBasicAuthEntryPoint() {
        return new UnauthorizedEntryPoint();
    }*/

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }


}