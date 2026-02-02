package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.assessment.ClientAssessmentResultHistoryItemDto;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;

import static java.util.Optional.ofNullable;

@Component
public class ClientAssessmentResultHistoryItemDtoConverter implements Converter<ClientAssessmentResult, ClientAssessmentResultHistoryItemDto> {

    @Override
    public ClientAssessmentResultHistoryItemDto convert(ClientAssessmentResult source) {
        var target = new ClientAssessmentResultHistoryItemDto();

        target.setId(source.getId());
        target.setModifiedDate(ofNullable(source.getLastModifiedDate()).map(DateTimeUtils::toEpochMilli).orElse(null));

        if (source.getDateCompleted() != null) {
            target.setCompletedDate(source.getDateCompleted().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        target.setAuthor(source.getEmployee().getFullName());
        target.setStatusName(source.getAssessmentStatus());
        target.setStatusTitle(source.getAssessmentStatus().getDisplayName());
        target.setTypeId(source.getAssessment().getId());
        target.setArchived(source.getArchived());
        target.setAuthorRole(source.getEmployee().getCareTeamRole().getName());
        return target;
    }
}
