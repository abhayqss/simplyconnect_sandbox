package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.document.facesheet.ContactWithRole;

import java.util.List;

public interface MedicalContactService {

    List<ContactWithRole> findMedicalContactsByClientId(Long clientId);
}
