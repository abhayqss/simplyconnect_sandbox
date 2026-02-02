package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.ProspectSaveData;
import com.scnsoft.eldermark.dto.prospect.ProspectActivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDeactivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectFilter;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.history.ProspectHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProspectService extends ProjectingService<Long> {

    ProspectHistory createHistoryRecord(Long id);

    Prospect save(Prospect prospect);

    Prospect save(ProspectSaveData data);

    Prospect findById(Long id);

    <T> Page<T> find(ProspectFilter filter, Pageable pageable, Class<T> projectionClass);

    Optional<IdAware> findByCommunityIdAndExternalId(Long communityId, Long externalId);

    void activateProspect(Long prospectId, ProspectActivationDto activationDto);

    void deactivateProspect(Long prospectId, ProspectDeactivationDto deactivationDto);

    Boolean isValidSsn(Long prospectId, Long communityId, String ssn);

    Boolean isEmailUnique(Long prospectId, Long organizationId, String email);
}
