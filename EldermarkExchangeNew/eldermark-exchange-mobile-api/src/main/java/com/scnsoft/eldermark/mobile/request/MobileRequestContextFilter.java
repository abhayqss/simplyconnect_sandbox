package com.scnsoft.eldermark.mobile.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MobileRequestContextFilter extends OncePerRequestFilter {

    @Autowired
    private MobileRequestContextProvider provider;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        var context = createContext(httpServletRequest);
        provider.setRequestContext(context);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private MobileRequestContext createContext(HttpServletRequest request) {
        var apiSubVersion = request.getHeader(MobileRequestContext.API_SUB_VERSION_HEADER);
        var platform = request.getHeader(MobileRequestContext.PLATFORM_HEADER);

        return new MobileRequestContext(apiSubVersion, platform);
    }
}
