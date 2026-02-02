package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientOutreachReportDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsOutreachReportItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientOutreachReportDetailsConverter implements Converter<ClientOutreachReportDetailsAware, ClientDetailsOutreachReportItem> {

    @Override
    public ClientDetailsOutreachReportItem convert(ClientOutreachReportDetailsAware source) {
        return new ClientDetailsOutreachReportItem(source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getMedicareNumber(),
                source.getDeactivationReason(),
                source.getIntakeDate(),
                source.getExitDate(),
                source.getCommunityId(),
                source.getCommunityName(),
                source.getOrganizationId()
        );
    }
}
