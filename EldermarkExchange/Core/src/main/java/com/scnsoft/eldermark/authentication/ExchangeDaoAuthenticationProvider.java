package com.scnsoft.eldermark.authentication;

import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("authenticationProvider")
public class ExchangeDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    EmployeePasswordSecurityService employeePasswordSecurityService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        ExchangeUserDetails exchangeUserDetails = (ExchangeUserDetails)userDetails;
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
            employeePasswordSecurityService.checkAccountIsNotLockedOrThrow(exchangeUserDetails.getEmployee());
            employeePasswordSecurityService.processSuccessfulLogin(exchangeUserDetails.getEmployee());
        } catch (BadCredentialsException e) {
            employeePasswordSecurityService.processFailedLogin(exchangeUserDetails.getEmployee());
            employeePasswordSecurityService.checkAccountIsNotLockedOrThrow(exchangeUserDetails.getEmployee());
            throw e;
        }

    }
}
