package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersBaseRecordProcessor;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersRecordServiceImpl;
import com.scnsoft.eldermark.service.healthpartners.client.HpClaimClientProvider;
import com.scnsoft.eldermark.service.healthpartners.ctx.RxClaimProcessingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
class HealthPartnersRxClaimProcessorImpl extends HealthPartnersBaseRecordProcessor<HealthPartnersRxClaim, RxClaimProcessingContext>
        implements HealthPartnersRxClaimProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersRecordServiceImpl.class);


    @Autowired
    private HpClaimClientProvider hpClaimClientProvider;

    @Autowired
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Autowired
    private HealthPartnersNormalRxClaimProcessor normalRxClaimProcessor;

    @Autowired
    private HealthPartnersAdjustmentRxClaimProcessor adjustmentRxClaimProcessor;

    @Override
    protected RxClaimProcessingContext createContext() {
        return new RxClaimProcessingContext();
    }

    @Override
    protected Long doProcess(HealthPartnersRxClaim claim, Long communityId,
                             RxClaimProcessingContext ctx) {
        var client = hpClaimClientProvider.getClient(claim, communityId, ctx);

        claim.setDuplicate(healthPartnersRxClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                claim.getMemberIdentifier(),
                claim.getClaimNo(),
                claim.getHpFileLogId()
        ));

        if (claim.getDuplicate()) {
            logger.info("Duplicate record");
            return client.getId();
        }

        if (isAdjustmentClaim(claim)) {
            logger.info("Detected adjustment claim");
            var originalClaim = healthPartnersRxClaimDao
                    .findFirstByClaimNoAndIsSuccessTrueAndMedicationDispenseIsNotNull(claim.getClaimAdjustedFromIdentifier());

            if (shouldProcessAdjustmentAsNormalClaim(originalClaim, claim)) {
                logger.info("Adjustment claim will be processed as normal claim");
                normalRxClaimProcessor.processNormalRxClaim(claim, ctx, client);
            } else {
                adjustmentRxClaimProcessor.processAdjustmentRxClaim(originalClaim, claim, ctx);
            }
        } else {
            normalRxClaimProcessor.processNormalRxClaim(claim, ctx, client);
        }
        return client.getId();
    }

    private boolean isAdjustmentClaim(HealthPartnersRxClaim claim) {
        return StringUtils.isNotEmpty(claim.getClaimAdjustedFromIdentifier());
    }

    private boolean shouldProcessAdjustmentAsNormalClaim(HealthPartnersRxClaim originalClaim, HealthPartnersRxClaim adjustmentClaim) {
        //since we delete medication, we need to be able to create new medication if after several adjustments
        //medication was deleted and then another adjustment comes
        return originalClaim == null &&
                (adjustmentClaim.getQuantityDispensed() == null || adjustmentClaim.getQuantityDispensed().compareTo(BigDecimal.ZERO) > 0)
                && (adjustmentClaim.getDaysSupply() == null || adjustmentClaim.getDaysSupply() > 0);
    }
}
