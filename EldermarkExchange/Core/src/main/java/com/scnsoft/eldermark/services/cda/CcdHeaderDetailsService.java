package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.CcdHeaderDetails;
import com.scnsoft.eldermark.entity.Resident;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for reading
 */
public interface CcdHeaderDetailsService {

    @Transactional(readOnly = true)
    CcdHeaderDetails getHeaderDetails(Long residentId);
    @Transactional(readOnly = true)
    CcdHeaderDetails getHeaderDetails(Long mainResidentId, List<Long> residentIds);

    @Transactional(readOnly = true)
    Resident getRecordTarget(Long residentId);

}