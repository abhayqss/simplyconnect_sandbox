package com.scnsoft.eldermark.web.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


public class GlobalPropertiesInterceptor extends HandlerInterceptorAdapter {
    public @Value("${auditReport.context}") String auditReportContext;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            ResourceBundle errorsResourceBundle = ResourceBundle.getBundle("i18n/EldermarkExchangeErrors", LocaleContextHolder.getLocale());
            Set<String> keySet = errorsResourceBundle.keySet();
            Map<String, String> errorMessages = new HashMap<String, String>();
            for (String key : keySet) {
                errorMessages.put(key, errorsResourceBundle.getString(key));
            }
            modelAndView.addObject("messages", errorMessages);

            modelAndView.addObject("auditReportContext", auditReportContext);
        }
    }
}
