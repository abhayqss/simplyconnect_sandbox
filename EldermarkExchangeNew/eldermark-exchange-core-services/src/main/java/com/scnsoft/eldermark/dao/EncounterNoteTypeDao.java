package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.note.EncounterNoteType;

@Repository
public interface EncounterNoteTypeDao extends JpaRepository<EncounterNoteType, Long> {

}
