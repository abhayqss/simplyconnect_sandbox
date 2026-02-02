package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dto.conversation.ConversationParticipantAccessibilityDto;
import com.scnsoft.eldermark.dto.conversation.ConversationTwilioDbSyncCheckResult;
import com.scnsoft.eldermark.dto.conversation.CreateConversationDto;
import com.scnsoft.eldermark.dto.conversation.EditConversationDto;

import java.util.Collection;
import java.util.List;

public interface ConversationFacade {

    String generateToken();

    String create(CreateConversationDto createConversationDto);

    String addParticipants(EditConversationDto createConversationDto);

    void deleteParticipants(EditConversationDto createConversationDto);

    void leaveConversation(String conversationSid);

    List<IdentityListItemDto> getUsersByConversationSids(List<String> conversationSids);

    ConversationParticipantAccessibilityDto getParticipantAccessibility(ConversationParticipatingAccessibilityFilter filter);

    void addReaction(String conversationSid, String messageSid, Long reactionId);

    void removeReaction(String conversationSid, String messageSid, Long reactionId);

    List<String> findDeletedUsers();

    List<String> findBrokenUsers();

    List<ConversationTwilioDbSyncCheckResult> findBrokenChats();

    void fixBrokenChats(List<String> conversationSids, boolean logActionsOnly);

    void deleteTwilioUsers(Collection<String> identities);

    void connectConversation(String conversationSid);

    void disconnectConversation(String conversationSid);

}
