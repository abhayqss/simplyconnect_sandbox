package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientDetailsConverter implements Converter<ClientDetailsAware, ClientDetailsItem> {

    @Override
    public ClientDetailsItem convert(ClientDetailsAware source) {
        return new ClientDetailsItem(source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getCommunityId(),
                source.getCommunityName(),
                source.getDeactivationReason(),
                source.getDeactivationDate(),
                source.getOrganizationId(),
                source.getOrganizationName(),
                source.getActive(),
                source.getMedicaidNumber(),
                source.getPerson(),
                source.getInNetworkInsuranceDisplayName(),
                source.getBirthDate()
        );
    }
}
