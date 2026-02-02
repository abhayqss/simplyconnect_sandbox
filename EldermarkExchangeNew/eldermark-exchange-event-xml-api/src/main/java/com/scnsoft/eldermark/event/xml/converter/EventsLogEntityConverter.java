package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.event.xml.entity.EventsLog;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class EventsLogEntityConverter implements Converter<HttpServletRequest, EventsLog> {

    @Override
    public EventsLog convert(HttpServletRequest source) {
        var target = new EventsLog();
        var userAgent = source.getHeader("User-Agent");
        target.setRemoteAddress(source.getRemoteAddr());
        target.setUserAgent(userAgent == null ? "unknown" : userAgent);
        return target;
    }
}
