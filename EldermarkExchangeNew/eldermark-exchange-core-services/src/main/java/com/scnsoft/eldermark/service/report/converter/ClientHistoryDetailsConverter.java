package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientHistoryDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientHistoryDetailsConverter implements Converter<ClientHistoryDetailsAware, ClientDetailsItem> {

    @Override
    public ClientDetailsItem convert(ClientHistoryDetailsAware source) {
        return new ClientDetailsItem(source.getClientId(),
                source.getClientFirstName(),
                source.getClientLastName(),
                source.getClientCommunityId(),
                source.getClientCommunityName(),
                source.getDeactivationReason(),
                source.getDeactivationDate(),
                source.getClientOrganizationId(),
                source.getClientOrganizationName(),
                source.getClientActive(),
                source.getClientMedicaidNumber(),
                source.getClientPerson(),
                source.getInNetworkInsuranceDisplayName(),
                source.getClientBirthDate()
        );
    }
}