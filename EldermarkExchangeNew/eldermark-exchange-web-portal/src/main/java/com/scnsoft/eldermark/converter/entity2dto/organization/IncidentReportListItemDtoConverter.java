package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.IncidentReportListItemDto;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncidentReportListItemDtoConverter implements Converter<IncidentReport, IncidentReportListItemDto> {

    @Override
    public IncidentReportListItemDto convert(IncidentReport source) {
        var target = new IncidentReportListItemDto();
        target.setId(source.getId());
        target.setClientId(source.getEvent().getClientId());
        target.setClientName(source.getFirstName() + " " + source.getLastName());
        target.setEventId(source.getEventId());
        if (source.getEvent().getClient().getAvatar() != null) {
            target.setClientAvatarId(source.getEvent().getClient().getAvatar().getId());
        }
        target.setEventType(source.getEvent().getEventType().getDescription());
        var curStatus = Optional.ofNullable(source.getStatus()).orElseGet(() -> source.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT);
        target.setStatusName(curStatus.name());
        target.setStatusTitle(curStatus.getDisplayName());
        target.setIncidentDate(DateTimeUtils.toEpochMilli(source.getIncidentDatetime()));
        target.setCanDelete(Optional.ofNullable(source.getStatus())
                .orElseGet(() -> source.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT) == IncidentReportStatus.DRAFT);
        target.setClientActive(source.getEvent().getClient().getActive());
        return target;
    }
}
