package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.authentication.UsernameBuilder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class ExtraParamAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String extraParameter = "extra";

    @Override
    protected String obtainUsername(HttpServletRequest request)
    {
        String username = request.getParameter(getUsernameParameter());
        String extraParam = request.getParameter(getExtraParameter());
        String linkLogin = request.getParameter("linkExisting");
        String token = request.getParameter("token");

        return UsernameBuilder.anUsername()
                .withCompanyId(extraParam)
                .withLogin(username)
                .withLinkLogin(linkLogin)
                .withToken(token)
                .build();
    }

    public String getExtraParameter()
    {
        return this.extraParameter;
    }

    public void setExtraParameter(String extraParameter)
    {
        this.extraParameter = extraParameter;
    }

    public static String getDelimiter()
    {
        return UsernameBuilder.getDelimiter();
    }

}