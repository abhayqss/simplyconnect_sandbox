package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.serviceplan.ProgramType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramTypeDao extends JpaRepository<ProgramType, Long> {

}
