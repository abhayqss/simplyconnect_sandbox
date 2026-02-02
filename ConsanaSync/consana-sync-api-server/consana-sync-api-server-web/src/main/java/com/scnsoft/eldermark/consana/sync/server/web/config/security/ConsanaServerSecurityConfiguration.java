package com.scnsoft.eldermark.consana.sync.server.web.config.security;

import com.scnsoft.eldermark.consana.sync.server.web.config.security.basic.ConsanaBasicAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:auth-${spring.profiles.active}.properties")
public class ConsanaServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value( "${basic.auth.user}" )
    private String basicAuthUser;

    @Value( "${basic.auth.password}" )
    private String basicAuthPassword;

    @Autowired
    private ConsanaBasicAuthEntryPoint consanaBasicAuthEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(basicAuthUser).password(passwordEncoder().encode(basicAuthPassword))
                .authorities("ROLE_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/submit").authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(consanaBasicAuthEntryPoint);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
