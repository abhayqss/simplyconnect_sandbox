package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClientMedProfessionalDao;
import com.scnsoft.eldermark.dao.DocumentationOfDao;
import com.scnsoft.eldermark.entity.document.facesheet.ContactWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MedicalContactServiceImpl implements MedicalContactService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientMedProfessionalDao medicalProfessionalDao;

    @Autowired
    private DocumentationOfDao documentationOfDao;

    @Override
    public List<ContactWithRole> findMedicalContactsByClientId(Long clientId) {
        List<ContactWithRole> contacts = new ArrayList<>();
        var mergedClientsIds = clientService.findAllMergedClientsIds(clientId);
        contacts.addAll(medicalProfessionalDao.listByClientIds(mergedClientsIds));
        contacts.addAll(documentationOfDao.findByClient_IdIn(mergedClientsIds));
        return contacts;
    }
}
