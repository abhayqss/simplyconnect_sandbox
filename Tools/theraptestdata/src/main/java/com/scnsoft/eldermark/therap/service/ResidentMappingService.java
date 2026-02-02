package com.scnsoft.eldermark.therap.service;

import com.scnsoft.eldermark.therap.bean.TherapRecord;
import com.scnsoft.eldermark.therap.entity.ResidentMapping;

public interface ResidentMappingService {

    ResidentMapping generateAndCreateNewMapping(TherapRecord patientIdentifier);

    ResidentMapping findAndUpdateMapping(TherapRecord therapRecord);
}
