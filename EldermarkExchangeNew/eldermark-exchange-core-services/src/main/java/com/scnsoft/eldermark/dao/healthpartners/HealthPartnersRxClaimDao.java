package com.scnsoft.eldermark.dao.healthpartners;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HealthPartnersRxClaimDao extends AppJpaRepository<HealthPartnersRxClaim, Long> {

    List<HealthPartnersRxClaim> findAllByHpFileLogId(Long hpFileLogId, Sort sort);

    boolean existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
            String memberIdentifier,
            String claimNo,
            Long hpFileLogId
    );

    HealthPartnersRxClaim findFirstByClaimNoAndIsSuccessTrueAndMedicationDispenseIsNotNull(String claimNo);

    @Modifying
    @Query("update HealthPartnersRxClaim c set c.medicationDispense = null, c.medicationDeletedType = :deletedType where " +
            "c.medicationDispenseId = :medicationDispenseId")
    void setMedicationDispenseNull(@Param("deletedType") String deletedType,
                                   @Param("medicationDispenseId") Long medicationDispenseId
    );
}
