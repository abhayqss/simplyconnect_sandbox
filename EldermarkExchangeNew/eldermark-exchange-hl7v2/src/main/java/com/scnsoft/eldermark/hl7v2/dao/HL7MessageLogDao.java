package com.scnsoft.eldermark.hl7v2.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.hl7v2.entity.HL7MessageLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface HL7MessageLogDao extends AppJpaRepository<HL7MessageLog, Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update HL7MessageLog set " +
            "success = true, " +
            "errorMessage = null, " +
            "processedDatetime = :when, " +
            "adtMessageId = :adtMessageId, " +
            "affectedClient1Id = :affectedClient1Id " +
            " where id = :id")
    void saveProcessingSuccess(
            @Param("when") Instant when,
            @Param("adtMessageId") Long adtMessageId,
            @Param("affectedClient1Id") Long affectedClient1Id,
            @Param("id") Long id
    );

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update HL7MessageLog set " +
            "success = false , " +
            "errorMessage = :errorMessage, " +
            "processedDatetime = :when " +
            " where id = :id")
    void saveProcessingFail(
            @Param("errorMessage") String errorMessage,
            @Param("when") Instant when,
            @Param("id") Long id
    );


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update HL7MessageLog set openxdsApiSuccess = true, openxdsApiErrorMessage=null where id = :id")
    void openXdsApiSuccess(@Param("id") Long id);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update HL7MessageLog set openxdsApiSuccess = false, openxdsApiErrorMessage = :errorMessage where id = :id")
    void openXdsApiFail(@Param("id") Long id, @Param("errorMessage") String openXdsApiErrorMessage);

}
