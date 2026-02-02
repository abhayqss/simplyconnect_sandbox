package com.scnsoft.eldermark.service.healthpartners.problem;

import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;

public interface HpClaimProblemFactory {

    Problem createProblem(HealthPartnersMedClaim claim, IdOrganizationIdActiveAware client);

}
