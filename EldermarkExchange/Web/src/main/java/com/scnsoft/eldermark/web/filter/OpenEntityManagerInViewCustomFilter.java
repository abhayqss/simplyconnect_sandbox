package com.scnsoft.eldermark.web.filter;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class OpenEntityManagerInViewCustomFilter extends OpenEntityManagerInViewFilter implements Filter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/resources")) {
            filterChain.doFilter(request, response);
        } else {
            super.doFilterInternal(request, response, filterChain);
        }
    }

}
