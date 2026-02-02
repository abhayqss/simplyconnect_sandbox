package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.note.NoteSubType;

public interface NoteSubTypeDao extends JpaRepository<NoteSubType, Long> {
    
}
