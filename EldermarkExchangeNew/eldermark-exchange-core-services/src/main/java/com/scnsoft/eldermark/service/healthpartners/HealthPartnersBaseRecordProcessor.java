package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;
import com.scnsoft.eldermark.util.TransactionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class HealthPartnersBaseRecordProcessor<T extends BaseHealthPartnersRecord, CTX extends ClaimProcessingContext>
        implements HealthPartnersRecordProcessor<T> {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersBaseRecordProcessor.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(T claim, Long communityId) {
        var ctx = createContext();
        Long clientId = null;
        try {
            clientId = doProcess(claim, communityId, ctx);
            claim.setSuccess(true);
        } catch (Exception e) {
            logger.warn("Exception during processing claim ", e);
            claim.setErrorMessage(ExceptionUtils.getStackTrace(e));
            claim.setSuccess(false);
            claim.setProcessingException(e);
            var tx = TransactionUtils.getCurrentTransaction();
            if (tx != null) {
                tx.setRollbackOnly();
            }
        }

        if (clientId != null && claim.isSuccess()) {
            claim.setClientId(clientId);
            claim.setUpdateTypes(ctx.getUpdateTypes());
        }
    }

    protected abstract CTX createContext();

    protected abstract Long doProcess(T record, Long communityId, CTX ctx);
}
