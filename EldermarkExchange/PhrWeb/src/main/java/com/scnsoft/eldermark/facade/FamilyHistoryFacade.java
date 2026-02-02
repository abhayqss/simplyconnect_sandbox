package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.FamilyHistoryInfoDto;
import com.scnsoft.eldermark.web.entity.FamilyHistoryListItemDto;
import com.scnsoft.eldermark.web.entity.FamilyHistoryObservationInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FamilyHistoryFacade {

    Page<FamilyHistoryListItemDto> listFamilyHistoryForUser(Long userId, Pageable pageable);

    Page<FamilyHistoryListItemDto> listFamilyHistoryForReceiver(Long receiverId, Pageable pageable);

    FamilyHistoryInfoDto getFamilyHistoryInfo(Long familyHistoryId);

    FamilyHistoryObservationInfoDto getFamilyHistoryObservationInfo(Long observationId);
}
