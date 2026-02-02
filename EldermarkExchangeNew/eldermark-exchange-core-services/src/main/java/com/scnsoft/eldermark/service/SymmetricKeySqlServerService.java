package com.scnsoft.eldermark.service;

import org.springframework.transaction.PlatformTransactionManager;

public interface SymmetricKeySqlServerService {

    void open();

    void open(PlatformTransactionManager platformTransactionManager);

}
