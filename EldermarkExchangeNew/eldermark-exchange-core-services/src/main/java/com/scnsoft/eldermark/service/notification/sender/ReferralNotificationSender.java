package com.scnsoft.eldermark.service.notification.sender;

import org.springframework.stereotype.Service;

@Service
public interface ReferralNotificationSender {

    void send(Long id);

}
