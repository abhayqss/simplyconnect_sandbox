package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;

public interface HealthPartnersRecordService {

    void processRxClaim(HealthPartnersRxClaim claim, Long communityId);

    void processTermedMember(HealthPartnersTermedMember termedMember, Long communityId);

    void processMedClaim(HealthPartnersMedClaim claim, Long communityId);
}
