package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.MedicationSupplyOrder;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.service.healthpartners.author.HpMedicalProfessionalAuthorFactory;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class HpMedicationFactoryImpl implements HpMedicationFactory {
    private static final Logger logger = LoggerFactory.getLogger(HpMedicationFactoryImpl.class);

    @Autowired
    private HpMedicalProfessionalAuthorFactory hpMedicalProfessionalAuthorFactory;

    @Autowired
    private ClientService clientService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    @Transactional
    public Medication createMedication(HealthPartnersRxClaim claim, CcdCode productNameCode,
                                       IdOrganizationIdActiveAware client) {
        logger.info("Creating medication");
        var medication = new Medication();

        var organization = organizationService.getOne(client.getOrganizationId());
        medication.setLegacyId(0);
        medication.setMedicationStarted(DateTimeUtils.toDate(claim.getServiceDate()));
        medication.setClient(clientService.getById(client.getId()));
        medication.setOrganization(organization);
        medication.setMedicationInformation(createMedicationInformation(claim, productNameCode, organization));
        medication.setMedicationSupplyOrder(createMedicationSupplyOrder(claim, organization));
        medication.setMedicationDispenses(new ArrayList<>());
        return medication;
    }

    private MedicationInformation createMedicationInformation(HealthPartnersRxClaim claim, CcdCode productNameCode,
                                                              Organization organization) {
        var medicationInformation = new MedicationInformation();
        medicationInformation.setLegacyId(0);
        medicationInformation.setLegacyTable(HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE);
        medicationInformation.setOrganization(organization);
        if (productNameCode != null) {
            medicationInformation.setProductNameCode(productNameCode);
        } else {
            medicationInformation.setProductNameText(claim.getDrugName());
        }
        return medicationInformation;
    }

    private MedicationSupplyOrder createMedicationSupplyOrder(HealthPartnersRxClaim claim, Organization organization) {
        var medicationSupplyOrder = new MedicationSupplyOrder();
        medicationSupplyOrder.setLegacyId(0);
        medicationSupplyOrder.setLegacyTable(HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE);
        medicationSupplyOrder.setOrganization(organization);
        medicationSupplyOrder.setDAWProductSelectionCode(claim.getDAWProductSelectionCode());
        medicationSupplyOrder.setPrescriptionOriginCode(claim.getPrescriptionOriginCode());
        medicationSupplyOrder.setPrescriptionNumber(claim.getRXNumber());

        var pair = hpMedicalProfessionalAuthorFactory.create(
                organization,
                HealthPartnersUtils.MEDICATION_CLAIM_LEGACY_TABLE,
                claim.getPrescribingPhysicianNPI(),
                claim.getPrescriberFirstName(),
                claim.getPrescriberMiddleName(),
                claim.getPrescriberLastName()
        );

        medicationSupplyOrder.setAuthor(pair.getFirst());
        medicationSupplyOrder.setMedicalProfessional(pair.getSecond());
        return medicationSupplyOrder;
    }
}
