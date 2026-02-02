package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.note.Note;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface CustomNoteDao {
    Map<Long, List<Long>> findGroupNoteClientIds(Specification<Note> specification);
}