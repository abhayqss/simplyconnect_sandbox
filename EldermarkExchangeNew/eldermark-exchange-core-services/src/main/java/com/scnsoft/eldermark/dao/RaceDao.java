package com.scnsoft.eldermark.dao;


import com.scnsoft.eldermark.entity.event.incident.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceDao extends JpaRepository<Race, Long> {

    Optional<Race> findByCodeAndCodeSystem(String code, String codeSystem);

}
