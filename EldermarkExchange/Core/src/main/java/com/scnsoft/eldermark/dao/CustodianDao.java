package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Custodian;

public interface CustodianDao extends ResidentAwareDao<Custodian> {
    Custodian getCcdCustodian(Long residentId);
}
