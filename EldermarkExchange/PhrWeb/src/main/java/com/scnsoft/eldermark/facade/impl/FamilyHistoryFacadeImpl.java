package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.FamilyHistoryFacade;
import com.scnsoft.eldermark.service.FamilyHistoryService;
import com.scnsoft.eldermark.web.entity.FamilyHistoryInfoDto;
import com.scnsoft.eldermark.web.entity.FamilyHistoryListItemDto;
import com.scnsoft.eldermark.web.entity.FamilyHistoryObservationInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FamilyHistoryFacadeImpl extends BasePhrFacade implements FamilyHistoryFacade {

    @Autowired
    private Converter<FamilyHistory, FamilyHistoryInfoDto> familyHistoryInfoDtoConverter;

    @Autowired
    private Converter<FamilyHistory, FamilyHistoryListItemDto> familyHistoryListItemDtoConverter;

    @Autowired
    private Converter<FamilyHistoryObservation, FamilyHistoryObservationInfoDto> familyHistoryObservationInfoDtoConverter;

    @Autowired
    private FamilyHistoryService familyHistoryService;

    public Page<FamilyHistoryListItemDto> listFamilyHistoryForUser(Long userId, Pageable pageable){
        return familyHistoryService.listFamilyHistory(getUserResidentIds(userId, AccessRight.Code.MY_PHR), pageable)
                .map(familyHistoryListItemDtoConverter);
    }

    public Page<FamilyHistoryListItemDto> listFamilyHistoryForReceiver(Long receiverId, Pageable pageable){
        return familyHistoryService.listFamilyHistory(getReceiverResidentIds(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(familyHistoryListItemDtoConverter);
    }

    public FamilyHistoryInfoDto getFamilyHistoryInfo(Long familyHistoryId) {
        final FamilyHistory familyHistory = familyHistoryService.getFamilyHistory(familyHistoryId);
        validateAssociation(familyHistory.getResident().getId(), AccessRight.Code.MY_PHR);
        return familyHistoryInfoDtoConverter.convert(familyHistory);
    }


    public FamilyHistoryObservationInfoDto getFamilyHistoryObservationInfo(Long observationId) {
        final FamilyHistoryObservation familyHistoryObservation = familyHistoryService.getFamilyHistoryObservation(observationId);
        validateAssociation(familyHistoryObservation.getFamilyHistory().getResident().getId(), AccessRight.Code.MY_PHR);
        return familyHistoryObservationInfoDtoConverter.convert(familyHistoryObservation);
    }
}
