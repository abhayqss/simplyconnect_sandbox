package com.scnsoft.eldermark.service.healthpartners.processor.med;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersMedClaimDao;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersBaseRecordProcessor;
import com.scnsoft.eldermark.service.healthpartners.client.HpClaimClientProvider;
import com.scnsoft.eldermark.service.healthpartners.ctx.ClaimProcessingContext;
import com.scnsoft.eldermark.service.healthpartners.problem.HpClaimProblemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//todo unit test?
@Service
class HealthPartnersMedClaimProcessorImpl
        extends HealthPartnersBaseRecordProcessor<HealthPartnersMedClaim, ClaimProcessingContext>
        implements HealthPartnersMedClaimProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersMedClaimProcessorImpl.class);

    @Autowired
    private HpClaimClientProvider hpClaimClientProvider;

    @Autowired
    private HpClaimProblemFactory hpClaimProblemFactory;

    @Autowired
    private HealthPartnersMedClaimDao healthPartnersMedClaimDao;

    @Override
    protected ClaimProcessingContext createContext() {
        return new ClaimProcessingContext();
    }

    @Override
    protected Long doProcess(HealthPartnersMedClaim claim, Long communityId, ClaimProcessingContext ctx) {
        var client = hpClaimClientProvider.getClient(claim, communityId, ctx);

        claim.setDuplicate(healthPartnersMedClaimDao.existsByMemberIdentifierAndClaimNoAndHpFileLogIdNotAndIsSuccessTrue(
                claim.getMemberIdentifier(),
                claim.getClaimNo(),
                claim.getHpFileLogId()
        ));

        if (claim.getDuplicate()) {
            logger.info("Duplicate record");
            return client.getId();
        }

        var problem = hpClaimProblemFactory.createProblem(claim, client);

        var observation = problem.getProblemObservations().get(0);
        claim.setProblemObservation(observation);
        claim.setProblemObservationId(observation.getId());

        ctx.getUpdateTypes().add(ResidentUpdateType.PROBLEM);
        return client.getId();
    }
}
