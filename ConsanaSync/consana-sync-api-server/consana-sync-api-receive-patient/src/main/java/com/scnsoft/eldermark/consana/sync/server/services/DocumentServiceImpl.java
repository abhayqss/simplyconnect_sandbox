package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.DocumentDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Document;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional(noRollbackFor = Exception.class)
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentDao documentDao;

    @Override
    public Set<String> getAllDocumentConsanaIds(Resident resident) {
        return documentDao.findAllByConsanaMapIdIsNotNullAndClientLegacyIdAndClientOrganizationAlternativeId(resident.getLegacyId(), resident.getDatabase().getAlternativeId())
                .stream()
                .map(Document::getConsanaMapId)
                .collect(Collectors.toSet());
    }
}
