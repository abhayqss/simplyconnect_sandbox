package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.ccd.Custodian;

@Repository
public interface CustodianDao extends JpaRepository<Custodian, Long> {

    @Query("select c from Custodian c where c.client.id= :clientId")
    Custodian getCcdCustodian(@Param("clientId") Long clientId);

    void deleteAllByClientId(Long clientId);

}
