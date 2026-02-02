package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.FunctionalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionalStatusDao extends JpaRepository<FunctionalStatus, Long> {

    @Query("select fs from FunctionalStatus fs where fs.client.id in (:clientIds)")
    List<FunctionalStatus> listByClientIds(@Param("clientIds") List<Long> clientIds);


    void deleteAllByClientId(Long clientId);
}
