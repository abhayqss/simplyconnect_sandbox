package com.scnsoft.eldermark.services.carecoordination;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.carecoordination.EncounterNoteTypeDao;
import com.scnsoft.eldermark.entity.EncounterNoteType;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

@Service
@Transactional
public class EncounterNoteTypeServiceImpl implements EncounterNoteTypeService {

    private Map<Long, EncounterNoteType> mapping;

    private List<KeyValueDto> cache;

    @Autowired
    EncounterNoteTypeDao encounterTypeDao;

    @PostConstruct
    protected void postConstruct() {
        List<EncounterNoteType> entList = encounterTypeDao.findAll();

        cache = new ArrayList<>();
        mapping = new LinkedHashMap<>();

        for (EncounterNoteType encounterNoteType : entList) {
            cache.add(new KeyValueDto(encounterNoteType.getId(), encounterNoteType.getDescription()));
            mapping.put(encounterNoteType.getId(), encounterNoteType);
        }
    }

    @Override
    public EncounterNoteType getById(Long id) {
        return mapping.get(id);
    }

    @Override
    public List<KeyValueDto> getAllEncounterNoteTypes() {
        return new ArrayList<>(cache);
    }

}