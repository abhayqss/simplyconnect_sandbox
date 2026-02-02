package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.serviceplan.ProgramSubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramSubTypeDao extends JpaRepository<ProgramSubType, Long> {

}
