package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.lab.LabResearchOrder;

public interface LabResearchNotificationService {

    void prepareResultReceivedNotification(LabResearchOrder labResearchOrder);
}
