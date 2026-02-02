package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HpClaimClientProviderImpl implements HpClaimClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(HpClaimClientProviderImpl.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private HpClientFactory<HpClientInfo> hpClientInfoHpClientFactory;

    @Override
    @Transactional
    public IdOrganizationIdActiveAware getClient(BaseHealthPartnersRecord claim,
                                                 Long communityId,
                                                 ClaimProcessingContext ctx) {
        var foundClient = clientService.findHealthPartnersClient(claim.getMemberIdentifier(), communityId);
        foundClient.ifPresent(c -> logger.info("Found client for hp member identifier {}", claim.getMemberIdentifier()));

        return foundClient
                .map(client -> activateClientIfInactive(client, ctx))
                .orElseGet(() -> createClient(claim, communityId, ctx));
    }

    private IdOrganizationIdActiveAware activateClientIfInactive(IdOrganizationIdActiveAware client,
                                                                 ClaimProcessingContext ctx) {

        if (!Boolean.TRUE.equals(client.getActive())) {
            ctx.getUpdateTypes().add(ResidentUpdateType.RESIDENT);
            clientService.activateClient(client.getId());
            return new IdOrganizationIdActiveAware() {
                @Override
                public Boolean getActive() {
                    return true;
                }

                @Override
                public Long getId() {
                    return client.getId();
                }

                @Override
                public Long getOrganizationId() {
                    return client.getOrganizationId();
                }
            };
        }
        return client;
    }

    private IdOrganizationIdActiveAware createClient(BaseHealthPartnersRecord claim, Long communityId, ClaimProcessingContext ctx) {
        logger.info("Creating new client for hp member identifier {}", claim.getMemberIdentifier());
        ctx.setClientIsNewHint(true);
        ctx.getUpdateTypes().add(ResidentUpdateType.RESIDENT);
        var info = new HpClientInfo(
                claim.getMemberIdentifier(),
                claim.getMemberFirstName(),
                claim.getMemberMiddleName(),
                claim.getMemberLastName(),
                claim.getBirthDate(),
                communityId,
                true
        );
        return clientService.save(hpClientInfoHpClientFactory.create(info));
    }
}
