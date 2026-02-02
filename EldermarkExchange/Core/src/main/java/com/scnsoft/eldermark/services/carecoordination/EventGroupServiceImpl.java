package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.EventTypeDao;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pzhurba on 05-Oct-15.
 */
@Service
public class EventGroupServiceImpl implements EventGroupService {
    @Autowired
    EventTypeDao eventTypeDao;

    private Map<Long, EventGroup> eventGroups = null;
//    private Map<String, EventGroup> eventTypesByCode = null;
    private List<KeyValueDto> eventGroupsList = null;


    @Override
    public List<KeyValueDto> getAllEventGroups() {
        fillEventGroups();
        return eventGroupsList;
    }

//    public EventType getByCode(String code) {
//        fillEventGroups();
//        return eventTypesByCode.get(code);
//    }

    public EventGroup get(Long id) {
        fillEventGroups();
        return eventGroups.get(id);
    }

    private void fillEventGroups() {
        if ((eventGroups ==null) || (eventGroupsList ==null)) {
            eventGroups = new HashMap<Long, EventGroup>();
//            eventTypesByCode = new HashMap<String, EventGroup>();
            eventGroupsList = new ArrayList<KeyValueDto>();
            for (EventGroup eventGroup : eventTypeDao.getGroupList("priority")) {
                eventGroups.put(eventGroup.getId(), eventGroup);
//                eventTypesByCode.put(eventGroup.getCode(), eventGroup);
                eventGroupsList.add(new KeyValueDto(eventGroup.getId(), eventGroup.getName()));
            }
        }

    }
}
