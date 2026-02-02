package com.scnsoft.eldermark.facade.prospect;

import com.scnsoft.eldermark.dto.prospect.ProspectCommunityUniquenessDto;
import com.scnsoft.eldermark.dto.prospect.ProspectActivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDeactivationDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.ProspectFilterDto;
import com.scnsoft.eldermark.dto.prospect.ProspectListItemDto;
import com.scnsoft.eldermark.dto.prospect.ProspectOrganizationUniquenessDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProspectFacade {

    Page<ProspectListItemDto> find(ProspectFilterDto filter, Pageable pageable);

    Long add(ProspectDto prospectDto);

    Long edit(ProspectDto prospectDto);

    ProspectDto findById(Long prospectId);

    void activate(Long prospectId, ProspectActivationDto activationDto);

    void deactivate(Long prospectId, ProspectDeactivationDto deactivationDto);

    boolean canView();

    boolean canAdd(Long organizationId);

    boolean canEdit(Long prospectId);

    ProspectCommunityUniquenessDto validateUniqueInCommunity(Long prospectId, Long communityId, String ssn);

    ProspectOrganizationUniquenessDto validateUniqueInOrganization(Long clientId, Long organizationId, String email);
}
