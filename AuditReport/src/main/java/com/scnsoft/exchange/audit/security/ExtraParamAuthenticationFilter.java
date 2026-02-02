package com.scnsoft.exchange.audit.security;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class ExtraParamAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String extraParameter = "extra";

    public static String delimiter = ":";

    @Override
    protected String obtainUsername(HttpServletRequest request)
    {
        String username = request.getParameter(getUsernameParameter());
        String extraParam = request.getParameter(getExtraParameter());

        return username + getDelimiter() + extraParam;
    }

    public String getExtraParameter()
    {
        return this.extraParameter;
    }

    public void setExtraParameter(String extraParameter)
    {
        this.extraParameter = extraParameter;
    }

    public String getDelimiter()
    {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }
}