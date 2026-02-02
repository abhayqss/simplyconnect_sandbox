package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.ResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ResidentListItemDto;
import com.scnsoft.eldermark.api.external.web.dto.ResolveItiPatientIdentifierRequestDto;
import com.scnsoft.eldermark.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResidentsService {

    Page<ResidentListItemDto> listByOrganization(Long orgId, Pageable pageable);

    Page<ResidentListItemDto> listByCommunity(Long communityId, Pageable pageable);

    void checkAccessOrThrow(Long residentId);

    ResidentDto get(Long residentId);

    Client getEntity(Long residentId);

    ResidentDto create(Long communityId, String phone, String email, String firstName, String lastName,
                       String ssn, String middleName, String nucleusUserId);

    Long resolveItiPatientIdentifier(
            ResolveItiPatientIdentifierRequestDto resolveItiPatientIdentifierRequestDto);
}
