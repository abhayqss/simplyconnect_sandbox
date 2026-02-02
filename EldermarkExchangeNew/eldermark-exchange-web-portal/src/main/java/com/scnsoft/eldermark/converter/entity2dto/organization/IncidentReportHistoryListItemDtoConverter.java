package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.IncidentReportHistoryListItemDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentReportHistoryListItemDtoConverter implements Converter<IncidentReport, IncidentReportHistoryListItemDto> {

    @Override
    public IncidentReportHistoryListItemDto convert(IncidentReport source) {
        var target = new IncidentReportHistoryListItemDto();
        target.setAuthor(source.getEmployee().getFullName());
        target.setAuthorRole(source.getEmployee().getCareTeamRole().getDisplayName());
        target.setStatus(source.getAuditableStatus().getDisplayName());
        target.setDate(source.getLastModifiedDate().toEpochMilli());
        target.setIsArchived(source.getArchived());
        target.setReportId(source.getId());
        return target;
    }
}
