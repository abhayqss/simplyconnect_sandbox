package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.mobile.dto.conversation.CreateConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;

import java.util.List;

public interface ConversationFacade {

    String generateToken();

    String create(CreateConversationDto createConversationDto);

    List<String> find(List<Long> employeeIds, String friendlyName);

    String addParticipants(EditConversationDto createConversationDto);

    void deleteParticipants(EditConversationDto createConversationDto);

    void leaveConversation(String conversationSid);

    List<IdentityListItemDto> getUsersByConversationSids(List<String> conversationSids);

    List<CommunityAndRoleAwareIdentityListItemDto> getUserDetailsByConversationSid(String conversationSid);

    void addReaction(String conversationSid, String messageSid, Long reactionId);

    void removeReaction(String conversationSid, String messageSid, Long reactionId);

    void connectConversation(String conversationSid);

    void disconnectConversation(String conversationSid);
}
