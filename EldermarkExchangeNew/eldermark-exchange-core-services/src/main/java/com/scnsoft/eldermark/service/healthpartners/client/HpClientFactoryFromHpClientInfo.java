package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.basic.CareCoordinationConstants;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HpClientFactoryFromHpClientInfo implements HpClientFactory<HpClientInfo> {
    public static final String POLICY_OBTAINED_FROM_CONTRACT_VALUE = "Contracted agreement in place between Consana and HealthPartners for access of client information using SimplyConnect";

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    @Transactional
    public Client create(HpClientInfo clientInfo) {
        var client = new Client();

        CareCoordinationConstants.setLegacyId(client);
        client.setLegacyTable(CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);

        var organizationId = communityService.findById(clientInfo.getCommunityId(), OrganizationIdAware.class).getOrganizationId();
        var organization = organizationService.getOne(organizationId);

        var community = communityService.get(clientInfo.getCommunityId());

        client.setOrganization(organization);
        client.setOrganizationId(organization.getId());
        client.setCommunity(community);
        client.setCommunityId(clientInfo.getCommunityId());

        client.setActive(clientInfo.isActive());

        client.setPerson(CareCoordinationUtils.createNewPerson(organization));

        setClientNames(clientInfo, client);

        client.setBirthDate(clientInfo.getBirthDate());
        client.setHealthPartnersMemberIdentifier(clientInfo.getMemberIdentifier());

        clientHieConsentDefaultPolicyService.fillDefaultPolicy(client, POLICY_OBTAINED_FROM_CONTRACT_VALUE);
        return client;
    }

    private void setClientNames(HpClientInfo clientInfo, Client client) {
        client.setFirstName(clientInfo.getMemberFirstName());
        client.setLastName(clientInfo.getMemberLastName());
        client.setMiddleName(clientInfo.getMemberMiddleName());

        var name = CareCoordinationUtils.createAndAddName(
                client.getPerson(),
                clientInfo.getMemberFirstName(),
                clientInfo.getMemberLastName());
        name.setMiddle(clientInfo.getMemberMiddleName());
    }
}
