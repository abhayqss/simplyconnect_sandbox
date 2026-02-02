package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.shared.web.dto.EventTypeDto;
import com.scnsoft.eldermark.api.shared.web.dto.EventTypeGroupDto;
import com.scnsoft.eldermark.dao.EventGroupDao;
import com.scnsoft.eldermark.dao.EventTypeDao;
import com.scnsoft.eldermark.entity.event.EventGroup;
import com.scnsoft.eldermark.entity.event.EventGroup_;
import com.scnsoft.eldermark.entity.event.EventType;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventTypesServiceImpl implements EventTypesService {

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private EventGroupDao eventGroupDao;

    @Autowired
    private DozerBeanMapper dozer;

    @Override
    public List<EventTypeDto> getEventTypes() {
        List<EventType> eventTypes = eventTypeDao.findAll();
        return transform(eventTypes);
    }

    @Override
    public List<EventTypeGroupDto> getEventGroups() {
        final List<EventGroup> eventGroups = eventGroupDao.findAll(Sort.by(Sort.Order.asc(EventGroup_.PRIORITY)));
        return transformGroups(eventGroups);
    }

    @Override
    public EventType findById(Long eventTypeId) {
        return eventTypeDao.findById(eventTypeId).orElse(null);
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
}
