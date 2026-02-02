package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class HpMedicationDispenseFactoryImpl implements HpMedicationDispenseFactory {
    private static final Logger logger = LoggerFactory.getLogger(HpMedicationDispenseFactoryImpl.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    @Transactional
    public MedicationDispense create(HealthPartnersRxClaim claim, IdOrganizationIdActiveAware client) {
        logger.info("Creating medication dispense");
        var medicationDispense = new MedicationDispense();
        medicationDispense.setLegacyId(0);
        medicationDispense.setLegacyTable(HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE);
        var organization = organizationService.getOne(client.getOrganizationId());
        medicationDispense.setOrganization(organization);
        medicationDispense.setOrganizationId(client.getOrganizationId());
        medicationDispense.setFillNumber(claim.getRefillNumber());
        medicationDispense.setDispenseDateLow(DateTimeUtils.toDate(claim.getServiceDate()));
        medicationDispense.setDispenseDateHigh(DateTimeUtils.toDate(claim.getServiceDate().plus(claim.getDaysSupply(), ChronoUnit.DAYS)));
        medicationDispense.setQuantity(claim.getQuantityDispensed());
        medicationDispense.setQuantityQualifierCode(claim.getQuantityQualifierCode());
        medicationDispense.setPrescriptionNumber(claim.getRXNumber());

        var provider = communityService.findByHealthPartnersBillingProviderRef(
                        claim.getClaimBillingProvider(),
                        client.getOrganizationId()
                )
                .map(idAware -> communityService.get(idAware.getId()))
                .orElseGet(() -> createProvider(claim, organization));

        medicationDispense.setProvider(provider);
        logger.info("Medication dispense created");
        return medicationDispense;
    }

    private Community createProvider(HealthPartnersRxClaim claim, Organization organization) {
        var community = new Community();
        community.setLegacyId(Instant.now().toString());
        community.setLegacyTable(HealthPartnersUtils.DISPENSE_PROVIDER_LEGACY_TABLE);
        community.setName(claim.getPharmacyName());
        community.setOrganization(organization);
        community.setModuleHie(true);
        community.setProviderNpi(claim.getPharmacyNPI());
        community.setHealthPartnersBillingProviderRef(claim.getClaimBillingProvider());
        return communityService.save(community);
    }
}
