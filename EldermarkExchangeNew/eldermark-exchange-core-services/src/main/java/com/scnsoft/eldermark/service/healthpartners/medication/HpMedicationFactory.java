package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;

public interface HpMedicationFactory {

    Medication createMedication(HealthPartnersRxClaim claim, CcdCode productNameCode, IdOrganizationIdActiveAware client);

}
