package com.scnsoft.eldermark.services.carecoordination.adt;

import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.shared.carecoordination.AdtDto;

public interface ProcessAdtService {

    /**
     * fill admit and discharge dates for SC objects using information from ADT message
     * @param adtDto
     * @param careCoordinationResident
     */
    void processAdmitDischargeDates(AdtDto adtDto, CareCoordinationResident careCoordinationResident);

    /**
     * fill death date for SC objects using information from ADT message
     * @param adtDto
     * @param careCoordinationResident
     */
    void processDeathDate(AdtDto adtDto, CareCoordinationResident careCoordinationResident);
}
