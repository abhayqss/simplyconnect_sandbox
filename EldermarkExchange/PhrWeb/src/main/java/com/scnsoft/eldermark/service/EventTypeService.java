package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.EventTypeDao;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeGroupDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 6/7/2017.
 */
@Service
@Transactional(readOnly = true)
public class EventTypeService {

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    private DozerBeanMapper dozer;


    List<EventTypeDto> getSortedEventTypes(Collection<String> enabledEventTypes) {
        List<EventType> eventTypes = eventTypeDao.list("description");
        return transform(eventTypes, enabledEventTypes, null);
    }

    List<EventTypeDto> getSortedEventTypes(Collection<String> enabledEventTypes, List<EventTypeCareTeamRoleXref> defaultSettings) {
        List<EventType> eventTypes = eventTypeDao.list("description");
        return transform(eventTypes, enabledEventTypes, defaultSettings);
    }

    public List<EventTypeDto> getEventTypes() {
        List<EventType> eventTypes = eventTypeDao.list(null);
        return transform(eventTypes, null, null);
    }

    public List<EventTypeDto> getEventTypesForView() {
        List<EventType> eventTypes = eventTypeDao.listForView(null);
        return transform(eventTypes, null, null);
    }

    public List<EventTypeGroupDto> getEventGroups() {
        final List<EventGroup> eventGroups = eventTypeDao.getGroupList("priority");
        return transform(eventGroups);
    }

    public List<EventTypeGroupDto> getEventGroupsForView() {
        final List<EventGroup> eventGroups = eventTypeDao.getGroupListForView("priority");
        return transform(eventGroups);
    }

    public EventType getById(Long eventTypeId) {
        return eventTypeDao.get(eventTypeId);
    }

    private List<EventTypeDto> transform(List<EventType> eventTypes, Collection<String> enabledEventTypes, List<EventTypeCareTeamRoleXref> defaultSettings) {
        List<EventTypeDto> dtos = new ArrayList<>();
        for (EventType eventType : eventTypes) {
            EventTypeDto dto = dozer.map(eventType, EventTypeDto.class);
            if (enabledEventTypes != null) {
                dto.setEnabled(enabledEventTypes.contains(eventType.getCode()));
            }
            dto.setEditable(isChangeable(eventType.getId(), defaultSettings));
            if (enabledEventTypes == null && CollectionUtils.isEmpty(defaultSettings)) {
                dto.setGroupId(eventType.getEventGroup().getId());
                dto.setEventGroup(null);
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private List<EventTypeGroupDto> transform(List<EventGroup> eventGroups) {
        List<EventTypeGroupDto> dtos = new ArrayList<>();
        for (EventGroup eventGroup : eventGroups) {
            EventTypeGroupDto dto = dozer.map(eventGroup, EventTypeGroupDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    private static Boolean isChangeable(Long eventTypeId, List<EventTypeCareTeamRoleXref> defaultSettings) {
        if (CollectionUtils.isEmpty(defaultSettings)) {
            return null;
        }
        for (EventTypeCareTeamRoleXref defaultSetting : defaultSettings) {
            if (defaultSetting.getEventType().getId().equals(eventTypeId)) {
                return defaultSetting.getResponsibility().isChangeable();
            }
        }
        return true;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
