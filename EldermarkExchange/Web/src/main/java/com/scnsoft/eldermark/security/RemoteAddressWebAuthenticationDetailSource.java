package com.scnsoft.eldermark.security;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of {@link org.springframework.security.authentication.AuthenticationDetailsSource} which builds the details object from
 * an <tt>HttpServletRequest</tt> object, creating a {@code WebAuthenticationDetails}.
 *
 * @author Ben Alex
 */
public class RemoteAddressWebAuthenticationDetailSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    //~ Methods ========================================================================================================

    /**
     * @param context the {@code HttpServletRequest} object.
     * @return the {@code WebAuthenticationDetails} containing information about the current request
     */
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new RemoteAddressWebAuthenticationDetails(context);
    }
}

