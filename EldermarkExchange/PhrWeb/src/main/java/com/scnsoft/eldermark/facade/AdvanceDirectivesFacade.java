package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.AdvanceDirectiveDto;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import com.scnsoft.eldermark.web.entity.EncounterDto;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdvanceDirectivesFacade {

    Page<AdvanceDirectiveInfoDto> getAdvanceDirectivesForUser(Long userId, Pageable pageable);

    Page<AdvanceDirectiveInfoDto> getAdvanceDirectivesForReceiver(Long receiverId, Pageable pageable);

    AdvanceDirectiveDto getAdvanceDirective(Long advanceDirectiveId);

}
