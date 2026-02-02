package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.event.xml.dao.EventsLogDao;
import com.scnsoft.eldermark.event.xml.entity.EventsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventsLogServiceImpl implements EventsLogService {

    private final EventsLogDao eventsLogDao;

    @Autowired
    public EventsLogServiceImpl(EventsLogDao eventsLogDao) {
        this.eventsLogDao = eventsLogDao;
    }


    @Override
    public void saveLog(EventsLog log) {
        eventsLogDao.save(log);
    }
}
