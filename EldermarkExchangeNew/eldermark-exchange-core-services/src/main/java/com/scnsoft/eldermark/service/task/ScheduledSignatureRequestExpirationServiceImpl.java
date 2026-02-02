package com.scnsoft.eldermark.service.task;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@ConditionalOnProperty(value = "signature.request.expiration.scheduled.enabled", havingValue = "true")
public class ScheduledSignatureRequestExpirationServiceImpl implements ScheduledSignatureRequestExpirationService {

    @Autowired
    private DocumentSignatureRequestDao documentSignatureRequestDao;

    @Override
    @Transactional
    @Scheduled(cron = "${signature.request.expiration.scheduled.cron}")
    public void updateStatusOfExpiredSignatureRequests() {
        documentSignatureRequestDao.expireIfStatusInAndDateExpiresLessThanNow(
                DocumentSignatureRequestStatus.signatureRequestSentStatuses(), 
                Instant.now()
        );
    }
}
