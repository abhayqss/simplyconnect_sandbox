package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CareTeamMemberFacade {

    Page<CareTeamMemberListItemDto> find(CareTeamFilter careTeamFilter, Pageable pageable);

    CareTeamMemberDto findById(Long careTeamMemberId);

    Long count(CareTeamFilter filter);

    boolean exists(CareTeamFilter filter);

    void deleteById(Long careTeamMemberId);
}
