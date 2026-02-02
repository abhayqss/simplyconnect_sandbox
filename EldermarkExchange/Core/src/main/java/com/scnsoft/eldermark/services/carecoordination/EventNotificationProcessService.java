package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;

/**
 * Created by pzhurba on 02-Dec-15.
 */

public interface EventNotificationProcessService {
    void processNotification(final EventNotification notification, EventDto eventDto);
    String patientInitials(CareCoordinationResident resident, NotificationType notificationType);
}
