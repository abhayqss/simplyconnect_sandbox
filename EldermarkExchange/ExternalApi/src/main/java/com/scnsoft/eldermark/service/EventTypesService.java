package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.EventTypeDao;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeGroupDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 2/16/2018.
 */
@Service
@Transactional(readOnly = true)
public class EventTypesService {

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private DozerBeanMapper dozer;

    public List<EventTypeDto> getEventTypes() {
        List<EventType> eventTypes = eventTypeDao.list(null);
        return transform(eventTypes);
    }

    public List<EventTypeGroupDto> getEventGroups() {
        final List<EventGroup> eventGroups = eventTypeDao.getGroupList("priority");
        return transformGroups(eventGroups);
    }

    public EventType getById(Long eventTypeId) {
        return eventTypeDao.get(eventTypeId);
    }

    private List<EventTypeDto> transform(List<EventType> eventTypes) {
        List<EventTypeDto> dtos = new ArrayList<>();
        for (EventType eventType : eventTypes) {
            EventTypeDto dto = dozer.map(eventType, EventTypeDto.class);
            dto.setEnabled(null);
            dto.setEditable(null);
            dto.setGroupId(eventType.getEventGroup().getId());
            dto.setEventGroup(null);
            dtos.add(dto);
        }
        return dtos;
    }

    private List<EventTypeGroupDto> transformGroups(List<EventGroup> eventGroups) {
        List<EventTypeGroupDto> dtos = new ArrayList<>();
        for (EventGroup eventGroup : eventGroups) {
            EventTypeGroupDto dto = dozer.map(eventGroup, EventTypeGroupDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
