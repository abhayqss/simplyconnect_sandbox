package com.scnsoft.eldermark.security;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Detects invalid sessions for AJAX requests and calls {@link InvalidSessionStrategy} to handle them.
 * Filter is expected to be called before the {@link org.springframework.security.web.session.SessionManagementFilter} because
 * {@link org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy} doesn't work for AJAX requests.
*/
public class AjaxInvalidSessionFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    private InvalidSessionStrategy invalidSessionStrategy = null;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if(!(request).getRequestURI().startsWith("/resources")){
            String ajaxHeader = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(ajaxHeader)) {
                if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("Requested session ID " + request.getRequestedSessionId() + " is invalid.");
                    }
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
                        // No security context or authentication present. Check for a session timeout

                        if (invalidSessionStrategy != null) {
                            invalidSessionStrategy.onInvalidSessionDetected(request, response);
                            return;
                        }
                    }
                }
            }


        }
        chain.doFilter(request, response);


    }

    /**
     * Sets the strategy which will be invoked instead of allowing the filter chain to proceed, if the user agent
     * requests an invalid session Id. If the property is not set, no action will be taken.
     *
     * @param invalidSessionStrategy the strategy to invoke. Typically a {@link SendErrorInvalidSessionStrategy}.
     */
    public void setInvalidSessionStrategy(InvalidSessionStrategy invalidSessionStrategy) {
        this.invalidSessionStrategy = invalidSessionStrategy;
    }
}

