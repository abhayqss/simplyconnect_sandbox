package com.scnsoft.eldermark.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuditLogUtils {

    public static String getRemoteAddress() {
        var requestAttrs = RequestContextHolder.getRequestAttributes();
        if (requestAttrs instanceof ServletRequestAttributes) {
            var servletRequestAttrs = (ServletRequestAttributes) requestAttrs;
            return servletRequestAttrs.getRequest().getRemoteAddr();
        }
        return null;
    }
}
