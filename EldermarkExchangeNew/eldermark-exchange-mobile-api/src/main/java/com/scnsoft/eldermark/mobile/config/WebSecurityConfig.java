package com.scnsoft.eldermark.mobile.config;


import com.scnsoft.eldermark.entity.security.AuthScope;
import com.scnsoft.eldermark.mobile.exception.AccessDeniedExceptionHandler;
import com.scnsoft.eldermark.mobile.request.MobileRequestContextFilter;
import com.scnsoft.eldermark.mobile.security.JwtAuthenticationEntryPoint;
import com.scnsoft.eldermark.mobile.security.JwtAuthenticationFilter;
import com.scnsoft.eldermark.service.security.LoggedUserDetailsService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.filter.ApiRequestLoggingFilter;
import com.scnsoft.eldermark.web.commons.service.ApiRequestLoggingNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//todo configure
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoggedUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApiRequestLoggingNotificationService apiRequestLoggingNotificationService;

    @Value("${swagger.enabled}")
    private boolean enableSwagger;

    @Autowired
    private LoggedUserService loggedUserService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public MobileRequestContextFilter mobileRequestContextFilter() {
        return new MobileRequestContextFilter();
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
                //swagger-specific
                .antMatchers(HttpMethod.GET, "/v2/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui.html**",
                        "/webjars/**",
                        "favicon.ico"
                ).permitAll()
//                .antMatchers("/",
//                        "/favicon.ico",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js")
//
//                .permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/conversations/video/decline-by-room-token").permitAll()
                .antMatchers("/care-team-invitations/confirm-registration").permitAll()
                .antMatchers("/conversations/**").hasAnyAuthority(AuthScope.FULL.toString(), AuthScope.CONVERSATIONS.toString())
                .antMatchers("/avatars/**").hasAnyAuthority(AuthScope.FULL.toString(), AuthScope.CONVERSATIONS.toString());
//                .antMatchers("/directory/**").permitAll()
//                .antMatchers("/twilio/webhook/**").permitAll()


        if (enableSwagger) {
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET,
                            "/v2/api-docs",
                            "/swagger-resources/**",
                            "/swagger-ui/**",
                            "/swagger-ui",
                            "/webjars/**",
                            "favicon.ico"
                    ).permitAll();
        }

        http.authorizeRequests()
                .anyRequest()
                .hasAuthority(AuthScope.FULL.toString());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(mobileRequestContextFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(
                new ApiRequestLoggingFilter(apiRequestLoggingNotificationService, loggedUserService),
                MobileRequestContextFilter.class
        );
    }

    //swagger specific config is disabled. Uncomment and analyze config if swagger is needed
    @Override
    public void configure(WebSecurity web) {
        // web.ignoring().mvcMatchers("/swagger-ui.html/**", "/configuration/**",
        // "/swagger-resources/**", "/v2/api-docs");
        web.ignoring()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui",
                        "/webjars/**");
    }

}
