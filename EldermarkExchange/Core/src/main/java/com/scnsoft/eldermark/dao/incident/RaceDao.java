package com.scnsoft.eldermark.dao.incident;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.incident.Race;

@Repository
public interface RaceDao extends JpaRepository<Race, Long>{
    
    List<Race> findByOrderByName();

}
