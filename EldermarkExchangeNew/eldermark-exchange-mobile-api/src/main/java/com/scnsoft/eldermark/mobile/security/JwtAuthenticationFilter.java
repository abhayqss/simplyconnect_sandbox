package com.scnsoft.eldermark.mobile.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.AuthAccountInactiveException;
import com.scnsoft.eldermark.exception.AuthException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.security.LoggedUserDetailsService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtTokenFacadeImpl jwtTokenFacade;

    @Autowired
    private LoggedUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = jwtTokenFacade.getJwtFromRequest(httpServletRequest);
            if (StringUtils.isNotBlank(jwt) && jwtTokenFacade.validateToken(jwt)) {
                var userAuthCtx = jwtTokenFacade.getAuthenticationContextFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadByUserAuthCtx(userAuthCtx);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                if (!hasMobileAppAccessPermission(authentication)) {
                    sendErrorResponse(httpServletResponse, new AuthException(InternalServerExceptionType.APP_ACCESS_DENIED));
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthAccountInactiveException ae) {
            //TODO the goal is to distinguish between unauthorized requests and requests from inactive users.
            logger.error("Access to application by inactive user", ae);
            sendErrorResponse(httpServletResponse, ae);
            return;
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean hasMobileAppAccessPermission(UsernamePasswordAuthenticationToken authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getLinkedEmployeesPermissions()
            .getOrDefault(Permission.MOBILE_APP_ACCESS, List.of())
            .contains(principal.getEmployeeId());
    }

    private void sendErrorResponse(HttpServletResponse httpServletResponse, AuthException ae) throws IOException {
        var errorResponse = Response.errorResponse(ae);
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.setStatus(ae.getHttpStatus().value());
        OutputStream out = httpServletResponse.getOutputStream();
        mapper.writeValue(out, errorResponse);
        out.flush();
    }
}
