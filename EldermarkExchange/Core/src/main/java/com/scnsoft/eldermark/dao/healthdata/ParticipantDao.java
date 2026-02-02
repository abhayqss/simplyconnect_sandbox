package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ParticipantDao extends JpaRepository<Participant, Long> {

}
