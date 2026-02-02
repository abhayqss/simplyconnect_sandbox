package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;

interface HealthPartnersNormalRxClaimProcessor {
    void processNormalRxClaim(HealthPartnersRxClaim claim, RxClaimProcessingContext ctx, IdOrganizationIdActiveAware client);
}
