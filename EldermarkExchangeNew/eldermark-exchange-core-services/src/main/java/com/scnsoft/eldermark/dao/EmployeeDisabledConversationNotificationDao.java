package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.video.ConversationNotificationType;
import com.scnsoft.eldermark.entity.video.EmployeeDisabledConversationNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDisabledConversationNotificationDao extends JpaRepository<EmployeeDisabledConversationNotification, Long> {

    boolean existsByEmployeeIdAndChannelAndType(Long employeeId, NotificationType channel, ConversationNotificationType type);

}
