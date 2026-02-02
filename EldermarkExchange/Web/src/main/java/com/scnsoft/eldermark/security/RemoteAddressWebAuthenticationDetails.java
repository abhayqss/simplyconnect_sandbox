package com.scnsoft.eldermark.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class RemoteAddressWebAuthenticationDetails extends WebAuthenticationDetails {
    private final String xForwardedFor;

    /**
     * Records the remote address and will also set the session Id if a session
     * already exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public RemoteAddressWebAuthenticationDetails(HttpServletRequest request) {
        super(request);

        this.xForwardedFor = request.getHeader("X-FORWARDED-FOR");
    }

    /**
     * Indicates originating IP address of a client including the case when it connects to a web server through an HTTP proxy or load balancer.
     *
     * @return the address
     */
    @Override
    public String getRemoteAddress() {
        if (xForwardedFor == null)
            return super.getRemoteAddress();
        else
            return xForwardedFor;
    }
}
