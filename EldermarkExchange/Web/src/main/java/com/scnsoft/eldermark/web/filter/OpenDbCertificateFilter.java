package com.scnsoft.eldermark.web.filter;

import com.scnsoft.eldermark.dao.DbUtilsDao;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//NOT USED
public class OpenDbCertificateFilter implements Filter {

    ApplicationContext context;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if(((HttpServletRequest) request).getRequestURI().matches(".*(css|jpg|png|gif|js)$")){
            filterChain.doFilter(request, response);
        } else {
            DbUtilsDao dbUtilsDao = context.getBean(DbUtilsDao.class);
            dbUtilsDao.openCertificate();
            filterChain.doFilter(request, response);
            dbUtilsDao.closeCertificate();
        }
    }

    @Override
    public void destroy() {

    }
}
