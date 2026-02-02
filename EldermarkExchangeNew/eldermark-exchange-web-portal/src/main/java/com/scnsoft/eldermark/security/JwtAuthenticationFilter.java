package com.scnsoft.eldermark.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.exception.AuthAccountInactiveException;
import com.scnsoft.eldermark.service.security.LoggedUserDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.OnCommittedResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenFacade jwtTokenFacade;

    @Autowired
    private LoggedUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = jwtTokenFacade.getJwtFromRequest(httpServletRequest);
            if (StringUtils.isNotBlank(jwt) && jwtTokenFacade.validateToken(jwt)) {
                var userAuthCtx = jwtTokenFacade.getAuthenticationContextFromJWT(jwt);
                var userDetails = userDetailsService.loadByUserAuthCtx(userAuthCtx);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthAccountInactiveException ae) {
            //TODO the goal is to distinguish between unauthorized requests and requests from inactive users.
            logger.error("Access to application by inactive user", ae);

            //in case contact became inactive in the middle of session
            jwtTokenFacade.removeTokenFromCookie(httpServletRequest, httpServletResponse);

            var errorResponse = Response.errorResponse(ae);
            httpServletResponse.setHeader("Content-Type", "application/json");
            httpServletResponse.setStatus(ae.getHttpStatus().value());
            ObjectMapper mapper = new ObjectMapper();
            OutputStream out = httpServletResponse.getOutputStream();
            mapper.writeValue(out, errorResponse);
            out.flush();
            return;
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(httpServletRequest, new AuthCookieWriterResponse(httpServletRequest, httpServletResponse));
    }

    class AuthCookieWriterResponse extends OnCommittedResponseWrapper {
        private final HttpServletRequest request;
        private boolean headerAndPayloadCookieUpdated;
        private boolean signatureCookieUpdated;

        AuthCookieWriterResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
        }

        @Override
        protected void onResponseCommitted() {
            if (notLoginAndNotLogout()) {
                //update token after request processing so that clients found in record search are in new token
                jwtTokenFacade.updateTokenInCookie(request, this.getHttpResponse());
            }
            this.disableOnResponseCommitted();
        }

        @Override
        public void addCookie(Cookie cookie) {
            super.addCookie(cookie);
            trackAuthCookiesUpdate(cookie);
        }

        private void trackAuthCookiesUpdate(Cookie cookie) {
            headerAndPayloadCookieUpdated |= JwtTokenFacade.JWT_HEADER_AND_PAYLOAD_COOKIE.equals(cookie.getName());
            signatureCookieUpdated |= JwtTokenFacade.JWT_SIGNATURE_COOKIE.equals(cookie.getName());
        }

        private boolean notLoginAndNotLogout() {
            return !headerAndPayloadCookieUpdated && !signatureCookieUpdated;
        }

        private HttpServletResponse getHttpResponse() {
            return (HttpServletResponse) this.getResponse();
        }
    }
}
