package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.EncountersFacade;
import com.scnsoft.eldermark.service.EncounterService;
import com.scnsoft.eldermark.web.entity.EncounterDto;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class EncountersFacadeImpl extends BasePhrFacade implements EncountersFacade {

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private Converter<Encounter, EncounterInfoDto> encounterInfoConverter;

    @Autowired
    private Converter<Encounter, EncounterDto> encounterConverter;

    @Override
    public Page<EncounterInfoDto> getEncountersInfoForUser(Long userId, Pageable pageable) {
        return encounterService.getEncountersForResidents(getUserResidentIds(userId, AccessRight.Code.MY_PHR), pageable)
                .map(encounterInfoConverter);
    }

    @Override
    public Page<EncounterInfoDto> getEncounterInfoForReceiver(Long receiverId, Pageable pageable) {
        return encounterService.getEncountersForResidents(getReceiverResidentIds(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(encounterInfoConverter);
    }

    @Override
    public EncounterDto getEncounter(Long encounterId) {
        final Encounter encounter = encounterService.getEncounter(encounterId);
        validateAssociation(encounter.getResident().getId(), AccessRight.Code.MY_PHR);
        return encounterConverter.convert(encounter);
    }
}
