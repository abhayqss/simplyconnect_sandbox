package com.scnsoft.eldermark.jpa;

import org.springframework.transaction.PlatformTransactionManager;

public interface PlatformTransactionManagerAware {

    void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager);
}
