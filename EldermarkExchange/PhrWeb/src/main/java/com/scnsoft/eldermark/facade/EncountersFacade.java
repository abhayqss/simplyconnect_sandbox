package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.EncounterDto;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import com.scnsoft.eldermark.web.entity.ProcedureListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 */
public interface EncountersFacade {

    Page<EncounterInfoDto> getEncountersInfoForUser(Long userId, Pageable pageable);

    Page<EncounterInfoDto> getEncounterInfoForReceiver(Long receiverId, Pageable pageable);

    EncounterDto getEncounter(Long encounterId);

}
