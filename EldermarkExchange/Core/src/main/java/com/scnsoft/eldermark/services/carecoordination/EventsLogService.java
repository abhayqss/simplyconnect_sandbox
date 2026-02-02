package com.scnsoft.eldermark.services.carecoordination;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pzhurba on 25-Sep-15.
 */
@Transactional
public interface EventsLogService {
    void logIncomingMessage(HttpServletRequest request, String body);
}
