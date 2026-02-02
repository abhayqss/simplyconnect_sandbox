package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.AffiliatedNotificationDto;

import java.util.Collection;

public interface AffiliatedRelationshipNotificationService {

    void sentNotification(AffiliatedNotificationDto dto, Collection<Long> recipientIds);
}
