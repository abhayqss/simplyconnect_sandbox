package com.scnsoft.eldermark.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class SymmetricKeySqlServerServiceImpl implements SymmetricKeySqlServerService {
    private static final Logger logger = LoggerFactory.getLogger(SymmetricKeySqlServerServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void open() {
        openKey();
    }

    @Override
    public void open(PlatformTransactionManager platformTransactionManager) {
        var template = new TransactionTemplate(platformTransactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);
        template.executeWithoutResult(txStatus -> {
            openKey();
        });
    }

    private void openKey() {
        logger.debug("Opening symmetric key");
        jdbcTemplate.execute("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1");
        logger.debug("Opened symmetric key");
    }
}
