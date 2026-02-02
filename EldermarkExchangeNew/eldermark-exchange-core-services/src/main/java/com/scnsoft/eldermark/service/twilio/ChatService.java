package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.twilio.chat.SystemMessage;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioSecurityFieldsAware;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dto.conversation.ConversationTwilioDbSyncCheckResult;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.twilio.media.Media;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;

import java.util.*;

public interface ChatService {
    //todo add disconnected chat checks
    boolean isChatEnabled();

    boolean isChatEnabled(Employee e);

    boolean isChatEnabled(Long employeeId);

    boolean isChatEnabled(EmployeeTwilioSecurityFieldsAware e);

    String generateToken(Long employeeId);

    String generateToken(Employee e);

    String createChat(Set<Long> employeeIds, Long creatorId, String friendlyName, Long participatingClientId, Long incidentReportId);

    List<String> findConversations(List<Long> employeeIds, String friendlyName, Long selfId);

    Optional<String> findPersonalChatSid(Long employeeId1, Long employeeId2);

    String addParticipants(String conversationSid, String friendlyName, Set<Long> employeeIds, Long selfId, Long participatingClientId);

    Map<String, Participant> getConversationParticipantMap(String conversationSid);

    void deleteParticipants(String conversationSid, Set<Long> employeeIds, Long selfId);

    void leaveConversation(String conversationSid, Long selfId);

    Message writeSystemMessage(String conversationSid, SystemMessage systemMessage, String body);

    Message sendTextMessageWithLinks(String conversationSid, String author, String textMessage);

    List<IdentityListItemDto> findEmployeeChatUsersByConversationSids(List<String> conversationSids, Long selfId);

    Set<String> findActiveUserIdentitiesByConversationSids(List<String> conversationSids, Long selfId);

    Iterable<Participant> getChatParticipants(String conversationSid);

    Set<String> getPersonalChatIdentities(List<String> conversationSids, String selfIdentity);

    Set<String> getGroupChatIdentities(List<String> conversationSids, String selfIdentity);

    boolean isGroupChatParticipant(String conversationSid, Long employeeId);

    void joinIncidentReportConversation(Long incidentReportId, Long selfId);

    void joinConversation(String conversationSid, EmployeeTwilioCommunicationsUser self, Integer maxParticipantQty);

    boolean existsConversationBetweenAnyAndClient(Collection<Long> employeeIds, Long clientId);

    String getServiceConversationSid(Employee employee);

    Message sendServiceMessage(EmployeeTwilioCommunicationsUser user, ServiceMessage serviceMessage,
                               String devicePushNotificationTokenToExclude);

    String getFriendlyName(String conversationSid);

    void updateFriendlyName(String conversationSid, String friendlyName);

    void registerActiveCallChat(String conversationSid, String roomSid);

    void unregisterActiveCallChat(String conversationSid);

    boolean isAnyChatParticipant(String conversationSid, Collection<Long> employeeIds);

    Media fetchMedia(String mediaSid);

    byte[] downloadMediaContent(Media media);

    String sendMediaMessage(String conversationSid, String author, String fileName, String mediaType, byte[] bytes);

    boolean existsConversationBetweenAnyAndEmployee(Collection<Long> employeeIds, Long employeeId);

    Message fetchMessage(String conversationSid, String messageSid);

    List<CommunityAndRoleAwareIdentityListItemDto> findEmployeeChatUserDetailsByConversationSid(String conversationSid, Long selfId);

    Optional<String> findOwnerIdentity(String conversationSid);

    void deleteChatHistoryForIdentities(Collection<String> identities);

    String getPersonalOrCreateConversation(Set<Long> chatParticipantsIds, Long actorId, Long participatingClientId);

    void addReaction(String conversationSid, String messageSid, Long reactionId, Long actorId);

    void removeReaction(String conversationSid, String messageSid, Long reactionId, Long actorId);

    void updateConversationsDisconnection(Set<String> conversationSid, boolean disconnect, boolean forceUpdate);

    boolean isConnected(String conversationSid);

    List<ConversationTwilioDbSyncCheckResult> findBrokenChats();

    void fixBrokenChats(List<String> conversationSids, boolean logActionsOnly);
}
