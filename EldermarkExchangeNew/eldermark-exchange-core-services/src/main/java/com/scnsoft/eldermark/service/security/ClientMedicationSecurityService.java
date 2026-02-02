package com.scnsoft.eldermark.service.security;


import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;

public interface ClientMedicationSecurityService extends CcdEntitySecurityService {

    boolean canViewOfClient(Long clientId);

    boolean canAdd(ClientMedicationSecurityAwareEntity medication);

    boolean canEdit(Long id);

    boolean canEdit(ClientMedicationSecurityAwareEntity medication);
}
