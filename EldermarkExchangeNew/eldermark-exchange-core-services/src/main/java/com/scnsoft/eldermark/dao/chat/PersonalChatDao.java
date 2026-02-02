package com.scnsoft.eldermark.dao.chat;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.chat.PersonalChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PersonalChatDao extends AppJpaRepository<PersonalChat, Long> {

    List<PersonalChat> findByTwilioConversationSidIn(Collection<String> twilioConversationSids);
}
