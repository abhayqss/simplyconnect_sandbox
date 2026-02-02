package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentSignatureRequestDao extends AppJpaRepository<DocumentSignatureRequest, Long> {

    Optional<DocumentSignatureRequest> findByPdcflowSignatureId(BigInteger pdcFlowSignatureId);

    @Modifying
    @Query("update DocumentSignatureRequest set status = 'EXPIRED' where status in :statuses and dateExpires < :now")
    void expireIfStatusInAndDateExpiresLessThanNow(@Param("statuses") Collection<DocumentSignatureRequestStatus> statuses, @Param("now") Instant now);

    List<DocumentSignatureRequest> findAllByBulkRequestIdAndStatusIn(Long bulkRequestId, Collection<DocumentSignatureRequestStatus> statuses);

    List<DocumentSignatureRequest> findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(Long bulkRequestId, Long templateId, Collection<DocumentSignatureRequestStatus> statuses);
}
