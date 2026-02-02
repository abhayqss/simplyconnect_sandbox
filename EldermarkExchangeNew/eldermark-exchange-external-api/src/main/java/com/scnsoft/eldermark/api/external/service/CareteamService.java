package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberBriefDto;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CareteamService {
    Page<CareteamMemberBriefDto> listCommunityCTMs(Long communityId, Pageable pageable);

    Page<CareteamMemberBriefDto> listResidentCTMs(Long residentId, String directory, Pageable pageable);

    CareteamMemberDto get(Long residentId, Long contactId);
}
