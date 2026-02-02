package com.scnsoft.eldermark.dao;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by averazub on 10/27/2016.
 */
@Component
public class DbUtilsDaoImpl implements DbUtilsDao {
    @PersistenceContext
    private EntityManager entityManager;


    public void openCertificate() {
        if (entityManager.isOpen()) {
            entityManager.createNativeQuery("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1").executeUpdate();
        }
    }

    public void closeCertificate() {
        if (entityManager.isOpen()) {
            entityManager.createNativeQuery("CLOSE SYMMETRIC KEY SymmetricKey1").executeUpdate();
        }
    }


}
