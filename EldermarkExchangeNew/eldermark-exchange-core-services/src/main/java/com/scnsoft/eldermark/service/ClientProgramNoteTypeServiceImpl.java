package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClientProgramNoteTypeDao;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClientProgramNoteTypeServiceImpl implements ClientProgramNoteTypeService {

    @Autowired
    private ClientProgramNoteTypeDao clientProgramNoteTypeDao;

    @Override
    public ClientProgramNoteType findById(Long id) {
        return clientProgramNoteTypeDao.findById(id).orElseThrow();
    }
}
