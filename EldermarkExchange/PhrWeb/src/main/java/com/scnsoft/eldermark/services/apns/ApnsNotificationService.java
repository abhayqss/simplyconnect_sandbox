package com.scnsoft.eldermark.services.apns;

import com.scnsoft.eldermark.entity.phr.ApnsModel;

import java.util.concurrent.Future;

public interface ApnsNotificationService {
    
    Future<Boolean> voipPush(ApnsModel payloadmodel);
}
