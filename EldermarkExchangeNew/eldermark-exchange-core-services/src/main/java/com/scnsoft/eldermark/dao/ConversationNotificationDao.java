package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.video.ConversationNotification;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationNotificationDao extends AppJpaRepository<ConversationNotification, Long> {
}
