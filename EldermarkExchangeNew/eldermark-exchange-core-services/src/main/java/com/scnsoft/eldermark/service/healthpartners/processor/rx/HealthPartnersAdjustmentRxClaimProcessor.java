package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;

interface HealthPartnersAdjustmentRxClaimProcessor {

    void processAdjustmentRxClaim(HealthPartnersRxClaim originalClaim,
                                  HealthPartnersRxClaim adjustmentClaim,
                                  RxClaimProcessingContext ctx);
}
