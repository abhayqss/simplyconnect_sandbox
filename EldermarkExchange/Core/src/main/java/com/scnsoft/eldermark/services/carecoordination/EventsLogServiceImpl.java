package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.EventsLogDao;
import com.scnsoft.eldermark.entity.EventsLogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pzhurba on 13-Nov-15.
 */
@Service
public class EventsLogServiceImpl implements EventsLogService {
    private static final Logger logger = LoggerFactory.getLogger(EventsLogServiceImpl.class);

    @Autowired
    private EventsLogDao eventsLogDao;


    @Override
    @Async
    public void logIncomingMessage(HttpServletRequest request, String body) {
        logger.info("Log incoming message, thread : " + Thread.currentThread().getId());
        try {
            final EventsLogEntity entity = new EventsLogEntity();
            final String userAgent = request.getHeader("User-Agent");
            entity.setMessage(body);
            entity.setRemoteAddress(request.getRemoteAddr());
            entity.setUserAgent(userAgent == null ? "unknown" : userAgent);

            eventsLogDao.create(entity);
            eventsLogDao.flush();
        } catch (Exception e) {
            logger.error("Error saving incoming XML ", e);
        }
    }
}
