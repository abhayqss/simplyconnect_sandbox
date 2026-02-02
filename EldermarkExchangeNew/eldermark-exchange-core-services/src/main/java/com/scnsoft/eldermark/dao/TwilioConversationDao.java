package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.chat.TwilioConversation;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface TwilioConversationDao extends AppJpaRepository<TwilioConversation, String> {

    @Modifying
    @Query("update TwilioConversation set " +
            "lastMessageIndex = case when :messageIndex > lastMessageIndex then :messageIndex else lastMessageIndex end, " +
            "lastMessageDatetime = case when :messageIndex > lastMessageIndex then :messageDateTime else lastMessageDatetime end" +
            " where twilioConversationSid = :conversationSid")
    void updateLastMessage(@Param("conversationSid") String conversationSid,
                           @Param("messageIndex") Integer messageIndex,
                           @Param("messageDateTime") Instant messageDateTime);

    @Modifying
    @Query("update TwilioConversation set friendlyConversationName = :friendlyName where twilioConversationSid = :conversationSid")
    void updateFriendlyName(@Param("conversationSid") String conversationSid,
                            @Param("friendlyName") String friendlyName);

    @Modifying
    @Query("update TwilioConversation set dateCreated = :dateCreated where twilioConversationSid = :conversationSid")
    void updateDateCreated(@Param("conversationSid") String conversationSid,
                           @Param("dateCreated") Instant dateCreated);

    @Query("Select tc from TwilioConversation tc where tc.twilioConversationSid = :twilioConversationSid")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    TwilioConversation findLockedByConversationSid(@Param("twilioConversationSid") String twilioConversationSid);

    <T> List<T> findAllByTwilioConversationSidInAndDisconnected(Collection<String> twilioConversationSids,
                                                                boolean disconnected,
                                                                Class<T> projectionClass);

    @Modifying
    @Query("update TwilioConversation set disconnected = :disconnected where twilioConversationSid = :twilioConversationSid")
    void setDisconnected(@Param("disconnected") boolean disconnected,
                         @Param("twilioConversationSid") String twilioConversationSid);

}
