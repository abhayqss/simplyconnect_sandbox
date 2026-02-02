package com.scnsoft.eldermark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scnsoft.eldermark.dao.EncounterNoteTypeDao;
import com.scnsoft.eldermark.entity.note.EncounterNoteType;

@Service
public class EncounterNoteTypeServiceImpl implements EncounterNoteTypeService {

    @Autowired
    private EncounterNoteTypeDao encounterNoteTypeDao;

    @Override
    public EncounterNoteType findById(Long id) {
        return encounterNoteTypeDao.findById(id).orElse(null);
    }

}
