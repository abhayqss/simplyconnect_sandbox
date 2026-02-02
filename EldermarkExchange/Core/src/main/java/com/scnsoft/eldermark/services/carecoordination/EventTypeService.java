package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

/**
 * Created by pzhurba on 05-Oct-15.
 */
public interface EventTypeService {
    public EventType getByCode(String code);

    public EventType get(Long id) ;
    public List<KeyValueDto> getAllEventTypes();
}
