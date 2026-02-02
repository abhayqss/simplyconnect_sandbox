package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.community.Handset;

@Repository
public interface HandsetDao extends JpaRepository<Handset, Long> {

  // TOSEE again maybe not required  @Query("select fs from FunctionalStatus fs where fs.resident.id = :clientId")
  //  Handset getClientFunctionalStatus(@Param("clientId") Long clientId); 
}
