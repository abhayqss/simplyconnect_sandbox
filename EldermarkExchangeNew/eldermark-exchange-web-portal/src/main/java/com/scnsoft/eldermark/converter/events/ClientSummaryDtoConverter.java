package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.BaseClientSummaryViewDataConverter;
import com.scnsoft.eldermark.dto.events.ClientSummaryDto;
import com.scnsoft.eldermark.entity.Client;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientSummaryDtoConverter extends BaseClientSummaryViewDataConverter<ClientSummaryDto> {

    @Override
    protected ClientSummaryDto create() {
        return new ClientSummaryDto();
    }

    @Override
    protected void fill(Client client, ClientSummaryDto info) {
        super.fill(client, info);

        info.setId(client.getId());
        info.setFirstName(client.getFirstName());
        info.setLastName(client.getLastName());
        info.setOrganizationId(client.getOrganizationId());
        info.setCommunityId(client.getCommunity().getId());


    }
}
