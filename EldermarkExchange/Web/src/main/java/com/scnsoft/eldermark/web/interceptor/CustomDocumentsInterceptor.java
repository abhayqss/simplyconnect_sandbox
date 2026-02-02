package com.scnsoft.eldermark.web.interceptor;

import com.scnsoft.eldermark.facades.DocumentFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CustomDocumentsInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private DocumentFacade documentFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String residentId = (String) pathVariables.get("residentId");
        String documentId = (String) pathVariables.get("documentId");

        if (StringUtils.isBlank(documentId)) {
            return true; // ignore all urls except those who have documentId
        }

        Long resId = Long.valueOf(residentId);
        Long docId = Long.valueOf(documentId);

        boolean isOwning = documentFacade.isAttachedToResident(resId, docId) || documentFacade.isAttachedToMergedResidents(resId, docId);
        if (!isOwning) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.getWriter().write("HTTP Status 403 - Access to the specified resource has been forbidden.");
        }

        return isOwning;
    }
}
