package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.AlertDto;
import com.scnsoft.eldermark.shared.palatiumcare.ResidentDto;
import com.scnsoft.eldermark.mapper.UserMobileDto;
import com.scnsoft.eldermark.schema.Event;

import java.util.Date;

public class AlertEventService {


    public void createEvent(AlertDto alertDto) {

        Event event = new Event();
        Date datetime = alertDto.getEventDateTime();
        ResidentDto resident = alertDto.getResident();
        UserMobileDto responder = alertDto.getResponder();


    }


}
