package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Organization;

public interface AppointmentFeatureNotificationService {
    void send(Organization organization);
}
