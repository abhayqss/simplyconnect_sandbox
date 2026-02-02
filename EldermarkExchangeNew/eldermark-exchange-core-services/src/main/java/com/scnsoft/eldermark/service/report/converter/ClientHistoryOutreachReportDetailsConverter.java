package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientHistoryOutreachReportDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsOutreachReportItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientHistoryOutreachReportDetailsConverter implements Converter<ClientHistoryOutreachReportDetailsAware, ClientDetailsOutreachReportItem> {

    @Override
    public ClientDetailsOutreachReportItem convert(ClientHistoryOutreachReportDetailsAware source) {
        return new ClientDetailsOutreachReportItem(source.getClientId(),
                source.getClientFirstName(),
                source.getClientLastName(),
                source.getClientMedicareNumber(),
                source.getDeactivationReason(),
                source.getClientIntakeDate(),
                source.getExitDate(),
                source.getClientCommunityId(),
                source.getClientCommunityName(),
                source.getClientOrganizationId()
        );
    }
}
