package com.scnsoft.eldermark.service.twilio;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.beans.ConversationType;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.projection.entity.IncidentReportClientIdCommunityIdAwareEntity;
import com.scnsoft.eldermark.beans.twilio.attributes.ConversationAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.MessageAttributes;
import com.scnsoft.eldermark.beans.twilio.attributes.MessageReaction;
import com.scnsoft.eldermark.beans.twilio.attributes.ParticipantAttributes;
import com.scnsoft.eldermark.beans.twilio.chat.SystemMessage;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.beans.twilio.user.*;
import com.scnsoft.eldermark.dao.IncidentReportDao;
import com.scnsoft.eldermark.dao.TwilioConversationDao;
import com.scnsoft.eldermark.dao.TwilioParticipantReadMessageStatusDao;
import com.scnsoft.eldermark.dao.chat.GroupChatParticipantHistoryDao;
import com.scnsoft.eldermark.dao.chat.PersonalChatDao;
import com.scnsoft.eldermark.dao.specification.ChatSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.dto.conversation.ConversationTwilioDbSyncCheckResult;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.chat.*;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport_;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.service.twilio.media.Media;
import com.scnsoft.eldermark.service.twilio.notification.ConversationNotificationService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.StreamUtils;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.Role;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;
import com.twilio.rest.conversations.v1.service.conversation.Webhook;
import com.twilio.rest.conversations.v1.service.user.UserConversation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class TwilioChatServiceImpl implements ChatService, LastMessageSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(TwilioChatServiceImpl.class);

    private final Set<SystemMessage> IGNORED_FOR_MISSED_CHAT_SYSTEM_MESSAGES = EnumSet.of(
            SystemMessage.CALL_MISSED,
            SystemMessage.CALL_END
    );

    @Value("${twilio.chat.service.sid}")
    private String chatServiceSid;

    @Value("${twilio.chat.enabled}")
    private boolean isChatEnabled;

    @Value("${twilio.conversation.webhook.filters}")
    private List<String> conversationWebhookFilters;

    @Value("${twilio.conversation.webhook.path}")
    private String conversationWebhookPath;

    @Value("${twilio.conversation.role.read-only.name}")
    private String conversationReadOnlyRoleName;

    @Value("${twilio.conversation.role.channel-user.name}")
    private String conversationUserRoleName;

    @Autowired
    private TwilioAccessTokenService tokenService;

    @Autowired
    private GroupChatParticipantHistoryDao groupChatParticipantHistoryDao;

    @Autowired
    private PersonalChatDao personalChatDao;

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Autowired
    private TwilioAttributeService attributeService;

    @Autowired
    private ServiceMessageEncoder serviceMessageEncoder;

    @Autowired
    private TwilioUserService twilioUserService;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private IncidentReportDao incidentReportDao;

    @Autowired
    private ChatSpecificationGenerator chatSpecificationGenerator;

    @Autowired
    private ConversationNotificationService conversationNotificationService;

    @Autowired
    private TwilioConversationDao twilioConversationDao;

    @Autowired
    private TwilioParticipantReadMessageStatusDao twilioParticipantReadMessageStatusDao;

    private final TransactionTemplate transactionTemplate;

    private String readOnlyConversationRoleSid;
    private String conversationUserRoleSid;

    @Autowired
    public TwilioChatServiceImpl(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @PostConstruct
    public void fetchRoleSids() {
        if (!isChatEnabled) {
            return;
        }
        var roles = Role.reader(chatServiceSid)
                .read(twilioRestClient);

        for (var role : roles) {
            if (Role.RoleType.CONVERSATION.equals(role.getType())) {
                if (conversationReadOnlyRoleName.equals(role.getFriendlyName())) {
                    readOnlyConversationRoleSid = role.getSid();
                    logger.info("Fetched roleSid for {}", conversationReadOnlyRoleName);
                } else if (conversationUserRoleName.equals(role.getFriendlyName())) {
                    conversationUserRoleSid = role.getSid();
                    logger.info("Fetched roleSid for {}", conversationUserRoleName);
                }
            }
        }

        if (StringUtils.isEmpty(readOnlyConversationRoleSid)) {
            throw new RuntimeException("Failed to resolve role sid for twilio conversation role " + conversationReadOnlyRoleName);
        }

        if (StringUtils.isEmpty(conversationUserRoleSid)) {
            throw new RuntimeException("Failed to resolve role sid for twilio conversation role " + conversationUserRoleName);
        }
    }

    @Override
    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    @Override
    public boolean isChatEnabled(Employee e) {
        return isChatEnabled(new EntityBackedEmployeeTwilioCommunicationsUser(e));
    }

    @Override
    public boolean isChatEnabled(Long employeeId) {
        return isChatEnabled(twilioUserService.findById(employeeId));
    }

    @Override
    public boolean isChatEnabled(EmployeeTwilioSecurityFieldsAware e) {
        return isChatEnabled &&
                Boolean.TRUE.equals(e.getOrganizationIsChatEnabled())
                && isActiveUser(e);
    }

    @Override
    public String generateToken(Long employeeId) {
        var user = twilioUserService.findById(employeeId);
        return generateToken(user);
    }

    @Override
    public String generateToken(Employee e) {
        return generateToken(new EntityBackedEmployeeTwilioCommunicationsUser(e));
    }

    private String generateToken(EmployeeTwilioCommunicationsUser user) {
        validateChatEnabledForActor(user);
        twilioUserService.createUserIfNotExists(user);
        var identity = twilioUserService.toIdentity(user);
        return tokenService.generateChatToken(identity, chatServiceSid);
    }

    private void validateChatEnabled(EmployeeTwilioCommunicationsUser employee) {
        if (!isChatEnabled(employee)) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE,
                    "Chat functionality is not available for employee [" + employee.getId() + "]");
        }
    }

    private void validateChatEnabledForActor(EmployeeTwilioCommunicationsUser employee) {
        if (!isChatEnabled(employee)) {
            throw new BusinessException(BusinessExceptionType.CHAT_DISABLED);
        }
    }

    private boolean isActiveUser(EmployeeTwilioSecurityFieldsAware e) {
        return EmployeeStatus.ACTIVE == e.getStatus();
    }

    @Override
    public String createChat(Set<Long> employeeIds, Long creatorId, String friendlyName,
                             Long participatingClientId, Long incidentReportId) {
        if (!employeeIds.contains(creatorId)) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "Creator must be member of chat");
        }

        var creator = twilioUserService.findById(creatorId);
        validateChatEnabledForActor(creator);

        if (incidentReportId != null) {
            incidentReportService.validateHasNoConversation(incidentReportId);
        }

        if (employeeIds.size() <= 1) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "Chat can contain 2+ members only.");
        }

        var employees = twilioUserService.findByIdIn(employeeIds);

        if (employees.size() != employeeIds.size()) {
            throw new BusinessException("Failed to fetch contact.");
        }

        employees.forEach(this::validateChatEnabled);

        String associatedEmployeeIdentity = null;
        if (participatingClientId != null) {
            associatedEmployeeIdentity = findAssociatedContactIdentity(employees, participatingClientId);
        }

        employees.forEach(twilioUserService::createUserIfNotExists);

        if (employees.size() == 2 && incidentReportId == null) {
            return createPersonalChat(employees, participatingClientId, associatedEmployeeIdentity);
        }

        return createGroupChat(employees, creator, friendlyName,
                participatingClientId, associatedEmployeeIdentity, incidentReportId);
    }

    @Override
    public List<String> findConversations(List<Long> employeeIds, String friendlyName, Long selfId) {

        var self = twilioUserService.findById(selfId);
        validateChatEnabledForActor(self);

        var conversations = getUserConversations(selfId);

        var conversationIdToAttributesStream = StreamUtils.stream(conversations)
                .map(c -> new Pair<>(c.getConversationSid(), attributeService.parse(c)));

        if (CollectionUtils.isNotEmpty(employeeIds)) {
            var employeesIdentities = ConversationUtils.employeeIdsToIdentity(employeeIds);
            conversationIdToAttributesStream = conversationIdToAttributesStream.filter(p -> {
                var attributes = p.getSecond();
                var participantIdentities = attributes.getParticipantIdentities();
                return CollectionUtils.isEqualCollection(employeesIdentities, participantIdentities);
            });
        }

        if (StringUtils.isNotEmpty(friendlyName)) {
            conversationIdToAttributesStream = conversationIdToAttributesStream.filter(p -> {
                var attributes = p.getSecond();
                return friendlyName.equals(attributes.getFriendlyName());
            });
        }

        return conversationIdToAttributesStream
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    private Iterable<UserConversation> getUserConversations(Long selfId) {
        var selfIdentity = ConversationUtils.employeeIdToIdentity(selfId);
        return UserConversation.reader(chatServiceSid, selfIdentity)
                .read(twilioRestClient);
    }

    private String createPersonalChat(Collection<EmployeeTwilioCommunicationsUser> employees, Long participatingClientId,
                                      String associatedEmployeeIdentity) {
        var it = employees.iterator();
        var employee1 = it.next();
        var employee2 = it.next();
        var e1Identity = twilioUserService.toIdentity(employee1);
        var e2Identity = twilioUserService.toIdentity(employee2);

        if (personalChatDao.count(chatSpecificationGenerator.personalChatBetweenUsers(e1Identity, e2Identity)) > 0) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "1-to-1 chat already exists");
        }

        var conversation = createConversationResource(ConversationType.PERSONAL, Set.of(e1Identity, e2Identity),
                participatingClientId, associatedEmployeeIdentity);
        var participant1 = Participant.creator(chatServiceSid, conversation.getSid()).setIdentity(e1Identity)
                .create(twilioRestClient);
        var participant2 = Participant.creator(chatServiceSid, conversation.getSid()).setIdentity(e2Identity)
                .create(twilioRestClient);

        createConversationWebhook(conversation.getSid());

        createParticipantReadMessageStatus(participant1);
        createParticipantReadMessageStatus(participant2);

        var personalChat = new PersonalChat();
        personalChat.setTwilioConversationSid(conversation.getSid());
        personalChat.setTwilioIdentity1(e1Identity);
        personalChat.setTwilioIdentity2(e2Identity);
        if (participatingClientId != null) {
            personalChat.setClient1Id(e1Identity.equals(associatedEmployeeIdentity) ? participatingClientId : null);
            personalChat.setClient2Id(e2Identity.equals(associatedEmployeeIdentity) ? participatingClientId : null);
        }
        personalChatDao.save(personalChat);

        var twilioConversation = new TwilioConversation();
        twilioConversation.setConversationType(ConversationType.PERSONAL);
        twilioConversation.setTwilioConversationSid(conversation.getSid());
        twilioConversation.setDateCreated(conversation.getDateCreated().toInstant());
        twilioConversation.setLastMessageIndex(-1);
        twilioConversationDao.save(twilioConversation);

        return conversation.getSid();
    }

    private String createGroupChat(Collection<EmployeeTwilioCommunicationsUser> employees, EmployeeTwilioCommunicationsUser creator,
                                   String friendlyName,
                                   Long participatingClientId, String associatedEmployeeIdentity,
                                   Long incidentReportId) {
        var identities = employees.stream().map(twilioUserService::toIdentity).collect(Collectors.toSet());

        var irInfo = Optional.ofNullable(incidentReportId)
                .map(id -> incidentReportService.findById(id, IncidentReportClientIdCommunityIdAwareEntity.class))
                .orElse(null);

        var conversation = createConversationResource(ConversationType.GROUP, identities, friendlyName,
                participatingClientId, associatedEmployeeIdentity, irInfo);

        saveGroupTwilioConversation(conversation, friendlyName);

        var creatorIdentity = twilioUserService.toIdentity(creator);
        identities.forEach(identity -> addGroupChatParticipant(conversation.getSid(), identity,
                creatorIdentity.equals(identity), creator,
                identity.equals(associatedEmployeeIdentity) ? participatingClientId : null));

        if (irInfo != null) {
            incidentReportService.assignConversation(irInfo.getId(), conversation.getSid());
        }
        createConversationWebhook(conversation.getSid());
        return conversation.getSid();
    }

    private void saveGroupTwilioConversation(Conversation conversation, String friendlyName) {
        var twilioConversation = new TwilioConversation();
        twilioConversation.setConversationType(ConversationType.GROUP);
        twilioConversation.setTwilioConversationSid(conversation.getSid());
        twilioConversation.setLastMessageIndex(-1);
        twilioConversation.setFriendlyConversationName(friendlyName);
        twilioConversation.setDateCreated(conversation.getDateCreated().toInstant());
        twilioConversationDao.save(twilioConversation);
    }

    @Override
    public Optional<String> findPersonalChatSid(Long employeeId1, Long employeeId2) {
        var chatBetweenUsers = chatSpecificationGenerator.personalChatBetweenUsers(
                ConversationUtils.employeeIdToIdentity(employeeId1),
                ConversationUtils.employeeIdToIdentity(employeeId2));

        return personalChatDao.findAll(chatBetweenUsers).stream()
                .map(PersonalChat::getTwilioConversationSid)
                .findFirst();
    }


    private Conversation createConversationResource(ConversationType type, Set<String> futureParticipantIdentities,
                                                    Long participatingClientId, String associatedContactIdentity) {
        return createConversationResource(type, futureParticipantIdentities, null, participatingClientId, associatedContactIdentity, null);
    }

    private Conversation createConversationResource(ConversationType type, Set<String> futureParticipantIdentities,
                                                    String friendlyName,
                                                    Long participatingClientId, String associatedContactIdentity,
                                                    IncidentReportClientIdCommunityIdAwareEntity irInfo) {
        if (CollectionUtils.isEmpty(futureParticipantIdentities)) {
            throw new BusinessException("Empty conversations are not allowed");
        }
        var conversationAttrs = new ConversationAttributes()
                .setType(type)
                .setFriendlyName(friendlyName)
                .setParticipantIdentities(futureParticipantIdentities)
                .setParticipatingClientId(participatingClientId)
                .setAssociatedContactIdentity(associatedContactIdentity);
        if (irInfo != null) {
            conversationAttrs = conversationAttrs
                    .setIncidentReportId(irInfo.getId())
                    .setIrClientId(irInfo.getEventClientId())
                    .setIrClientCommunityId(irInfo.getEventClientCommunityId());
        }

        var conversationAttrsStr = attributeService.build(conversationAttrs);
        return Conversation.creator(chatServiceSid)
                .setAttributes(conversationAttrsStr)
                .create(twilioRestClient);
    }

    private void createConversationWebhook(String conversationSid) {
        if (StringUtils.isNotEmpty(conversationWebhookPath)) {
            Webhook.creator(chatServiceSid, conversationSid, Webhook.Target.WEBHOOK)
                    .setConfigurationMethod(Webhook.Method.POST)
                    .setConfigurationFilters(conversationWebhookFilters)
                    .setConfigurationUrl(conversationWebhookPath)
                    .create(twilioRestClient);
        }
    }

    private Participant createParticipantResource(String conversationSid, String identity, ParticipantAttributes attributes) {
        return Participant.creator(chatServiceSid, conversationSid)
                .setIdentity(identity)
                .setAttributes(attributeService.build(attributes))
                .create(twilioRestClient);
    }

    @Override
    public String addParticipants(String conversationSid, String friendlyName, Set<Long> employeeIds, Long selfId, Long participatingClientId) {
        var self = twilioUserService.findById(selfId);
        validateChatEnabledForActor(self);

        var employees = twilioUserService.findByIdIn(employeeIds);
        employees.forEach(this::validateChatEnabled);

        var currentParticipantsMap = getConversationParticipantMap(conversationSid);

        var identitiesToAdd = employees.stream().map(twilioUserService::toIdentity).collect(Collectors.toSet());
        validateNotAlreadyParticipants(currentParticipantsMap, identitiesToAdd);

        if (!isConnected(conversationSid)) {
            throw new BusinessException("Can't add participants to disconnected chats");
        }

        var conversation = fetchConversationResource(conversationSid);
        var conversationAttr = attributeService.parse(conversation);

        String associatedEmployeeIdentity = null;
        if (participatingClientId != null) {
            if (conversationAttr.getParticipatingClientId() != null) {
                throw new BusinessException("Client is already present in conversation");
            }
            associatedEmployeeIdentity = findAssociatedContactIdentity(employees, participatingClientId);
        }

        var allIdentities = Sets.union(currentParticipantsMap.keySet(), identitiesToAdd);

        switch (conversationAttr.getType()) {
            case PERSONAL:
                if (CollectionUtils.isEmpty(identitiesToAdd)) {
                    //don't create group chat copy
                    break;
                }
                if (participatingClientId == null) {
                    //if there was a client in personal chat, he should remain in new group chat
                    participatingClientId = conversationAttr.getParticipatingClientId();
                    associatedEmployeeIdentity = conversationAttr.getAssociatedContactIdentity();
                }
                conversation = createConversationResource(ConversationType.GROUP, allIdentities, friendlyName,
                        participatingClientId, associatedEmployeeIdentity, null);

                var newConversationSid = conversation.getSid();
                createConversationWebhook(newConversationSid);
                saveGroupTwilioConversation(conversation, friendlyName);

                var selfIdentity = twilioUserService.toIdentity(self);

                for (var identity : currentParticipantsMap.keySet()) {
                    addGroupChatParticipant(newConversationSid, identity, selfIdentity.equals(identity), self,
                            identity.equals(associatedEmployeeIdentity) ? participatingClientId : null);
                }
                break;
            case GROUP:
                validateOwner(currentParticipantsMap, self);

                conversationAttr = conversationAttr
                        .setParticipantIdentities(allIdentities)
                        .setFriendlyName(friendlyName);

                if (participatingClientId != null) {
                    conversationAttr = conversationAttr
                            .setParticipatingClientId(participatingClientId)
                            .setAssociatedContactIdentity(associatedEmployeeIdentity);
                }

                var newAttrs = attributeService.build(conversationAttr);

                conversation = Conversation.updater(chatServiceSid, conversation.getSid())
                        .setAttributes(newAttrs)
                        .update(twilioRestClient);

                twilioConversationDao.updateFriendlyName(conversation.getSid(), friendlyName);

                break;
            default:
                throw new BusinessException("Unknown chat type " + conversationAttr.getType());
        }

        employees.forEach(twilioUserService::createUserIfNotExists);
        var sid = conversation.getSid();
        for (var identity : identitiesToAdd) {
            addGroupChatParticipant(sid, identity, false, self,
                    identity.equals(associatedEmployeeIdentity) ? participatingClientId : null);
        }

        if (CollectionUtils.isNotEmpty(employees)) {
            writeParticipantsAddedMessage(sid, employees, self);
        }

        return sid;
    }

    private String findAssociatedContactIdentity(Collection<EmployeeTwilioCommunicationsUser> employees, Long participatingClientId) {
        return employees.stream()
                .filter(e -> CollectionUtils.emptyIfNull(e.getAssociatedClientIds()).contains(participatingClientId))
                .findFirst()
                .map(twilioUserService::toIdentity)
                .orElseThrow(() ->
                        new BusinessException("Associated contact is not present for client " + participatingClientId));
    }

    @Override
    public Iterable<Participant> getChatParticipants(String conversationSid) {
        return Participant.reader(chatServiceSid, conversationSid).read(twilioRestClient);
    }

    @Override
    public Map<String, Participant> getConversationParticipantMap(String conversationSid) {
        return StreamUtils.stream(getChatParticipants(conversationSid)).collect(StreamUtils.toMapOfUniqueKeys(Participant::getIdentity));
    }

    private Participant addGroupChatParticipant(String conversationSid, String identity, boolean isOwner, EmployeeTwilioCommunicationsUser addedBy,
                                                Long participatingClientId) {
        //execute in new transaction so that even if there are further errors DB state is consistent with Twilio
        var p = transactionTemplate.execute(tx -> {
            var attr = new ParticipantAttributes().setIsOwner(isOwner).setClientId(participatingClientId);
            var participant = createParticipantResource(conversationSid, identity, attr);
            saveAddedParticipantHistory(participant, addedBy, participatingClientId);
            createParticipantReadMessageStatus(participant);

            return participant;
        });
        if (identity.equals("e1324")) {
            logger.info("Test throw for e1324, conversation {}", conversationSid);
            throw new IllegalArgumentException();
        }
        return p;
    }

    private void validateOwner(Map<String, Participant> participants, EmployeeTwilioCommunicationsUser self) {
        var selfParticipant = participants.getOrDefault(twilioUserService.toIdentity(self), null);
        if (selfParticipant == null) {
            throw new BusinessException("Only conversation owner can edit conversation");
        }
        var selfAttrs = attributeService.parse(selfParticipant);
        if (!selfAttrs.getIsOwner()) {
            throw new BusinessException("Only conversation owner can edit conversation");
        }
    }

    private void validateParticipantNotOwner(Map<String, Participant> participants, String selfIdentity) {
        var selfParticipant = participants.getOrDefault(selfIdentity, null);
        if (selfParticipant == null) {
            throw new BusinessException("Employee is not participant of conversation");
        }
        var selfAttrs = attributeService.parse(selfParticipant);
        if (selfAttrs.getIsOwner()) {
            throw new BusinessException("Conversation owner can not perform this operation");
        }
    }

    private void validateNotAlreadyParticipants(Map<String, Participant> currentParticipantsMap, Collection<String> identities) {
        if (identities.stream().anyMatch(currentParticipantsMap::containsKey)) {
            //or just filter them out?
            throw new BusinessException("One of conversation members is already conversation member");
        }
    }

    @Override
    public void deleteParticipants(String conversationSid, Set<Long> employeeIds, Long selfId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return;
        }
        if (employeeIds.contains(selfId)) {
            throw new BusinessException("Can't delete yourself");
        }

        var self = twilioUserService.findById(selfId);
        validateChatEnabledForActor(self);

        if (!isConnected(conversationSid)) {
            throw new BusinessException("Can't delete participants from disconnected chats");
        }

        var conversation = fetchConversationResource(conversationSid);
        var conversationAttr = attributeService.parse(conversation);

        if (ConversationType.GROUP != conversationAttr.getType()) {
            throw new BusinessException("Can't delete participants from non-group conversation");
        }

        var currentParticipantsMap = getConversationParticipantMap(conversationSid);
        validateOwner(currentParticipantsMap, self);

        var employees = twilioUserService.findByIdIn(employeeIds);
        if (CollectionUtils.isEmpty(employees) || employees.size() != employeeIds.size()) {
            throw new BusinessException("Incorrect employee ids for deleting from conversation");
        }
        var identities = employees.stream().map(twilioUserService::toIdentity).collect(Collectors.toSet());
        validateConversationMembers(currentParticipantsMap, identities);

        identities.forEach(identity -> {
            transactionTemplate.executeWithoutResult(tx -> {
                if (deleteParticipant(conversationSid, currentParticipantsMap.get(identity).getSid())) {
                    saveRemovedParticipantHistory(currentParticipantsMap.get(identity), self);
                    deleteParticipantReadMessageStatus(currentParticipantsMap.get(identity).getSid());
                }
            });
        });

        conversationAttr.getParticipantIdentities().removeAll(identities);
        if (identities.contains(conversationAttr.getAssociatedContactIdentity())) {
            conversationAttr = conversationAttr
                    .setAssociatedContactIdentity(null)
                    .setParticipatingClientId(null);
        }
        Conversation.updater(chatServiceSid, conversation.getSid())
                .setAttributes(attributeService.build(conversationAttr))
                .update(twilioRestClient);

        writeParticipantsRemovedMessage(conversationSid, employees, self);
    }

    private Conversation fetchConversationResource(String conversationSid) {
        return Conversation.fetcher(chatServiceSid, conversationSid).fetch(twilioRestClient);
    }

    private void validateConversationMembers(Map<String, Participant> currentParticipantsMap, Collection<String> identities) {
        if (!identities.stream().allMatch(currentParticipantsMap::containsKey)) {
            throw new BusinessException("One of participants is not conversation member");
        }
    }

    private boolean deleteParticipant(String conversationSid, String participantSid) {
        return Participant.deleter(chatServiceSid, conversationSid, participantSid)
                .delete(twilioRestClient);
    }

    @Override
    public void leaveConversation(String conversationSid, Long selfId) {
        var self = twilioUserService.findById(selfId);
        validateChatEnabledForActor(self);

        var conversation = fetchConversationResource(conversationSid);
        var conversationAttr = attributeService.parse(conversation);

        if (ConversationType.GROUP != conversationAttr.getType()) {
            throw new BusinessException("Can't leave non-group conversation");
        }

        var selfIdentity = twilioUserService.toIdentity(self);
        var currentParticipantsMap = getConversationParticipantMap(conversationSid);

        validateParticipantNotOwner(currentParticipantsMap, selfIdentity);

        var participant = currentParticipantsMap.getOrDefault(selfIdentity, null);
        transactionTemplate.executeWithoutResult(tx -> {
            if (deleteParticipant(conversationSid, participant.getSid())) {
                saveLeftParticipantHistory(participant);
                deleteParticipantReadMessageStatus(participant.getSid());
            }
        });

        conversationAttr.getParticipantIdentities().remove(selfIdentity);
        Conversation.updater(chatServiceSid, conversation.getSid())
                .setAttributes(attributeService.build(conversationAttr))
                .update(twilioRestClient);

        writeParticipantLeft(conversationSid, self);
    }

    private void saveAddedParticipantHistory(Participant participant, EmployeeTwilioCommunicationsUser addedBy, Long participatingClientId) {
        var history = new GroupChatParticipantHistory();

        history.setAddedDatetime(Instant.now());
        history.setAddedByTwilioIdentity(twilioUserService.toIdentity(addedBy));

        history.setTwilioConversationSid(participant.getConversationSid());
        history.setTwilioParticipantSid(participant.getSid());
        history.setTwilioIdentity(participant.getIdentity());
        history.setClientId(participatingClientId);

        groupChatParticipantHistoryDao.save(history);
    }

    private void createParticipantReadMessageStatus(Participant participant) {
        var readMessageStatus = new TwilioParticipantReadMessageStatus();

        readMessageStatus.setTwilioParticipantSid(participant.getSid());
        readMessageStatus.setTwilioConversationSid(participant.getConversationSid());
        readMessageStatus.setLastReadMessageIndex(Optional.ofNullable(participant.getLastReadMessageIndex()).orElse(-1));
        readMessageStatus.setEmployeeId(ConversationUtils.employeeIdFromIdentity(participant.getIdentity()));

        twilioParticipantReadMessageStatusDao.save(readMessageStatus);
    }

    private void deleteParticipantReadMessageStatus(String participantSid) {
        twilioParticipantReadMessageStatusDao.deleteById(participantSid);
    }

    private void saveRemovedParticipantHistory(Participant participant, EmployeeTwilioCommunicationsUser deletedBy) {
        var history = groupChatParticipantHistoryDao.findFirstByTwilioParticipantSidAndDeletedDatetimeIsNull(participant.getSid());

        if (history == null) {
            logger.warn("'Added' history entry not found for participant [" + participant.getSid() + "]");
            return;
        }

        history.setDeletedDatetime(Instant.now());
        history.setDeletedReason(GroupChatParticipantHistoryDeletedReason.REMOVED);
        history.setRemovedByTwilioIdentity(twilioUserService.toIdentity(deletedBy));

        groupChatParticipantHistoryDao.save(history);
    }

    private void saveLeftParticipantHistory(Participant participant) {
        var history = groupChatParticipantHistoryDao.findFirstByTwilioParticipantSidAndDeletedDatetimeIsNull(participant.getSid());

        if (history == null) {
            logger.warn("'Added' history entry not found for participant [" + participant.getSid() + "]");
            return;
        }

        history.setDeletedDatetime(Instant.now());
        history.setDeletedReason(GroupChatParticipantHistoryDeletedReason.LEFT);

        groupChatParticipantHistoryDao.save(history);
    }

    private Message writeParticipantsAddedMessage(String conversationSid, Collection<EmployeeTwilioCommunicationsUser> addedMembers, EmployeeTwilioCommunicationsUser whoAdded) {
        var body = concatNamesAndAuxiliaryVerb(addedMembers) +
                " added by " + whoAdded.getFullName();

        return writeSystemMessage(conversationSid, SystemMessage.PARTICIPANTS_ADDED, body);
    }

    private Message writeParticipantsRemovedMessage(String conversationSid, Collection<EmployeeTwilioCommunicationsUser> removedMembers, EmployeeTwilioCommunicationsUser whoRemoved) {
        var body = concatNamesAndAuxiliaryVerb(removedMembers) +
                " removed by " + whoRemoved.getFullName();

        return writeSystemMessage(conversationSid, SystemMessage.PARTICIPANTS_REMOVED, body);
    }

    private Message writeParticipantLeft(String conversationSid, EmployeeTwilioCommunicationsUser whoLeft) {
        var body = whoLeft.getFullName() + " left";

        return writeSystemMessage(conversationSid, SystemMessage.PARTICIPANT_LEFT, body);
    }

    private Message writeParticipantJoined(String conversationSid, EmployeeTwilioCommunicationsUser whoLeft) {
        var body = whoLeft.getFullName() + " joined";
        return writeSystemMessage(conversationSid, SystemMessage.PARTICIPANT_JOINED, body);
    }

    @Override
    public Message writeSystemMessage(String conversationSid, SystemMessage systemMessage, String body) {
        var attr = new MessageAttributes().setSystemMessageName(systemMessage);
        return sendMessage(conversationSid, null, attr, body);
    }

    @Override
    public Message sendTextMessageWithLinks(String conversationSid, String author, String messageText) {
        var attr = new MessageAttributes().setDisplayLinks(true);
        return sendMessage(conversationSid, author, attr, messageText);
    }

    private Message sendMessage(String conversationSid, String author, MessageAttributes messageAttributes, String body) {
        var message = createMessage(conversationSid, author, messageAttributes, body);
        if (messageAttributes.getSystemMessageName() == null || !IGNORED_FOR_MISSED_CHAT_SYSTEM_MESSAGES.contains(messageAttributes.getSystemMessageName())) {
            twilioConversationDao.updateLastMessage(conversationSid, message.getIndex(), message.getDateCreated().toInstant());
        }
        return message;
    }

    private Message createMessage(String conversationSid, MessageAttributes messageAttributes, String body) {
        return createMessage(conversationSid, null, messageAttributes, body);
    }

    private Message createMessage(String conversationSid, String author, MessageAttributes messageAttributes, String body) {
        return Message.creator(chatServiceSid, conversationSid)
                .setAuthor(author)
                .setAttributes(attributeService.build(messageAttributes))
                .setBody(body)
                .create(twilioRestClient);
    }

    private Message createMessage(String conversationSid, String mediaSid, String author) {
        var creator = Message.creator(chatServiceSid, conversationSid)
                .setMediaSid(mediaSid);
        if (StringUtils.isNotEmpty(author)) {
            creator = creator
                    .setAuthor(author)
                    .setXTwilioWebhookEnabled(Message.WebhookEnabledType.TRUE);
        }

        return creator.create(twilioRestClient);
    }

    private String concatNamesAndAuxiliaryVerb(Collection<EmployeeTwilioCommunicationsUser> users) {
        return concatNames(users) + (users.size() == 1 ? " was" : " were");
    }

    private String concatNames(Collection<EmployeeTwilioCommunicationsUser> users) {
        return users.stream().map(EmployeeTwilioCommunicationsUser::getFullName).collect(Collectors.joining(", "));

    }

    @Override
    public List<IdentityListItemDto> findEmployeeChatUsersByConversationSids(List<String> conversationSids, Long selfId) {
        var userIdentities = getUserIdentities(conversationSids, selfId);

        //TODO another approach is to load participants from twilio (remove if not necessary)
        /*var userIdentities = conversationSids.stream()
                .map(this::conversationParticipantMap)
                .peek(participantsMap -> validateConversationMember(participantsMap, selfIdentity))
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());*/

        return twilioUserService.findDtoByIdentities(userIdentities);
    }

    @Override
    public Set<String> findActiveUserIdentitiesByConversationSids(List<String> conversationSids, Long selfId) {
        var self = twilioUserService.findById(selfId);
        var selfIdentity = twilioUserService.toIdentity(self);

        var personalChatsIdentities = getPersonalChatIdentities(conversationSids, selfIdentity);
        var groupChatIdentities = getGroupChatIdentities(conversationSids, selfIdentity, true);

        return Stream.concat(personalChatsIdentities.stream(), groupChatIdentities.stream())
                .collect(Collectors.toSet());
    }

    private Set<String> getUserIdentities(List<String> conversationSids, Long selfId) {
        var self = twilioUserService.findById(selfId);
        validateChatEnabledForActor(self);

        var selfIdentity = twilioUserService.toIdentity(self);

        var personalChatsIdentities = getPersonalChatIdentities(conversationSids, selfIdentity);
        var groupChatIdentities = getGroupChatIdentities(conversationSids, selfIdentity);

        return Stream.concat(personalChatsIdentities.stream(), groupChatIdentities.stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getPersonalChatIdentities(List<String> conversationSids, String selfIdentity) {
        return personalChatDao.findByTwilioConversationSidIn(conversationSids)
                .stream()
                .peek(personalChat -> validateMemberOfPersonalChat(personalChat, selfIdentity))
                .flatMap(personalChat -> Stream.of(personalChat.getTwilioIdentity1(), personalChat.getTwilioIdentity2()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getGroupChatIdentities(List<String> conversationSids, String selfIdentity) {
        return getGroupChatIdentities(conversationSids, selfIdentity, false);
    }

    private Set<String> getGroupChatIdentities(List<String> conversationSids, String selfIdentity, boolean activeOnly) {

        List<GroupChatParticipantHistory> participants;
        if (activeOnly) {
            participants = groupChatParticipantHistoryDao.findByTwilioConversationSidInAndDeletedDatetimeIsNull(conversationSids);
        } else {
            participants = groupChatParticipantHistoryDao.findByTwilioConversationSidIn(conversationSids);
        }

        return participants.stream()
                .collect(Collectors.groupingBy(GroupChatParticipantHistory::getTwilioConversationSid))
                .entrySet()
                .stream()
                .peek(conversationMapEntry -> validatePresentMemberOfGroupChat(conversationMapEntry.getValue(), selfIdentity))
                .flatMap(conversationMapEntry -> conversationMapEntry.getValue().stream())
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .collect(Collectors.toSet());
    }

    private void validatePresentMemberOfGroupChat(List<GroupChatParticipantHistory> chatHistory, String selfIdentity) {
        if (chatHistory.stream()
                .filter(groupChatParticipantHistory -> groupChatParticipantHistory.getDeletedDatetime() == null)
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .noneMatch(identity -> identity.equals(selfIdentity))) {
            throw new BusinessException("Participant is not conversation member");
        }
    }

    private void validateMemberOfPersonalChat(PersonalChat personalChat, String identity) {
        if (!identity.equals(personalChat.getTwilioIdentity1()) && !identity.equals(personalChat.getTwilioIdentity2())) {
            throw new BusinessException("Participant is not conversation member");
        }
    }

    @Override
    public boolean isGroupChatParticipant(String conversationSid, Long employeeId) {
        var user = twilioUserService.findById(employeeId);
        var userIdentity = twilioUserService.toIdentity(user);

        boolean isParticipant = groupChatParticipantHistoryDao.findByTwilioConversationSidIn(Set.of(conversationSid))
                .stream()
                .filter(groupChatParticipantHistory -> groupChatParticipantHistory.getDeletedDatetime() == null)
                .map(GroupChatParticipantHistory::getTwilioIdentity)
                .anyMatch(identity -> identity.equals(userIdentity));

        return isParticipant;
    }

    @Override
    public void joinIncidentReportConversation(Long incidentReportId, Long selfId) {
        var conversationSid = incidentReportService.findById(incidentReportId, ConversationSidAware.class).getTwilioConversationSid();
        if (StringUtils.isEmpty(conversationSid)) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "Incident Report doesn't have conversation assigned");
        }
        var self = twilioUserService.findById(selfId);
        joinConversation(conversationSid, self, null);
    }

    @Override
    public void joinConversation(String conversationSid, EmployeeTwilioCommunicationsUser self, Integer maxParticipantQty) {
        var selfIdentity = twilioUserService.toIdentity(self);

        validateChatEnabled(self);
        var currentParticipantsMap = getConversationParticipantMap(conversationSid);

        validateNotAlreadyParticipants(currentParticipantsMap, Set.of(selfIdentity));

        var conversation = Conversation.fetcher(chatServiceSid, conversationSid).fetch(twilioRestClient);
        var conversationAttr = attributeService.parse(conversation);

        var allIdentities = Sets.union(currentParticipantsMap.keySet(), Set.of(selfIdentity));

        if (conversationAttr.getType() != ConversationType.GROUP) {
            throw new BusinessException("Chat should be of Group type");
        }

        if (maxParticipantQty != null && allIdentities.size() > maxParticipantQty) {
            throw new BusinessException("Exceeded max number of chart members(" + maxParticipantQty + "): " + allIdentities.size());
        }

        conversationAttr.setParticipantIdentities(allIdentities);
        var conversationUpdater = Conversation.updater(chatServiceSid, conversation.getSid())
                .setAttributes(attributeService.build(conversationAttr));
        conversationUpdater.update(twilioRestClient);

        twilioUserService.createUserIfNotExists(self);
        var sid = conversation.getSid();
        addGroupChatParticipant(sid, selfIdentity, false, self, null);
        writeParticipantJoined(sid, self);
    }

    @Override
    public boolean existsConversationBetweenAnyAndClient(Collection<Long> employeeIds, Long clientId) {
        if (CollectionUtils.isEmpty(employeeIds) || clientId == null) {
            return false;
        }

        var identities = employeeIds.stream().map(ConversationUtils::employeeIdToIdentity).collect(Collectors.toSet());
        var personalChatWithClient = chatSpecificationGenerator.personalChatBetweenAnyUserAndClient(identities, clientId);

        if (personalChatDao.count(personalChatWithClient) > 0) {
            return true;
        }

        var groupChatBetweenAnyUserAndClient = chatSpecificationGenerator.groupChatBetweenAnyUserAndClient(identities, clientId);
        return groupChatParticipantHistoryDao.count(groupChatBetweenAnyUserAndClient) > 0;
    }

    @Override
    public String getServiceConversationSid(Employee employee) {
        return getServiceConversationSid(new EntityBackedEmployeeTwilioCommunicationsUser(employee));
    }

    private String getServiceConversationSid(EmployeeTwilioCommunicationsUser user) {
        validateChatEnabled(user);
        twilioUserService.createUserIfNotExists(user);

        if (StringUtils.isNotEmpty(user.getTwilioServiceConversationSid())) {
            return user.getTwilioServiceConversationSid();
        }

        var identity = twilioUserService.toIdentity(user);
        var conversation = createConversationResource(ConversationType.SERVICE, Collections.singleton(identity), null, null);
        createParticipantResource(conversation.getSid(), identity, new ParticipantAttributes());

        twilioUserService.updateServiceConversationSid(user, conversation.getSid());

        return conversation.getSid();
    }

    @Override
    public Message sendServiceMessage(EmployeeTwilioCommunicationsUser user,
                                      ServiceMessage serviceMessage,
                                      String devicePushNotificationTokenToExclude) {
        var serviceConversationSid = getServiceConversationSid(user);
        var message = createMessage(serviceConversationSid, new MessageAttributes(), serviceMessageEncoder.encode(serviceMessage));
        conversationNotificationService.sendServiceMessageNotifications(
                serviceMessage,
                message.getSid(),
                user.getId(),
                devicePushNotificationTokenToExclude
        );
        return message;
    }

    @Override
    public String getFriendlyName(String conversationSid) {
        //we can also pull name from DB instead of twilio
        var conversation = fetchConversationResource(conversationSid);
        return attributeService.parse(conversation).getFriendlyName();
    }

    @Override
    public void updateFriendlyName(String conversationSid, String friendlyName) {
        if (StringUtils.isEmpty(friendlyName)) {
            return;
        }
        var conversation = fetchConversationResource(conversationSid);
        var conversationAttr = attributeService.parse(conversation);
        conversationAttr.setFriendlyName(friendlyName);
        Conversation.updater(chatServiceSid, conversation.getSid())
                .setAttributes(attributeService.build(conversationAttr))
                .update(twilioRestClient);
        twilioConversationDao.updateFriendlyName(conversationSid, friendlyName);
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void registerActiveCallChat(String conversationSid, String roomSid) {
        logger.info("Registering conversation [{}] as active call", conversationSid);
        updateRoomSidAttribute(conversationSid, roomSid);

        var conversationParticipants = getChatParticipants(conversationSid);
        for (var participant : conversationParticipants) {
            twilioUserService.registerActiveCallChat(participant.getIdentity(), conversationSid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void unregisterActiveCallChat(String conversationSid) {
        logger.info("Unregistering conversation [{}] as active call", conversationSid);
        updateRoomSidAttribute(conversationSid, null);

        var conversationParticipants = getChatParticipants(conversationSid);
        for (var participant : conversationParticipants) {
            twilioUserService.unregisterActiveCallChat(participant.getIdentity(), conversationSid);
        }
    }

    private void updateRoomSidAttribute(String conversationSid, String roomSid) {
        var conversation = fetchConversationResource(conversationSid);
        var attributes = attributeService.parse(conversation);
        if (!Objects.equals(attributes.getRoomSid(), roomSid)) {
            attributes.setRoomSid(roomSid);
            Conversation.updater(chatServiceSid, conversationSid)
                    .setAttributes(attributeService.build(attributes))
                    .update(twilioRestClient);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyChatParticipant(String conversationSid, Collection<Long> employeeIds) {
        var userIdentities = twilioUserService.findByIdIn(employeeIds).stream()
                .map((user -> twilioUserService.toIdentity(user)))
                .collect(Collectors.toSet());

        return isAnyChatParticipantByIdentities(conversationSid, userIdentities);
    }

    private boolean isAnyChatParticipantByIdentities(String conversationSid, Collection<String> userIdentities) {
        var inPersonalChat = chatSpecificationGenerator.inPersonalChat(conversationSid, userIdentities);
        if (personalChatDao.count(inPersonalChat) > 0) {
            return true;
        }

        var inGroupChat = chatSpecificationGenerator.inGroupChat(conversationSid, userIdentities);
        return groupChatParticipantHistoryDao.count(inGroupChat) > 0;
    }

    @Override
    public Media fetchMedia(String mediaSid) {
        return Media.fetcher(chatServiceSid, mediaSid).fetch(twilioRestClient);
    }

    @Override
    public byte[] downloadMediaContent(Media media) {
        return Media.contentDownloader(media).download(twilioRestClient);
    }

    @Override
    public String sendMediaMessage(String conversationSid, String author, String fileName, String mediaType, byte[] bytes) {
        var media = Media.creator(chatServiceSid)
                .setContent(bytes)
                .setFilename(fileName)
                .setContentType(mediaType)
                .create(twilioRestClient);

        var message = createMessage(conversationSid, media.getSid(), author);

        twilioConversationDao.updateLastMessage(conversationSid, message.getIndex(), message.getDateCreated().toInstant());

        return message.getSid();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsConversationBetweenAnyAndEmployee(Collection<Long> employeeIds, Long employeeId) {
        if (CollectionUtils.isEmpty(employeeIds) || employeeId == null) {
            return false;
        }

        var identities = employeeIds.stream().map(ConversationUtils::employeeIdToIdentity).collect(Collectors.toSet());
        var personalChatWithEmployee = chatSpecificationGenerator.personalChatBetweenAnyUserAndUser(identities, ConversationUtils.employeeIdToIdentity(employeeId));

        if (personalChatDao.exists(personalChatWithEmployee)) {
            return true;
        }

        var groupChatBetweenAnyUserAndUser = chatSpecificationGenerator.groupChatBetweenAnyUserAndUser(identities, ConversationUtils.employeeIdToIdentity(employeeId));
        return groupChatParticipantHistoryDao.exists(groupChatBetweenAnyUserAndUser);
    }

    @Override
    public Message fetchMessage(String conversationSid, String messageSid) {
        return Message.fetcher(chatServiceSid, conversationSid, messageSid).fetch(twilioRestClient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityAndRoleAwareIdentityListItemDto> findEmployeeChatUserDetailsByConversationSid(String conversationSid, Long selfId) {
        var userIdentities = getUserIdentities(Collections.singletonList(conversationSid), selfId);
        return twilioUserService.findDetailedDtoByIdentities(userIdentities);
    }

    @Override
    public Optional<String> findOwnerIdentity(String conversationSid) {
        //todo consider moving owner info to conversation attributes
        var participants = Participant.reader(chatServiceSid, conversationSid).read(twilioRestClient);
        return StreamUtils.stream(participants)
                .filter(participant -> attributeService.parse(participant).getIsOwner())
                .map(Participant::getIdentity)
                .findFirst();
    }

    @Override
    public void deleteChatHistoryForIdentities(Collection<String> identities) {
        logger.info("Deleting chats for identities {}", identities);

        var personalChats = personalChatDao.findAll(chatSpecificationGenerator.personalChatsOfUsers(identities));

        var personalChatSids = personalChats.stream().map(PersonalChat::getTwilioConversationSid).collect(Collectors.toSet());
        logger.info("Found personal chats for identities {}: {}", identities, personalChatSids);

        personalChatSids.forEach(personalChatSid -> {
            Conversation.deleter(chatServiceSid, personalChatSid).delete(twilioRestClient);
            logger.info("Removed personal chat [{}] from Twilio", personalChatSid);
        });

        personalChatDao.deleteAll(personalChats);
        logger.info("Removed personal chats from DB");

        var groupChatHistoryEntries = groupChatParticipantHistoryDao.findAll(
                chatSpecificationGenerator.groupChatEntriesOfUsers(identities)
        );

        var groupChatSids = groupChatHistoryEntries.stream().map(GroupChatParticipantHistory::getTwilioConversationSid)
                .collect(Collectors.toSet());
        logger.info("Found group chats for identities {}: {}", identities, groupChatSids);

        groupChatSids.forEach(groupChatSid -> {
            Message.reader(chatServiceSid, groupChatSid).read(twilioRestClient)
                    .forEach(message -> {
                        if (StringUtils.isNotEmpty(message.getAuthor()) && identities.contains(message.getAuthor())) {
                            Message.deleter(chatServiceSid, groupChatSid, message.getSid()).delete(twilioRestClient);
                            logger.info("Deleted message [{}] from group chat [{}] authored by [{}]",
                                    message.getSid(), groupChatSid, message.getAuthor());
                        }
                    });
            logger.info("Deleted messages of removed users from group chat [{}]", groupChatSid);
        });

        groupChatParticipantHistoryDao.deleteAll(groupChatHistoryEntries);
        logger.info("Removed group chats history entries for removed users");
    }

    @Override
    public String getPersonalOrCreateConversation(
            Set<Long> chatParticipantsIds,
            Long actorId,
            Long participatingClientId
    ) {
        if (chatParticipantsIds.size() == 2) { //participant is among chatParticipantsIds
            var it = chatParticipantsIds.iterator();
            var id1 = it.next();
            var id2 = it.next();
            var personalChatSid = findPersonalChatSid(id1, id2);

            if (personalChatSid.isPresent()) {
                return personalChatSid.get();
            }
        }
        return createChat(
                chatParticipantsIds,
                actorId,
                null,
                participatingClientId,
                null
        );
    }

    @Override
    @Transactional
    public void synchronizeLastMessageIndexes() {
        if (!isChatEnabled) {
            logger.info("Won't synchronize last messages indexes - chats are disabled");
            return;
        }
        twilioConversationDao.findAll(SpecificationUtils.and(), TwilioConversationSidAware.class).stream()
                .map(TwilioConversationSidAware::getTwilioConversationSid)
                .forEach(conversationSid -> {
                    Message.reader(chatServiceSid, conversationSid)
                            .setOrder(Message.OrderType.DESC)
                            .limit(1)
                            .read(twilioRestClient)
                            .forEach(msg -> twilioConversationDao.updateLastMessage(
                                    conversationSid,
                                    msg.getIndex(),
                                    msg.getDateCreated().toInstant())
                            );

                    Participant.reader(chatServiceSid, conversationSid)
                            .read(twilioRestClient)
                            .forEach(participant -> twilioParticipantReadMessageStatusDao.upsertLastReadMessage(
                                    participant.getSid(),
                                    Optional.ofNullable(participant.getLastReadMessageIndex()).orElse(-1),
                                    conversationSid,
                                    ConversationUtils.employeeIdFromIdentity(participant.getIdentity()))
                            );
                });
    }

    @Override
    @Transactional
    public void addReaction(String conversationSid, String messageSid, Long reactionId, Long actorId) {
        var user = twilioUserService.findById(actorId);

        validateChatEnabledForActor(user);

        var identity = ConversationUtils.employeeIdToIdentity(actorId);
        if (!isAnyChatParticipantByIdentities(conversationSid, List.of(identity))) {
            throw new BusinessException("User is not member of conversation");
        }

        //fetch locked entity so that only one user can modify reactions at single point of time across all modules
        var conversationEntity = twilioConversationDao.findLockedByConversationSid(conversationSid);
        if (conversationEntity.getDisconnected()) {
            throw new BusinessException("Reactions are not available in disconnected chats");
        }
        var message = Message.fetcher(chatServiceSid, conversationSid, messageSid)
                .fetch(twilioRestClient);

        var attributes = attributeService.parse(message);

        var reaction = new MessageReaction(identity, reactionId);

        boolean updateNeeded = false;
        if (CollectionUtils.isEmpty(attributes.getReactions())) {
            attributes.setReactions(List.of(reaction));
            updateNeeded = true;
        } else {
            if (attributes.getReactions().stream().noneMatch(reaction::equals)) {
                attributes.getReactions().removeIf(it -> Objects.equals(it.getAuthorIdentity(), identity));
                attributes.getReactions().add(reaction);
                updateNeeded = true;
            }
        }

        if (updateNeeded) {
            Message.updater(chatServiceSid, conversationSid, messageSid)
                    .setAttributes(attributeService.build(attributes))
                    .update(twilioRestClient);
        }

    }

    @Override
    @Transactional
    public void removeReaction(String conversationSid, String messageSid, Long reactionId, Long actorId) {
        var user = twilioUserService.findById(actorId);

        validateChatEnabledForActor(user);

        var identity = ConversationUtils.employeeIdToIdentity(actorId);
        if (!isAnyChatParticipantByIdentities(conversationSid, List.of(identity))) {
            throw new BusinessException("User is not member of conversation");
        }

        //fetch locked entity so that only one user can modify reactions at single point of time across all modules
        var conversationEntity = twilioConversationDao.findLockedByConversationSid(conversationSid);
        if (conversationEntity.getDisconnected()) {
            throw new BusinessException("Reactions are not available in disconnected chats");
        }

        var message = Message.fetcher(chatServiceSid, conversationSid, messageSid)
                .fetch(twilioRestClient);

        var attributes = attributeService.parse(message);

        if (CollectionUtils.isNotEmpty(attributes.getReactions()) &&
                attributes.getReactions()
                        .removeIf(r -> r.getAuthorIdentity().equals(identity) && r.getId().equals(reactionId))
        ) {
            Message.updater(chatServiceSid, conversationSid, messageSid)
                    .setAttributes(attributeService.build(attributes))
                    .update(twilioRestClient);
        }
    }

    @Override
    public void updateConversationsDisconnection(Set<String> conversationSids, boolean disconnect, boolean forceUpdate) {
        if (!isChatEnabled) {
            throw new RuntimeException("Chats are not enabled");
        }
        var conversationRoleSidToSet = disconnect ?
                readOnlyConversationRoleSid :
                conversationUserRoleSid;

        var conversationsAlreadyUpdated = forceUpdate ? Set.of()
                : twilioConversationDao.findAllByTwilioConversationSidInAndDisconnected(
                        conversationSids,
                        disconnect,
                        TwilioConversationSidAware.class).stream()
                .map(TwilioConversationSidAware::getTwilioConversationSid)
                .collect(Collectors.toSet());

        conversationSids.forEach(conversationSid -> {
            try {
                if (!forceUpdate && conversationsAlreadyUpdated.contains(conversationSid)) {
                    logger.info("Won't {} conversation {} - already {}",
                            disconnect ? "disconnect" : "reconnect",
                            conversationSid,
                            disconnect ? "disconnected" : "connected"
                    );
                    return;
                }

                logger.info("{} conversation {}...",
                        disconnect ? "Disconnecting" : "Reconnecting",
                        conversationSid
                );

                var participants = Participant.reader(chatServiceSid, conversationSid)
                        .read(twilioRestClient);
                logger.info("Fetched participants for conversation {}", conversationSid);

                participants.forEach(participant -> {
                    logger.info("{} participant {}, identity {} ...",
                            disconnect ? "Disconnecting" : "Reconnecting",
                            participant.getSid(), participant.getIdentity());
                    Participant.updater(chatServiceSid, conversationSid, participant.getSid())
                            .setRoleSid(conversationRoleSidToSet)
                            .update(twilioRestClient);
                    logger.info("{} participant {}, identity {}",
                            disconnect ? "Disconnected" : "Reconnected",
                            participant.getSid(), participant.getIdentity());
                });

                var conversation = Conversation.fetcher(chatServiceSid, conversationSid).fetch(twilioRestClient);
                var conversationAttrs = attributeService.parse(conversation);
                conversationAttrs.setDisconnected(disconnect);
                Conversation.updater(chatServiceSid, conversationSid)
                        .setAttributes(attributeService.build(conversationAttrs))
                        .update(twilioRestClient);

                twilioConversationDao.setDisconnected(disconnect, conversationSid);

                logger.info("{} conversation {}",
                        disconnect ? "Disconnected" : "Reconnected",
                        conversationSid);
            } catch (Exception ex) {
                logger.error("Failed to {} conversation {}",
                        disconnect ? "disconnect" : "reconnect",
                        conversationSid, ex);
            }
        });
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isConnected(String conversationSid) {
        return twilioConversationDao.findFirst(chatSpecificationGenerator.twilioConversationBySid(conversationSid), DisconnectedAware.class)
                .map(conversation -> !conversation.getDisconnected())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationTwilioDbSyncCheckResult> findBrokenChats() {
        var result = new ArrayList<ConversationTwilioDbSyncCheckResult>();

        Conversation.reader(chatServiceSid).read(twilioRestClient)
                .forEach(conversation -> {
                    var attrs = attributeService.parse(conversation);
                    var checkResult = checkConversationSync(conversation, attrs);
                    if (checkResult.isBroken()) {
                        result.add(checkResult);

                        if (!checkResult.isParticipantsMismatch()) {
                            checkResult.setTwilioParticipants(null);
                            checkResult.setDbCurrentParticipants(null);
                            checkResult.setAttributesParticipants(null);
                        }
                    }
                });
        return result;
    }

    @Override
    @Transactional
    public void fixBrokenChats(List<String> conversationSids, boolean logActionsOnly) {
        logger.info("Fixing broken conversations, logActionsOnly = {}", logActionsOnly);
        conversationSids.forEach(conversationSid -> {
                    var conversation = Conversation.fetcher(chatServiceSid, conversationSid).fetch(twilioRestClient);
                    var attrs = attributeService.parse(conversation);

                    var checkResult = checkConversationSync(conversation, attrs);

                    if (!checkResult.isBroken()) {
                        logger.info("Conversation {} is not broken", conversation.getSid());
                        return;
                    }

                    if (CollectionUtils.isEmpty(checkResult.getDbCurrentParticipants())) {
                        logger.info("No participants in DB, conversation {} will be deleted", conversationSid);

                        if (!checkResult.isMissingTwilioConversation()) {
                            if (!logActionsOnly) {
                                twilioConversationDao.deleteById(conversationSid);
                            }
                            logger.info("Deleted TwilioConversation entry for conversation {}", conversationSid);
                        }

                        var irWithConversation = incidentReportDao.findFirst((root, criteriaQuery, criteriaBuilder) ->
                                        criteriaBuilder.equal(root.get(IncidentReport_.twilioConversationSid), conversationSid),
                                IdAware.class);
                        irWithConversation.ifPresent(ir -> {
                            logger.info("Conversation {} is assigned to IR {}, will be unassigned", conversationSid, ir.getId());
                            if (!logActionsOnly) {
                                incidentReportDao.assignConversation(ir.getId(), null);
                            }
                        });

                        if (!logActionsOnly) {
                            Conversation.deleter(chatServiceSid, conversationSid).delete(twilioRestClient);
                        }
                        logger.info("Conversation {} was deleted", conversationSid);
                    } else {
                        logger.info("Conversation {} has participants...", conversationSid);
                        if (checkResult.isMissingTwilioConversation()) {
                            var twilioConversation = new TwilioConversation();
                            twilioConversation.setTwilioConversationSid(conversation.getSid());
                            twilioConversation.setFriendlyConversationName(attrs.getFriendlyName());
                            twilioConversation.setLastMessageIndex(-1);
                            twilioConversation.setConversationType(ConversationType.GROUP);
                            twilioConversation.setDateCreated(conversation.getDateCreated().toInstant());
                            twilioConversation.setDisconnected(attrs.getDisconnected());

                            Message.reader(chatServiceSid, conversationSid).setOrder(Message.OrderType.DESC)
                                    .limit(1)
                                    .read(twilioRestClient)
                                    .forEach(message -> {
                                        twilioConversation.setLastMessageIndex(message.getIndex());
                                        twilioConversation.setLastMessageDatetime(message.getDateCreated().toInstant());
                                    });

                            if (!logActionsOnly) {
                                twilioConversationDao.save(twilioConversation);
                            }

                            logger.info("Saved TwilioConversation for conversation {}", conversationSid);
                        }

                        if (checkResult.isDisconnectMismatch()) {
                            if (!logActionsOnly) {
                                updateConversationsDisconnection(Set.of(conversationSid), checkResult.getDbDisconnect(), true);
                            }
                            logger.info("Updated disconnection for conversation {}", conversationSid);
                        }

                        if (checkResult.isParticipantsMismatch()) {
                            if (!SetUtils.isEqualSet(attrs.getParticipantIdentities(), checkResult.getDbCurrentParticipants())) {
                                logger.info("Attributes participants updated for conversation {}", conversationSid);
                                attrs.setParticipantIdentities(checkResult.getDbCurrentParticipants());
                                if (!logActionsOnly) {
                                    Conversation.updater(chatServiceSid, conversationSid).setAttributes(attributeService.build(attrs))
                                            .update(twilioRestClient);
                                }
                            }
                            var alreadyPresentInTwilio = new HashSet<>();

                            Participant.reader(chatServiceSid, conversationSid).read(twilioRestClient)
                                    .forEach(participant -> {
                                        if (!checkResult.getDbCurrentParticipants().contains(participant.getIdentity())) {
                                            if (!logActionsOnly) {
                                                Participant.deleter(chatServiceSid, conversationSid, participant.getSid()).delete(twilioRestClient);
                                            }
                                            logger.info("Deleted participant {} (identity {}) from conversation {}", participant.getSid(), participant.getIdentity(), conversation.getSid());
                                        } else {
                                            alreadyPresentInTwilio.add(participant.getIdentity());
                                        }
                                    });

                            var groupHistory = groupChatParticipantHistoryDao.findAll(
                                    chatSpecificationGenerator.groupChatEntriesByConversationSid(conversation.getSid()).and(
                                            chatSpecificationGenerator.notDeletedGroupChatMember())
                            );

                            groupHistory.forEach(dbParticipant -> {
                                if (!alreadyPresentInTwilio.contains(dbParticipant.getTwilioIdentity())) {
                                    var participantAttrs = new ParticipantAttributes();
                                    participantAttrs.setIsOwner(false);
                                    participantAttrs.setClientId(dbParticipant.getClientId());
                                    if (!logActionsOnly) {
                                        createParticipantResource(conversationSid, dbParticipant.getTwilioIdentity(), participantAttrs);
                                    }
                                    logger.info("Added participant {} to conversation {} (client {})", dbParticipant.getTwilioIdentity(),
                                            conversationSid, participantAttrs.getClientId()
                                    );
                                }
                            });
                        }
                    }
                }
        );
    }

    private ConversationTwilioDbSyncCheckResult checkConversationSync(Conversation conversation, ConversationAttributes attrs) {
        var result = new ConversationTwilioDbSyncCheckResult();
        result.setConversationSid(conversation.getSid());
        result.setConversationType(attrs.getType());

        if (attrs.getType() == ConversationType.SERVICE) {
            return result;
        }

        var tc = twilioConversationDao.findFirst((root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(TwilioConversation_.twilioConversationSid), conversation.getSid()),
                TwilioConversation.class);
        if (tc.isEmpty()) {
            result.setMissingTwilioConversation(true);
        } else {
            if (tc.get().getDisconnected() != attrs.getDisconnected()) {
                result.setDisconnectMismatch(true);
                result.setDbDisconnect(tc.get().getDisconnected());
                result.setTwilioDisconnect(attrs.getDisconnected());
            }
        }

        if (attrs.getType() == ConversationType.GROUP) {
            var groupHistory = groupChatParticipantHistoryDao.findAll(
                    chatSpecificationGenerator.groupChatEntriesByConversationSid(conversation.getSid()).and(
                            chatSpecificationGenerator.notDeletedGroupChatMember()),
                    TwilioIdentityAware.class
            );
            var currentInDb = groupHistory.stream()
                    .map(TwilioIdentityAware::getTwilioIdentity)
                    .collect(Collectors.toSet());
            var participants = Participant.reader(chatServiceSid, conversation.getSid()).read(twilioRestClient);
            var currentInTwilio = StreamUtils.stream(participants)
                    .map(Participant::getIdentity)
                    .collect(Collectors.toSet());

            var currentFromAttr = attrs.getParticipantIdentities();

            result.setTwilioParticipants(currentInTwilio);
            result.setDbCurrentParticipants(currentInDb);
            result.setAttributesParticipants(currentFromAttr);

            if (!SetUtils.isEqualSet(currentInDb, currentInTwilio) ||
                    !SetUtils.isEqualSet(currentInDb, currentFromAttr) ||
                    !SetUtils.isEqualSet(currentInTwilio, currentFromAttr)) {
                result.setParticipantsMismatch(true);
            }
        }
        return result;
    }
}
