package com.scnsoft.eldermark.mobile.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@AutoConfigureAfter(WebSecurityConfig.class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

}
