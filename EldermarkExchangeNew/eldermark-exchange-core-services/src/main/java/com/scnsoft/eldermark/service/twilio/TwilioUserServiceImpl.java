package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.projection.CareTeamRoleIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsDetailedUser;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.util.StreamUtils;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.conversations.v1.service.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
class TwilioUserServiceImpl implements TwilioUserService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioChatServiceImpl.class);

    @Value("${twilio.chat.service.sid}")
    private String chatServiceSid;

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Autowired
    private ContactService contactService;

    @Autowired
    private TwilioAttributeService attributeService;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public String toIdentity(EmployeeTwilioCommunicationsUser user) {
        return ConversationUtils.employeeIdToIdentity(user.getId());
    }

    @Override
    public EmployeeTwilioCommunicationsUser fromIdentity(String identity) {
        return findById(ConversationUtils.employeeIdFromIdentity(identity));
    }

    @Override
    public Long getEmployeeSystemRoleId(String identity) {
        var id = ConversationUtils.employeeIdFromIdentity(identity);
        var roleIdAware = contactService.findById(id, CareTeamRoleIdAware.class);
        return roleIdAware.getCareTeamRoleId();
    }

    @Override
    public List<EmployeeTwilioCommunicationsUser> fromIdentities(Collection<String> identities) {
        return findByIdIn(employeeIdsFromIdentities(identities));
    }

    @Override
    public Map<String, EmployeeTwilioCommunicationsUser> mapFromIdentities(Collection<String> identities) {
        var identityIdMap = ConversationUtils.employeeIdsFromIdentitiesMap(identities);
        var entityIdMap = findByIdIn(employeeIdsFromIdentities(identities)).stream().collect(StreamUtils.toMapOfUniqueKeys(IdAware::getId));
        return identityIdMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> entityIdMap.get(e.getValue())));
    }

    @Override
    public Map<String, EmployeeTwilioCommunicationsUser> identityUserMapByIds(Collection<Long> ids) {
        return findByIdIn(ids).stream()
                .collect(Collectors.toMap(
                        user -> ConversationUtils.employeeIdToIdentity(user.getId()),
                        Function.identity()
                ));
    }

    @Override
    public List<IdNamesAware> namesFromIdentities(Collection<String> identities) {
        var ids = employeeIdsFromIdentities(identities);
        return contactService.findAllById(ids, IdNamesAware.class);
    }

    @Override
    public List<Long> employeeIdsFromIdentities(Collection<String> identities) {
        return ConversationUtils.employeeIdsFromIdentities(identities);
    }

    @Override
    @Transactional
    public void createUserIfNotExists(EmployeeTwilioCommunicationsUser e) {
        if (!isUserExists(e)) {
            createTwilioUser(e);
        }
    }

    private boolean isUserExists(EmployeeTwilioCommunicationsUser e) {
        return StringUtils.isNotEmpty(e.getTwilioUserSid());
    }

    //NOTE that EmployeeTwilioCommunicationsUser's twilioUserSid is not updated! Make sure to reload from DB if needed.
    private User createTwilioUser(EmployeeTwilioCommunicationsUser e) {
        var user = User.creator(chatServiceSid, toIdentity(e)).create(twilioRestClient);
        contactService.updateTwilioSid(e.getId(), user.getSid());

        try {
            e.setTwilioUserSid(user.getSid());
        } catch (Exception ex) {
            logger.info("Failed to invoke setTwilioUserSid on EmployeeTwilioCommunicationsUser projection. " +
                            "Instance class is {}, error message is: {}",
                    user.getClass().getSimpleName(),
                    ex.getMessage());
        }

        return user;
    }

    @Override
    public EmployeeTwilioCommunicationsUser findById(Long id) {
        return contactService.findById(id, EmployeeTwilioCommunicationsUser.class);
    }

    @Override
    public List<EmployeeTwilioCommunicationsUser> findByIdIn(Collection<Long> ids) {
        return contactService.findAllById(ids, EmployeeTwilioCommunicationsUser.class);
    }

    @Override
    public List<IdentityListItemDto> findDtoByIdentities(Collection<String> identities) {
        return fromIdentities(identities)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public IdentityListItemDto findDtoByIdentity(String identity) {
        return convert(fromIdentity(identity));
    }

    @Override
    public IdentityListItemDto convert(EmployeeTwilioCommunicationsUser user) {
        return convert(user, toIdentity(user));
    }

    @Override
    public IdentityListItemDto convert(EmployeeTwilioCommunicationsUser user, String identity) {
        return new IdentityListItemDto(
                identity,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCommunityId(),
                user.getCommunityName(),
                user.getAvatarId(),
                user.getAvatarAvatarName(),
                EmployeeStatus.ACTIVE == user.getStatus(),
                user.getOrganizationIsChatEnabled(),
                user.getOrganizationIsVideoEnabled());
    }

    @Override
    public User fetchFromTwilio(String sidOrIdentity) {
        return User.fetcher(chatServiceSid, sidOrIdentity).fetch(twilioRestClient);
    }

    @Override
    @Transactional
    public void updateServiceConversationSid(EmployeeTwilioCommunicationsUser user, String serviceConversationSid) {
        contactService.setTwilioServiceConversation(user.getId(), serviceConversationSid);

        try {
            user.setTwilioServiceConversationSid(serviceConversationSid);
        } catch (Exception ex) {
            logger.info("Failed to invoke setTwilioServiceConversationSid on EmployeeTwilioCommunicationsUser projection. " +
                            "Instance class is {}, error message is: {}",
                    user.getClass().getSimpleName(),
                    ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void registerActiveCallChat(String sidOrIdentity, String conversationSid) {
        logger.info("Registering conversation [{}] as active call for user [{}]", conversationSid, sidOrIdentity);
        var user = fetchFromTwilio(sidOrIdentity);
        var attributes = attributeService.parse(user);

        var activeCallChats = Optional.ofNullable(attributes.getActiveCallConversationSids())
                .map(HashSet::new).orElseGet(HashSet::new);
        if (!activeCallChats.contains(conversationSid)) {
            activeCallChats.add(conversationSid);
            attributes.setActiveCallConversationSids(activeCallChats);

            User.updater(chatServiceSid, sidOrIdentity).setAttributes(attributeService.build(attributes))
                    .update(twilioRestClient);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void registerActiveCallChatAsync(Collection<String> sidOrIdentities, String conversationSid) {
        for (var sidOrIdentity : sidOrIdentities) {
            registerActiveCallChat(sidOrIdentity, conversationSid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void unregisterActiveCallChat(String sidOrIdentity, String conversationSid) {
        logger.info("Unregistering conversation [{}] as active call for user [{}]", conversationSid, sidOrIdentity);
        var user = fetchFromTwilio(sidOrIdentity);
        var attributes = attributeService.parse(user);

        var activeCallChats = Optional.ofNullable(attributes.getActiveCallConversationSids())
                .map(HashSet::new).orElseGet(HashSet::new);
        if (!activeCallChats.remove(conversationSid)) {
            logger.warn("Invalid user active call state - trying to unregister chat [{}] which is not present among User [{}] attributes",
                    conversationSid, user.getIdentity());
            return;
        }

        attributes.setActiveCallConversationSids(activeCallChats);
        User.updater(chatServiceSid, sidOrIdentity).setAttributes(attributeService.build(attributes))
                .update(twilioRestClient);
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void unregisterActiveCallChatAsync(Collection<String> sidOrIdentities, String conversationSid) {
        for (var sidOrIdentity : sidOrIdentities) {
            unregisterActiveCallChat(sidOrIdentity, conversationSid);
        }
    }

    public boolean isOnline(String sidOrIdentity) {
        if (StringUtils.isEmpty(sidOrIdentity)) {
            return false;
        }
        var twilioUser = fetchFromTwilio(sidOrIdentity);
        return Boolean.TRUE.equals(twilioUser.getIsOnline());
    }

    @Override
    public List<CommunityAndRoleAwareIdentityListItemDto> findDetailedDtoByIdentities(Collection<String> identities) {
        return detailedFromIdentities(identities)
                .stream()
                .map(this::convertDetailed)
                .collect(Collectors.toList());
    }

    @Override
    public <P> List<P> findByIdentities(Collection<String> identities, Class<P> projection) {
        return contactService.findAllById(employeeIdsFromIdentities(identities), projection);
    }

    @Override
    public List<String> findDeletedUsers() {
        var result = new ArrayList<String>();
        User.reader(chatServiceSid).read(twilioRestClient).forEach(
                twilioUser -> {
                    var identity = twilioUser.getIdentity();
                    var employeeId = ConversationUtils.employeeIdFromIdentity(identity);
                    if (!employeeDao.existsById(employeeId)) {
                        result.add(identity);
                    }
                }
        );
        return result;
    }

    @Override
    public List<String> findBrokenUsers() {
        var result = new ArrayList<String>();
        User.reader(chatServiceSid).read(twilioRestClient).forEach(
                twilioUser -> {
                    var identity = twilioUser.getIdentity();
                    var employeeId = ConversationUtils.employeeIdFromIdentity(identity);
                    var userSid = twilioUser.getSid();
                    if (!employeeDao.exists(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                            criteriaBuilder.equal(root.get(Employee_.id), employeeId),
                            criteriaBuilder.equal(root.get(Employee_.twilioUserSid), userSid)
                    )))) {
                        result.add(identity);
                    }
                }
        );
        return result;
    }

    @Override
    public void deleteIdentitiesFromTwilio(Collection<String> identities) {
        logger.info("Removing twilio users by identities {}", identities);
        identities.forEach(identity -> {
            User.deleter(chatServiceSid, identity).delete(twilioRestClient);
            logger.info("Removed user [{}]", identity);
        });
    }

    private List<EmployeeTwilioCommunicationsDetailedUser> detailedFromIdentities(Collection<String> identities) {
        return findByIdentities(identities, EmployeeTwilioCommunicationsDetailedUser.class);
    }

    private CommunityAndRoleAwareIdentityListItemDto convertDetailed(EmployeeTwilioCommunicationsDetailedUser user) {
        return convertDetailed(user, toIdentity(user));
    }

    private CommunityAndRoleAwareIdentityListItemDto convertDetailed(EmployeeTwilioCommunicationsDetailedUser user, String identity) {
        return new CommunityAndRoleAwareIdentityListItemDto(
                identity,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCommunityId(),
                user.getCommunityName(),
                user.getAvatarId(),
                user.getAvatarAvatarName(),
                EmployeeStatus.ACTIVE == user.getStatus(),
                user.getCareTeamRoleName(),
                user.getOrganizationIsChatEnabled(),
                user.getOrganizationIsVideoEnabled()
        );
    }

}
