package com.scnsoft.eldermark.config;

import com.scnsoft.eldermark.entity.security.AuthScope;
import com.scnsoft.eldermark.exception.AccessDeniedExceptionHandler;
import com.scnsoft.eldermark.filter.TwilioWebhookFilter;
import com.scnsoft.eldermark.security.JwtAuthenticationEntryPoint;
import com.scnsoft.eldermark.security.JwtAuthenticationFilter;
import com.scnsoft.eldermark.service.security.LoggedUserDetailsService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.filter.ApiRequestLoggingFilter;
import com.scnsoft.eldermark.web.commons.service.ApiRequestLoggingNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoggedUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ApiRequestLoggingNotificationService apiRequestLoggingNotificationService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedExceptionHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers("/auth/user").authenticated()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/directory/**").permitAll()
                .antMatchers("/twilio/webhook/**").permitAll()
                .antMatchers("/pcc/webhooks").permitAll()
                .antMatchers("/pdcflow/postback/**").permitAll()
                .antMatchers("/care-team-invitations/email/create-account").permitAll()
                .anyRequest()
                .hasAuthority(AuthScope.FULL.toString());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
                new ApiRequestLoggingFilter(apiRequestLoggingNotificationService, loggedUserService),
                AbstractPreAuthenticatedProcessingFilter.class
        );
    }

    @Bean
    @Profile("!local") //allow devs to emulate callbacks during development
    public FilterRegistrationBean<TwilioWebhookFilter> twilioWebhookFilterRegistrationBean(
            @Value("${twilio.auth.token}") String authToken) {
        var registrationBean = new FilterRegistrationBean<TwilioWebhookFilter>();
        registrationBean.setFilter(new TwilioWebhookFilter(authToken));
        registrationBean.addUrlPatterns("/twilio/webhook/**");
        return registrationBean;
    }

    //swagger specific config is disabled. Uncomment and analyze config if swagger is needed
//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring()
//                .antMatchers("/v2/api-docs",
//                        "/configuration/ui",
//                        "/swagger-resources/**",
//                        "/configuration/security",
//                        "/swagger-ui.html",
//                        "/webjars/**")
//                .antMatchers("/api/auth")
//                .antMatchers("/api/registration")
//                .antMatchers("/api/admin/admins");
//    }

}
