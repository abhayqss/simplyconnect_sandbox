package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.VitalSignObservationDetailsDto;
import com.scnsoft.eldermark.api.external.web.dto.VitalSignObservationReport;
import com.scnsoft.eldermark.api.shared.entity.VitalSignType;
import org.springframework.data.domain.Pageable;

public interface VitalSignsService {

    VitalSignObservationDetailsDto create(Long residentId, VitalSignObservationDetailsDto body);

    VitalSignObservationDetailsDto get(Long residentId, Long vitalSignId);

    VitalSignObservationReport report(Long residentId, VitalSignType type, String dateFrom, String dateTo, Pageable pageable);
}
