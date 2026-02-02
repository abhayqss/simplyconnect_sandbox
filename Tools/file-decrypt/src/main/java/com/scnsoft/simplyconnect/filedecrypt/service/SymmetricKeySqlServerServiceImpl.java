package com.scnsoft.simplyconnect.filedecrypt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SymmetricKeySqlServerServiceImpl implements SymmetricKeySqlServerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void open() {
        openKey();
    }

    private void openKey() {
        jdbcTemplate.execute("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1");
    }

}
