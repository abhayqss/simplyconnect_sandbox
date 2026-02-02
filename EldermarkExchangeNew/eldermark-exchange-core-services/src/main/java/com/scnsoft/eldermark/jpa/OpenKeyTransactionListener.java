package com.scnsoft.eldermark.jpa;

import com.scnsoft.eldermark.service.SymmetricKeySqlServerService;
import com.scnsoft.eldermark.util.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class OpenKeyTransactionListener implements TransactionListener, PlatformTransactionManagerAware {
    private static final Logger logger = LoggerFactory.getLogger(OpenKeyTransactionListener.class);

    private final SymmetricKeySqlServerService symmetricKeySqlServerService;

    private PlatformTransactionManager platformTransactionManager;

    public OpenKeyTransactionListener(SymmetricKeySqlServerService symmetricKeySqlServerService) {
        this.symmetricKeySqlServerService = symmetricKeySqlServerService;
    }

    @Override
    public void afterTransactionBegin(TransactionStatus txStatus) {
        logger.debug("OpenKeyTransactionListener.afterTransactionBegin param {}", txStatus);
        logger.debug("OpenKeyTransactionListener.afterTransactionBegin from Aspect {}", TransactionUtils.getCurrentTransaction());

        /*
            Although everything seems to work fine via symmetricKeySqlServerService.open(),
            using programmatic just feels safer because we are already in the middle
            of @Transactional processing
         */
        symmetricKeySqlServerService.open(platformTransactionManager);
    }

    @Override
    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }
}
