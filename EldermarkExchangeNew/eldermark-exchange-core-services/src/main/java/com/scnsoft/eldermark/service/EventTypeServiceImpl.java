package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EventTypeDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.event.EventType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventTypeServiceImpl implements EventTypeService {

    @Autowired
    private EventTypeDao eventTypeDao;

    public static final Set<String> ALLOWED_PG_PRS_CLIENT_EVENT_CODES = Set.of("COVID19","DS","MEDS","NEWAP","UPDAP","CANAP","COMAP");

    private Map<Long, EventType> eventTypes;
    private Map<String, EventType> eventTypesByCode;
    private Map<CareTeamRoleCode, Set<String>> notViewableEventTypeCodesByUserRole;

    @PostConstruct
    private void postConstruct() {
        var types = eventTypeDao.findAll();
        eventTypes = types.stream().collect(Collectors.toMap(EventType::getId, Function.identity()));
        eventTypesByCode = types.stream().collect(Collectors.toMap(EventType::getCode, Function.identity()));
        notViewableEventTypeCodesByUserRole = Arrays.stream(CareTeamRoleCode.values()).collect(Collectors.toMap(Function.identity(), careTeamRoleCode -> {
            if (careTeamRoleCode == CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES || careTeamRoleCode == CareTeamRoleCode.ROLE_PARENT_GUARDIAN) {
                return types.stream().map(EventType::getCode).filter(code -> !ALLOWED_PG_PRS_CLIENT_EVENT_CODES.contains(code)).collect(Collectors.toSet());
            } else {
                return Collections.emptySet();
            }
        }));

    }

    @Override
    public EventType findById(Long id) {
        return eventTypes.get(id);
    }

    @Override
    public EventType findByCode(String code) {
        return eventTypesByCode.get(code);
    }

    @Override
    public Set<String> findDisabledCodesByRoles(List<CareTeamRoleCode> careTeamRoleCodes) {
        if (CollectionUtils.isEmpty(careTeamRoleCodes)) {
            return Collections.emptySet();
        }
        var notViewableCodes = notViewableEventTypeCodesByUserRole.get(careTeamRoleCodes.get(0));
        if (careTeamRoleCodes.size() > 1) {
            careTeamRoleCodes.forEach(careTeamRoleCode -> notViewableCodes.retainAll(notViewableEventTypeCodesByUserRole.get(careTeamRoleCode)));
        }
        return notViewableCodes;
    }

    @Override
    public List<Long> findDisabledIdsByRoles(List<CareTeamRoleCode> careTeamRoleCodes) {
        return findDisabledCodesByRoles(careTeamRoleCodes).stream().map(code -> eventTypesByCode.get(code).getId()).collect(Collectors.toList());
    }
}
