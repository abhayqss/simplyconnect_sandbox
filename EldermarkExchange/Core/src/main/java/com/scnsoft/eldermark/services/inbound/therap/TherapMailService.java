package com.scnsoft.eldermark.services.inbound.therap;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapTotalProcessingSummary;

public interface TherapMailService {

    void sendEmailNotifications(TherapTotalProcessingSummary summary);

}
