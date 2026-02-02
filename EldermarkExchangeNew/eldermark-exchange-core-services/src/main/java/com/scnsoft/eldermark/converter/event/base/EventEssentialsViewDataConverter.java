package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.dto.event.EventEssentialsViewData;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.xds.message.EVNSegmentContainingMessage;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class EventEssentialsViewDataConverter<E extends EventEssentialsViewData> implements Converter<Event, E> {

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Override
    public E convert(Event event) {
        var essentials = create();
        fill(event, essentials);
        return essentials;
    }

    protected abstract E create();

    protected void fill(Event event, E essentials){
        var eventAuthor = event.getEventAuthor();

        essentials.setAuthor(CareCoordinationUtils.getFullName(eventAuthor.getFirstName(), eventAuthor.getLastName()));
        essentials.setAuthorRole(eventAuthor.getRole());
        essentials.setDate(DateTimeUtils.toEpochMilli(event.getEventDateTime()));
        essentials.setTypeTitle(event.getEventType().getDescription());
        essentials.setIsEmergencyDepartmentVisit(event.isErVisit());
        essentials.setIsOvernightInpatient(event.isOvernightIn());
        essentials.setDeviceId(event.getDeviceId());

        if (event.getAdtMsgId() != null) {
            var adt = adtMessageDao.findById(event.getAdtMsgId()).orElseThrow();
            if (adt instanceof EVNSegmentContainingMessage && ((EVNSegmentContainingMessage) adt).getEvn() != null) {
                var evn = ((EVNSegmentContainingMessage) adt).getEvn();

                essentials.setTypeCode(evn.getEventTypeCode()); //we can also resolve code from adt message instance type
                essentials.setRecordedDate(DateTimeUtils.toEpochMilli(evn.getRecordedDatetime()));
            }
        }
    }

}
