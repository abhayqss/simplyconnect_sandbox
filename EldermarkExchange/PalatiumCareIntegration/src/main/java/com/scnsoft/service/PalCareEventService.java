package com.scnsoft.service;

import com.scnsoft.dto.incoming.EventType;
import com.scnsoft.dto.incoming.PalCareEventDto;
import com.scnsoft.eldermark.dao.palatiumcare.AlertDao;
import com.scnsoft.eldermark.dao.palatiumcare.EventDao;
import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;
import com.scnsoft.eldermark.entity.palatiumcare.AlertType;
import com.scnsoft.eldermark.entity.palatiumcare.PCEvent;
import com.scnsoft.eldermark.services.palatiumcare.alert.AlertNotificationService;
import com.scnsoft.mapper.event.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@Service
public class PalCareEventService {

    private EventDao eventDao;

    private AlertDao alertDao;

    private AlertNotificationService alertNotificationService;

    private EventMapper eventMapper = new EventMapper();

    @Autowired
    public void setAlertNotificationService(AlertNotificationService alertNotificationService) {
        this.alertNotificationService = alertNotificationService;
    }

    @Autowired
    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Autowired
    public void setAlertDao(AlertDao alertDao) {
        this.alertDao = alertDao;
    }


    private boolean isSystemAlert(String type) {
        return EventType.ALERT_SYSTEM.toString().equals(type);
    }

    private boolean isActiveAlert(String type) {
        return EventType.ALERT_ACTIVE.toString().equals(type);
    }

    @Transactional
    public void save(PalCareEventDto eventDto) {
        PCEvent event = eventMapper.dtoToEntity(eventDto);
        event = eventDao.save(event);
        String eventType = eventDto.getType();
        if(isSystemAlert(eventType) || isActiveAlert(eventType)) {
            Alert alert = new Alert();
            alert.setAlertType(isSystemAlert(eventType) ? AlertType.SYSTEM : AlertType.ACTIVE);
            alert.setEvent(event);
            alert.setStatus(AlertStatus.NOT_TAKEN_YET);
            alert = alertDao.save(alert);
            Long userId = 451L;
            alertNotificationService.sendAlertNotification(userId, alert);
        }


    }


}
