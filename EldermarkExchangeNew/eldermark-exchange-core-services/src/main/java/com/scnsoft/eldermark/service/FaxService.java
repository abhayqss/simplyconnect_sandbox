package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;

import java.util.concurrent.Future;

public interface FaxService {

    Future<Boolean> send(BaseFaxNotificationDto faxDto, byte[] content);

    boolean sendAndWait(BaseFaxNotificationDto faxDto, byte[] content);

}
