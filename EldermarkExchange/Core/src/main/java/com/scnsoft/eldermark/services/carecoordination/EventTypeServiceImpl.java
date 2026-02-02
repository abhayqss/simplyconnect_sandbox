package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.EventTypeDao;
import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author averazub
 * @author knetkachou
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 05-Oct-15.
 */
@Service
public class EventTypeServiceImpl implements EventTypeService {

    private final EventTypeDao eventTypeDao;

    private Map<Long, EventType> eventTypes = null;
    private Map<String, EventType> eventTypesByCode = null;
    private List<KeyValueDto> eventTypesList = null;

    @Autowired
    public EventTypeServiceImpl(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;
    }


    @Override
    public List<KeyValueDto> getAllEventTypes() {
        fillEventTypes();
        return eventTypesList;
    }

    public EventType getByCode(String code) {
        fillEventTypes();
        return eventTypesByCode.get(code);
    }

    public EventType get(Long id) {
        fillEventTypes();
        return eventTypes.get(id);
    }

    private void fillEventTypes() {
        if ((eventTypes == null) || (eventTypesByCode == null) || (eventTypesList == null)) {
            synchronized (eventTypeDao) {
                eventTypes = new HashMap<Long, EventType>();
                eventTypesByCode = new HashMap<String, EventType>();
                eventTypesList = new ArrayList<KeyValueDto>();
                for (EventType event : eventTypeDao.list("description")) {
                    eventTypes.put(event.getId(), event);
                    eventTypesByCode.put(event.getCode(), event);
                    if (!event.isForExternalUse() && !event.isService()) {
                        eventTypesList.add(new KeyValueDto(event.getId(), event.getDescription()));
                    }
                }
            }
        }
    }

}
