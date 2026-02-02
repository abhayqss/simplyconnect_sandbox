package com.scnsoft.eldermark.converter.entity2dto.household;

import static java.util.Optional.ofNullable;

import com.scnsoft.eldermark.beans.reports.enums.GenderType;
import com.scnsoft.eldermark.beans.reports.model.HouseholdMemberAssessmentDto;
import com.scnsoft.eldermark.beans.reports.model.NorCalComprehensiveAssessmentHouseHoldMembers;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dto.client.HouseHoldMemberListItemDto;
import com.scnsoft.eldermark.entity.client.householdmember.RelationshipType;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HouseholdMemberItemDtoConverter {

    @Autowired
    private CcdCodeDao ccdCodeDao;

    public List<HouseHoldMemberListItemDto> convert(NorCalComprehensiveAssessmentHouseHoldMembers source) {
        var dtos = new ArrayList<HouseHoldMemberListItemDto>();
        if (source != null) {
            addDto(dtos, source.getHouseholdMember1());
            addDto(dtos, source.getHouseholdMember2());
            addDto(dtos, source.getHouseholdMember3());
            addDto(dtos, source.getHouseholdMember4());
            addDto(dtos, source.getHouseholdMember5());
        }
        return dtos.stream().sorted(
            Comparator.comparing(HouseHoldMemberListItemDto::getIsActive, Comparator.reverseOrder())
                .thenComparing(HouseHoldMemberListItemDto::getFirstName)
                .thenComparing(HouseHoldMemberListItemDto::getLastName)
        )
            .collect(Collectors.toList());
    }

    private void addDto(List<HouseHoldMemberListItemDto> dtos, HouseholdMemberAssessmentDto source) {
        if (isHouseHoldNotEmpty(source)) {
            var target = new HouseHoldMemberListItemDto();
            target.setFirstName(source.getFirstName());
            target.setMiddleName(source.getMiddleName());
            target.setLastName(source.getLastName());
            target.setBirthDate(source.getBirthDate());
            convertGender(source.getGender()).ifPresent(target::setGender);
            //TODO if A gender that is not singularly "Female" or "Male" should be one more request to gender identity
            convertGender(source.getGenderIdentity()).ifPresent(target::setGenderIdentity);
            var relationshipType = RelationshipType.getByName(source.getRelationship());
            target.setRelationship(relationshipType != null ? relationshipType.getName() : null);
            target.setPhone(source.getPhone());
            target.setSocialSecurityNumber(source.getSocialSecurityNumber());
            target.setIsHouseholdHead("Active".equals(source.isHouseholdHead()));
            target.setIsActive("Active".equals(source.isActive()));
            if (source.isActive() != null) {
                var date = source.getDate() != null ? DateTimeUtils.parseUtcDateToInstantEndDay(source.getDate())
                    .toEpochMilli() : null;
                target.setDate(date);
            }

            dtos.add(target);
        }
    }

    private boolean isHouseHoldNotEmpty(HouseholdMemberAssessmentDto hm) {
        return hm != null && !StringUtils.isEmpty(hm.getFirstName());
    }

    private Optional<CcdCode> convertGender(String gender) {
        return ofNullable(gender)
            .map(GenderType::fromAssessmentValue)
            .map(genderType -> ccdCodeDao.getCcdCode(genderType.getCcdCode(), CodeSystem.ADMINISTRATIVE_GENDER.getOid()));
    }
}
