package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.dao.phr.ActivityDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.phr.EventActivity;
import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 5/17/2017
 */
@Service
@Transactional
public class EventActivityService {

    @Autowired
    ActivityDao activityDao;

    @Autowired
    private UserResidentRecordsDao userResidentRecordsDao;

    public List<EventActivity> logEventActivity(EventNotification eventNotification) {
        List<EventActivity> activities = new ArrayList<EventActivity>();
        Event event = eventNotification.getEvent();
        Long residentId = event.getResident().getId();

        List<Long> userIds = userResidentRecordsDao.getAllUserIdsByResidentId(residentId);
        for (Long userId : userIds) {
            EventActivity activity = new EventActivity();
            activity.setEmployee(eventNotification.getEmployee());
            activity.setPatientId(userId);
            activity.setDate(new Date());
            activity.setEventId(event.getId());
            activity.setEventType(event.getEventType());
            activity.setEventTypeId(event.getEventType().getId());
            activity.setResponsibility(eventNotification.getResponsibility());
            activity = activityDao.save(activity);
            activities.add(activity);
        }

        return activities;
    }

}
