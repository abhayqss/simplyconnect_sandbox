package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.NoteSubTypeDao;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoteSubTypeServiceImpl implements NoteSubTypeService {

    @Autowired
    private NoteSubTypeDao noteSubTypeDao;

    private Map<Long, NoteSubType> noteTypes;
    private Map<String, NoteSubType> noteTypesByCode;

    @PostConstruct
    protected void postConstruct() {
        var subTypes = noteSubTypeDao.findAll();
        noteTypes = subTypes.stream().collect(Collectors.toMap(NoteSubType::getId, Function.identity()));
        noteTypesByCode = subTypes.stream().collect(Collectors.toMap(NoteSubType::getCode, Function.identity()));
    }

    @Override
    public NoteSubType findById(Long subTypeId) {
        return noteTypes.get(subTypeId);
    }

    @Override
    public NoteSubType findByCode(String code) {
        return noteTypesByCode.get(code);
    }
}
