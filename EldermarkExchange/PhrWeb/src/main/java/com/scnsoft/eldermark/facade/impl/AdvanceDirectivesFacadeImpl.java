package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.AdvanceDirectivesFacade;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.service.AdvanceDirectiveService;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveDto;
import com.scnsoft.eldermark.web.entity.AdvanceDirectiveInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class AdvanceDirectivesFacadeImpl extends BasePhrFacade implements AdvanceDirectivesFacade {

    @Autowired
    private AdvanceDirectiveService advanceDirectiveService;

    @Autowired
    private Converter<AdvanceDirective, AdvanceDirectiveInfoDto> advanceDirectiveInfoDtoConverter;

    @Autowired
    private Converter<AdvanceDirective, AdvanceDirectiveDto> advanceDirectiveDtoConverter;

    @Override
    public Page<AdvanceDirectiveInfoDto> getAdvanceDirectivesForUser(Long userId, Pageable pageable) {
        return advanceDirectiveService.getAdvanceDirectivesForResidents(getUserResidentIds(userId, AccessRight.Code.MY_PHR), pageable)
                .map(advanceDirectiveInfoDtoConverter);
    }

    @Override
    public Page<AdvanceDirectiveInfoDto> getAdvanceDirectivesForReceiver(Long receiverId, Pageable pageable) {
        return advanceDirectiveService.getAdvanceDirectivesForResidents(getReceiverResidentIds(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(advanceDirectiveInfoDtoConverter);
    }

    @Override
    public AdvanceDirectiveDto getAdvanceDirective(Long advanceDirectiveId) {
        final AdvanceDirective advanceDirective = advanceDirectiveService.getAdvanceDirective(advanceDirectiveId);
        validateAssociation(advanceDirective.getResident().getId(), AccessRight.Code.MY_PHR);
        return advanceDirectiveDtoConverter.convert(advanceDirective);
    }
}
