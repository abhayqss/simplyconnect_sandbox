package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.PushNotificationRegistration;

public interface DeviceTokenAndAppNameAware extends DeviceTokenAware {
    PushNotificationRegistration.Application getAppName();
}
