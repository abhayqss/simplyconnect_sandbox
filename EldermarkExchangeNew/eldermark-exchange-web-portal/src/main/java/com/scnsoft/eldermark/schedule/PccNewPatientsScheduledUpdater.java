package com.scnsoft.eldermark.schedule;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCareSpecifications;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCareSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@ConditionalOnProperty(value = "pcc.update.newPatients.enabled", havingValue = "true")
public class PccNewPatientsScheduledUpdater {
    private static final Logger logger = LoggerFactory.getLogger(PccNewPatientsScheduledUpdater.class);

    private final PointClickCareSyncService pointClickCareSyncService;
    private final CommunityDao communityDao;
    private final PointClickCareSpecifications pointClickCareSpecifications;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public PccNewPatientsScheduledUpdater(PointClickCareSyncService pointClickCareSyncService,
                                          CommunityDao communityDao,
                                          PointClickCareSpecifications pointClickCareSpecifications,
                                          PlatformTransactionManager transactionManager) {
        this.pointClickCareSyncService = pointClickCareSyncService;
        this.communityDao = communityDao;
        this.pointClickCareSpecifications = pointClickCareSpecifications;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }


    @Scheduled(cron = "${pcc.update.newPatients.cron}", zone = "America/Chicago") //CST zone
    //intentionally non-transactional
    public void update() {
        logger.info("Starting scheduled PointClickCare new patients update");
        var communities = communityDao.findAll(pointClickCareSpecifications.pccCommunities(), IdAware.class);

        logger.info("Fetched {} communities to update", communities.size());

        for (IdAware community : communities) {
            logger.info("Updating community {}", community.getId());
            transactionTemplate.executeWithoutResult(tx -> {
                try {
                    pointClickCareSyncService.updateNewPatients(community.getId());
                    logger.info("Community {} updated", community);
                } catch (Exception ex) {
                    TransactionAspectSupport.currentTransactionStatus();
                    logger.warn("Failed to fetch new patients for community {}", community.getId(), ex);
                }
            });
        }
    }
}
