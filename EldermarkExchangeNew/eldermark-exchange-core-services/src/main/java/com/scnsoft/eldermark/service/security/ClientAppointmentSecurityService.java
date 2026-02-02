package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentSecurityFieldsAware;

public interface ClientAppointmentSecurityService {

    boolean canViewList();

    boolean canAdd(ClientAppointmentSecurityFieldsAware appointmentSecurityFieldsAware);

    boolean canView(Long appointmentId);

    boolean canEdit(Long appointmentId);

    boolean canAddInOrganization(Long organizationId);

    boolean canComplete(Long appointmentId);
}
