package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.FamilyHistoryInfoDto;
import com.scnsoft.eldermark.web.entity.FamilyHistoryObservationListItemDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FamilyHistoryInfoDtoConverter extends BasePhrService implements Converter<FamilyHistory, FamilyHistoryInfoDto> {

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private Converter<FamilyHistoryObservation, FamilyHistoryObservationListItemDto> familyHistoryObservationListItemDtoConverter;

    @Override

    public FamilyHistoryInfoDto convert(FamilyHistory familyHistory) {
        FamilyHistoryInfoDto familyHistoryInfoDto = new FamilyHistoryInfoDto();
        familyHistoryInfoDto.setRelationshipToPatient(familyHistory.getRelatedSubjectCode() != null ? familyHistory.getRelatedSubjectCode().getDisplayName() : null);
        familyHistoryInfoDto.setGender(familyHistory.getAdministrativeGenderCode() != null ? familyHistory.getAdministrativeGenderCode().getDisplayName() : null);
        familyHistoryInfoDto.setBirthDate(familyHistory.getBirthTime());
        familyHistoryInfoDto.setDataSource(DataSourceService.transform(familyHistory.getDatabase(), familyHistory.getResident().getId()));

        if (CollectionUtils.isNotEmpty(familyHistory.getFamilyHistoryObservations())) {
            for (FamilyHistoryObservation familyHistoryObservation: familyHistory.getFamilyHistoryObservations()) {
                familyHistoryInfoDto.addObservationsItem(familyHistoryObservationListItemDtoConverter.convert(familyHistoryObservation));
            }
        }

        return familyHistoryInfoDto;
    }
}
