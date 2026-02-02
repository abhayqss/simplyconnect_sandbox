package com.scnsoft.eldermark.service.healthpartners.medication;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;

public interface HpClaimMedicationProvider {

    Medication getMedication(IdOrganizationIdActiveAware client, HealthPartnersRxClaim claim, RxClaimProcessingContext ctx);

}
