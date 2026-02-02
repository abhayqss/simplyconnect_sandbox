package com.scnsoft.eldermark.service.assessment.arizona;

import com.scnsoft.eldermark.dto.notification.ArizonaMatrixMonthlyNotificationDto;

import java.time.Instant;
import java.util.List;

public interface ArizonaMatrixMonthlyNotificationService {
    List<ArizonaMatrixMonthlyNotificationDto> generateNotifications(Long organizationId, Instant fromDate, Instant toDate);
}
