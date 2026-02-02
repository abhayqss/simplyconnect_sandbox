package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ClientProblemDto;
import com.scnsoft.eldermark.dto.ProblemObservationCodeDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientProblemDtoConverter implements Converter<ClientProblem, ClientProblemDto> {

    @Autowired
    Converter<ProblemObservation, ProblemObservationCodeDto> problemObservationCodeDtoConverter;

    @Override
    public ClientProblemDto convert(ClientProblem source) {
        ClientProblemDto target = new ClientProblemDto();
        target.setId(source.getId());
        target.setName(source.getProblem());
        target.setIdentifiedDate(DateTimeUtils.toEpochMilli(source.getIdentifiedDate()));
        target.setCode(source.getProblemCode());
        target.setCodeSet(source.getProblemCodeSet());
        target.setRecordedBy(Optional.ofNullable(source.getRecordedBy()).map(Employee::getFullName).orElse(null));
        target.setType(source.getType());
        target.setStatus(Optional.ofNullable(source.getProblemStatusCode()).map(CcdCode::getDisplayName).orElse(null));
        target.setResolvedDate(DateTimeUtils.toEpochMilli(source.getStoppedDate()));
        target.setOnsetDate(DateTimeUtils.toEpochMilli(source.getOnsetDate()));
        target.setRecordedDate(DateTimeUtils.toEpochMilli(source.getRecordedDate()));
        target.setPrimary(source.getPrimary());
        target.setComments(source.getComments());
        setAgeObservation(source, target);
        target.setOrganizationName(Optional.ofNullable(source.getClient().getOrganization()).map(Organization::getName).orElse(null));
        target.setCommunityName(Optional.ofNullable(source.getClient().getCommunity()).map(Community::getName).orElse(null));
        return target;
    }

    private void setAgeObservation(ClientProblem source, ClientProblemDto target) {
        var value = source.getAgeObservationValue();
        var unit = source.getAgeObservationUnit();
        if ("a".equalsIgnoreCase(unit)) {
            target.setAgeObservationValue(value);
            target.setAgeObservationUnit("year");
        } else if ("day".equalsIgnoreCase(unit)) {
            if (value != null && value >= 365) {
                target.setAgeObservationValue(value/365);
                target.setAgeObservationUnit("year");
            } else {
                target.setAgeObservationValue(value);
                target.setAgeObservationUnit(unit);
            }
        } else {
            target.setAgeObservationValue(value);
            target.setAgeObservationUnit(unit);
        }
    }


}
