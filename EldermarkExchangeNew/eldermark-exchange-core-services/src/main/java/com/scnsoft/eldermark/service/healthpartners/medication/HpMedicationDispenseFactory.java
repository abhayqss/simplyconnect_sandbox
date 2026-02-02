package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;

public interface HpMedicationDispenseFactory {

    MedicationDispense create(HealthPartnersRxClaim claim, IdOrganizationIdActiveAware client);

}
