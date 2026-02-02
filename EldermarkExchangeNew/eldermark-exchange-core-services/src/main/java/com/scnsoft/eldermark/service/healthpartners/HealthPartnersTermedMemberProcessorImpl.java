package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.healthpartners.client.HpClientFactory;
import com.scnsoft.eldermark.service.healthpartners.client.HpClientInfo;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthPartnersTermedMemberProcessorImpl
        extends HealthPartnersBaseRecordProcessor<HealthPartnersTermedMember, ClaimProcessingContext>
        implements HealthPartnersTermedMemberProcessor {

    @Autowired
    private ClientService clientService;

    @Autowired
    private HpClientFactory<HpClientInfo> hpClientInfoHpClientFactory;

    @Override
    protected ClaimProcessingContext createContext() {
        return new ClaimProcessingContext();
    }

    @Override
    protected Long doProcess(HealthPartnersTermedMember termedMember, Long communityId, ClaimProcessingContext ctx) {
        var client = clientService.findHealthPartnersClient(termedMember.getMemberIdentifier(), communityId)
                .map(foundClient -> deactivateIfActive(foundClient, ctx))
                .map(deactivated -> clientService.getById(deactivated.getId()))
                .orElseGet(() -> createInactiveClient(termedMember, communityId, ctx));

        termedMember.setClient(client);
        termedMember.setClientId(client.getId());
        termedMember.setClientIsNew(ctx.isClientIsNewHint());
        return client.getId();
    }

    private IdOrganizationIdActiveAware deactivateIfActive(IdOrganizationIdActiveAware foundClient, ClaimProcessingContext ctx) {
        if (Boolean.TRUE.equals(foundClient.getActive())) {
            ctx.getUpdateTypes().add(ResidentUpdateType.RESIDENT);
            clientService.deactivateClient(foundClient.getId());
            return new IdOrganizationIdActiveAware() {

                @Override
                public Long getOrganizationId() {
                    return foundClient.getOrganizationId();
                }

                @Override
                public Long getId() {
                    return foundClient.getId();
                }

                @Override
                public Boolean getActive() {
                    return false;
                }
            };
        }
        return foundClient;
    }

    private Client createInactiveClient(HealthPartnersTermedMember termedMember, Long communityId, ClaimProcessingContext ctx) {
        ctx.setClientIsNewHint(true);
        ctx.getUpdateTypes().add(ResidentUpdateType.RESIDENT);
        var info = new HpClientInfo(
                termedMember.getMemberIdentifier(),
                termedMember.getMemberFirstName(),
                termedMember.getMemberMiddleName(),
                termedMember.getMemberLastName(),
                termedMember.getBirthDate(),
                communityId,
                false
        );
        return clientService.save(hpClientInfoHpClientFactory.create(info));
    }
}
