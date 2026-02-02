package com.scnsoft.eldermark.service.healthpartners.client;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;

public interface HpClaimClientProvider {

    IdOrganizationIdActiveAware getClient(BaseHealthPartnersRecord claim,
                                          Long communityId,
                                          ClaimProcessingContext ctx);
}
