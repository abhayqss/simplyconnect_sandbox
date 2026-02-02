package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.MedicationInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicationsService {

    Page<MedicationInfoDto> getInactive(Long residentId, Pageable pageable);

    Page<MedicationInfoDto> getActive(Long residentId, Pageable pageable);
}
