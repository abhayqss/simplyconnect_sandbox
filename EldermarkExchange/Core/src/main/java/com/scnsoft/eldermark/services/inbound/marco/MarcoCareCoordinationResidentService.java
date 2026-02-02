package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.entity.CareCoordinationResident;

import java.util.List;

public interface MarcoCareCoordinationResidentService {

    List<CareCoordinationResident> getPatientDetailsByIdentityFields(MarcoDocumentMetadata metadata);
}
