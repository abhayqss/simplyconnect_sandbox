package com.scnsoft.eldermark.web.interceptor.hashkey;

import com.scnsoft.eldermark.facades.ResidentFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HashKeyInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private ResidentFacade residentFacade;

    private InvalidHashKeyStrategy invalidHashKeyStrategy;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String residentId = (String) pathVariables.get("residentId");
        String providedHashKey = request.getParameter("hashKey");

        if (StringUtils.isBlank(residentId) || StringUtils.isBlank(providedHashKey) ||
            !residentFacade.assertHashKey(Long.valueOf(residentId), providedHashKey)) {

            invalidHashKeyStrategy.onInvalidHashKeyDetected(request, response);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("hashKey", request.getParameter("hashKey"));
        }
    }

    public void setInvalidHashKeyStrategy(InvalidHashKeyStrategy invalidHashKeyStrategy) {
        this.invalidHashKeyStrategy = invalidHashKeyStrategy;
    }
}
