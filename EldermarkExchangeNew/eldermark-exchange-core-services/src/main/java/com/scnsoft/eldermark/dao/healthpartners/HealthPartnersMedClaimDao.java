package com.scnsoft.eldermark.dao.healthpartners;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface HealthPartnersMedClaimDao extends AppJpaRepository<HealthPartnersMedClaim, Long> {

    List<HealthPartnersMedClaim> findAllByHpFileLogId(Long hpFileLogId, Sort sort);

    boolean existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
            String memberIdentifier,
            String claimNo,
            Long hpFileLogId
    );
}
