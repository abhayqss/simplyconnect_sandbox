package com.scnsoft.eldermark.therap.dao;

import com.scnsoft.eldermark.therap.entity.ResidentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentMappingDao extends JpaRepository<ResidentMapping, Long> {

    List<ResidentMapping> findAllBySourceFirstNameAndSourceLastName(String fn, String ln);
}
