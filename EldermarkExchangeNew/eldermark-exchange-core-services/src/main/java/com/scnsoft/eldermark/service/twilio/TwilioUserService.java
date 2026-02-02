package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.twilio.rest.conversations.v1.service.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TwilioUserService {

    String toIdentity(EmployeeTwilioCommunicationsUser user);

    EmployeeTwilioCommunicationsUser fromIdentity(String identity);

    Long getEmployeeSystemRoleId(String identity);

    List<EmployeeTwilioCommunicationsUser> fromIdentities(Collection<String> identities);

    Map<String, EmployeeTwilioCommunicationsUser> mapFromIdentities(Collection<String> identities);

    Map<String, EmployeeTwilioCommunicationsUser> identityUserMapByIds(Collection<Long> ids);

    List<IdNamesAware> namesFromIdentities(Collection<String> identities);

    List<Long> employeeIdsFromIdentities(Collection<String> identities);

    void createUserIfNotExists(EmployeeTwilioCommunicationsUser user);

    EmployeeTwilioCommunicationsUser findById(Long id);

    List<EmployeeTwilioCommunicationsUser> findByIdIn(Collection<Long> id);

    List<IdentityListItemDto> findDtoByIdentities(Collection<String> identities);

    IdentityListItemDto findDtoByIdentity(String identity);

    IdentityListItemDto convert(EmployeeTwilioCommunicationsUser user);

    IdentityListItemDto convert(EmployeeTwilioCommunicationsUser user, String identity);

    User fetchFromTwilio(String sidOrIdentity);

    void updateServiceConversationSid(EmployeeTwilioCommunicationsUser user, String serviceConversationSid);

    void registerActiveCallChat(String sidOrIdentity, String conversationSid);

    void registerActiveCallChatAsync(Collection<String> sidOrIdentities, String conversationSid);

    void unregisterActiveCallChat(String sidOrIdentity, String conversationSid);

    void unregisterActiveCallChatAsync(Collection<String> sidOrIdentities, String conversationSid);

    boolean isOnline(String sidOrIdentity);

    List<CommunityAndRoleAwareIdentityListItemDto> findDetailedDtoByIdentities(Collection<String> identities);

    <P> List<P> findByIdentities(Collection<String> identities, Class<P> projection);

    List<String> findDeletedUsers();

    List<String> findBrokenUsers();

    void deleteIdentitiesFromTwilio(Collection<String> identities);

}
