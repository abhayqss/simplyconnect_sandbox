package com.scnsoft.eldermark.dao.healthpartners;


import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersFileLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HealthPartnersFileLogDao extends AppJpaRepository<HealthPartnersFileLog, Long> {


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default HealthPartnersFileLog saveInNewTx(HealthPartnersFileLog log) {
        return save(log);
    }

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("Update HealthPartnersFileLog set isSuccess=true, errorMessage = null, processedAt = CURRENT_TIMESTAMP where id = :fileLogId")
    void writeSuccessInNewTx(@Param("fileLogId") Long fileLogId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("Update HealthPartnersFileLog set isSuccess=false, errorMessage = :errorMsg, processedAt = CURRENT_TIMESTAMP where id = :fileLogId")
    void writeFailInNewTx(@Param("fileLogId") Long fileLogId, @Param("errorMsg") String errorMsg);

}
