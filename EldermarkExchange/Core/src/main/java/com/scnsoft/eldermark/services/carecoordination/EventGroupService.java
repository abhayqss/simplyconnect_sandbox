package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

public interface EventGroupService {

     EventGroup get(Long id) ;
     List<KeyValueDto> getAllEventGroups();
}
