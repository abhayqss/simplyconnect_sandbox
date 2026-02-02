package com.scnsoft.eldermark.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by averazub on 10/27/2016.
 */
@Component
public interface DbUtilsDao {
    @Transactional
    public void openCertificate();
    @Transactional
    public void closeCertificate();
}
