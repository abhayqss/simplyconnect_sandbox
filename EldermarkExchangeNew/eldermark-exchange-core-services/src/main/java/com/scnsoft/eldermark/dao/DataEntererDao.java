package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.ccd.DataEnterer;

@Repository
public interface DataEntererDao extends JpaRepository<DataEnterer, Long> {

    @Query("select d from DataEnterer d where d.client.id = :clientId")
    DataEnterer getCcdDataEnterer(@Param("clientId") Long clientId);

    void deleteAllByClientId(Long clientId);
}


