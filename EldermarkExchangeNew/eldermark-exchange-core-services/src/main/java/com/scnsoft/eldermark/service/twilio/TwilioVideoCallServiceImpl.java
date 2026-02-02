package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.projection.ConversationSidAware;
import com.scnsoft.eldermark.beans.twilio.chat.SystemMessage;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.beans.twilio.messages.video.*;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioSecurityFieldsAware;
import com.scnsoft.eldermark.beans.twilio.user.EntityBackedEmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantState;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantStateEndReason;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.video.v1.Room;
import com.twilio.rest.video.v1.room.Participant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class TwilioVideoCallServiceImpl implements VideoCallService, VideoCallWebhookService, ActiveVideoCallsSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(TwilioVideoCallServiceImpl.class);
    private static final long MILLIS_IN_HOUR = 3600000;

    @Value("${twilio.video.enabled}")
    private boolean isVideoEnabled;

    @Value("${twilio.video.room.callback.url}")
    private String roomCallbackUrl;

    @Value("${videocall.timeout}")
    private Long videoCallTimeoutMs;

    @Value("${videocall.max.participants}")
    private Integer callMaxParticipants;

    @Autowired
    private ChatService chatService;

    @Autowired
    private TwilioAccessTokenService tokenService;

    @Autowired
    private TwilioUserService userService;

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Autowired
    private VideoCallHistoryService videoCallHistoryService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private IncidentReportService incidentReportService;

    @Override
    @Transactional(readOnly = true)
    public boolean isVideoCallEnabled(Long employeeId) {
        return chatService.isChatEnabled(employeeId) && isVideoEnabled(userService.findById(employeeId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVideoCallEnabled(EmployeeTwilioSecurityFieldsAware employee) {
        return chatService.isChatEnabled(employee) && isVideoEnabled(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVideoCallEnabled(Employee employee) {
        return isVideoCallEnabled(new EntityBackedEmployeeTwilioCommunicationsUser(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVideoCallEnabled(String conversationSid, Long selfId) {
        return CollectionUtils.emptyIfNull(chatService.findEmployeeChatUsersByConversationSids(List.of(conversationSid), selfId)).stream()
                .allMatch(e -> isVideoEnabled(userService.findById(e.getEmployeeId())));
    }

    @Override
    public InitiateCallOutcome initiateCallInConversation(String conversationSid, String friendlyName, Set<Long> employeeIds,
                                                          Long callerEmployeeId) {
        logger.info("Initiating call in conversation [{}] for employees [{}], supplied friendly name is [{}]",
                conversationSid,
                employeeIds,
                friendlyName);
        var caller = userService.findById(callerEmployeeId);
        validateVideoEnabledForActor(caller);

        if (!chatService.isConnected(conversationSid)) {
            throw new BusinessException("Can't call in disconnected chats");
        }

        var callerIdentity = userService.toIdentity(caller);

        var conversationParticipants = chatService.getConversationParticipantMap(conversationSid);
        if (!conversationParticipants.containsKey(callerIdentity)) {
            throw new BusinessException("Caller is not a member of conversation");
        }

        String conversationFriendlyName;
        if (StringUtils.isNotEmpty(friendlyName)) {
            chatService.updateFriendlyName(conversationSid, friendlyName);
            conversationFriendlyName = friendlyName;
        } else {
            conversationFriendlyName = chatService.getFriendlyName(conversationSid);
        }

        Set<String> calleeIdentities;

        if (CollectionUtils.isEmpty(employeeIds)) {
            calleeIdentities = new HashSet<>(conversationParticipants.keySet());
        } else {
            //call is for specific chat participants
            calleeIdentities = ConversationUtils.employeeIdsToIdentity(employeeIds);
            if (!conversationParticipants.keySet().containsAll(calleeIdentities)) {
                throw new BusinessException("All the provided employees should be in conversation");
            }
        }
        calleeIdentities.remove(callerIdentity);

        var calleesIdentityMap = userService.mapFromIdentities(calleeIdentities);

        if (calleeIdentities.size() != calleesIdentityMap.size()) {
            throw new BusinessException("Failed to fetch one of employees by identities");
        }

        //remove inactive from the call
        calleesIdentityMap.entrySet().removeIf(e -> !isVideoCallEnabled(e.getValue()));
        if (calleesIdentityMap.isEmpty()) {
            throw new BusinessException("There is no one who can accept the call");
        }

        return initiateCall(calleesIdentityMap, callerIdentity, caller.getCareTeamRoleId(), callHistory -> {
            callHistory.setInitialConversationSid(conversationSid);
            callHistory.setFriendlyConversationName(conversationFriendlyName);
        });
    }

    @Override
    public InitiateCallOutcome initiateCallForEmployees(String friendlyName, Set<Long> employeeIds, Long callerEmployeeId,
                                                        Long participatingClientId) {
        logger.info("Initiating call for employees [{}], supplied friendly name is [{}]", employeeIds, friendlyName);
        var caller = userService.findById(callerEmployeeId);
        validateVideoEnabledForActor(caller);
        var callerIdentity = userService.toIdentity(caller);

        if (employeeIds == null || employeeIds.size() < 2) {
            throw new BusinessException("There must be at least two employees to initiate call");
        }

        if (!employeeIds.contains(callerEmployeeId)) {
            throw new BusinessException("Caller is not among employees");
        }

        var calleeIds = employeeIds.stream()
                .filter(id -> !id.equals(callerEmployeeId))
                .collect(Collectors.toSet());

        var calleesIdentityMap = userService.identityUserMapByIds(calleeIds);

        return initiateCall(calleesIdentityMap, callerIdentity, caller.getCareTeamRoleId(),
                callHistory -> {
                    callHistory.setInitialConversationSid(
                            chatService.getPersonalOrCreateConversation(employeeIds, callerEmployeeId, participatingClientId)
                    );
                    callHistory.setFriendlyConversationName(friendlyName);
                });
    }

    private InitiateCallOutcome initiateCall(Map<String, EmployeeTwilioCommunicationsUser> calleesIdentityMap,
                                             String callerIdentity, Long callerRoleId,
                                             Consumer<VideoCallHistory> conversationSetter) {
        if (videoCallHistoryService.isBusy(callerIdentity)) {
            throw new BusinessException("Can't start a new call - caller is busy");
        }

        var now = Instant.now();
        var callHistory = new VideoCallHistory(callerIdentity, now);

        var callerHistory = new VideoCallParticipantHistory(callHistory, null, callerIdentity, callerRoleId,
                VideoCallParticipantState.OUTGOING_CALL, now);

        validateCallees(callHistory, calleesIdentityMap);
        addCallees(callHistory, calleesIdentityMap, VideoCallParticipantState.INCOMING_CALL, now, callerIdentity, conversationSetter);
        videoCallHistoryService.createMissingReadStatuses(callHistory.getId());

        var outcome = new InitiateCallOutcome();
        outcome.setConversationSid(callHistory.getInitialConversationSid());
        outcome.setConversationFriendlyName(callHistory.getFriendlyConversationName());

        var caller = userService.findDtoByIdentity(callerIdentity);
        outcome.setCaller(caller);

        var allCallees = calleesIdentityMap.entrySet().stream()
                .map(e -> userService.convert(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
        outcome.setCallees(allCallees);

        if (callHistory.getRoomSid() == null) { //all busy
            outcome.setAreCalleesBusy(true);

            callHistory.setEndDatetime(Instant.now());

            callerHistory.setStateEndReason(VideoCallParticipantStateEndReason.BUSY_CALLEE);
            callerHistory.setStateEndDatetime(Instant.now());

            videoCallHistoryService.save(callHistory);
        } else {
            var withIncomingCall = VideoCallUtils.getIdentities(VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory()));
            outcome.setPendingIdentities(withIncomingCall);
            outcome.setRoomSid(callHistory.getRoomSid());
            outcome.setRoomAccessToken(tokenService.generateVideoToken(callerIdentity, callHistory.getRoomSid()));

        }

        return outcome;
    }

    @Override
    public InitiateCallOutcome initiateCallInIrConversation(Long incidentReportId, Long callerEmployeeId) {
        var conversationSid = incidentReportService.findById(incidentReportId, ConversationSidAware.class).getTwilioConversationSid();
        if (StringUtils.isEmpty(conversationSid)) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "Incident Report doesn't have conversation assigned");
        }
        //todo disconnected?
        var caller = userService.findById(callerEmployeeId);

        validateVideoEnabled(caller);

        if (!chatService.isAnyChatParticipant(conversationSid, List.of(callerEmployeeId))) {
            chatService.joinConversation(conversationSid, caller, callMaxParticipants);
        }

        return initiateCallInConversation(conversationSid, null, null, callerEmployeeId);
    }

    @Override
    public List<AddParticipantOutcomeItem> addParticipants(String roomSid, String friendlyName, Set<Long> addEmployeeIds,
                                                           Long actorEmployeeId, Long participatingClientId) {
        logger.info("Adding participants [{}] to call roomSid [{}], by [{}]", addEmployeeIds, roomSid, actorEmployeeId);
        if (CollectionUtils.isEmpty(addEmployeeIds)) {
            return Collections.emptyList();
        }

        var actor = userService.findById(actorEmployeeId);
        validateVideoEnabledForActor(actor);

        var callHistory = findActiveCallByRoomSid(roomSid);
        var actorIdentity = userService.toIdentity(actor);

        VideoCallUtils.findFirstIdentityEntry(VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()), actorIdentity)
                .orElseThrow(() -> new BusinessException("Actor must be on call in order to add new members"));

        var calleesIdentityMap = userService.identityUserMapByIds(addEmployeeIds);
        validateCallees(callHistory, calleesIdentityMap);

        //if underlying conversation is 1-to-1 chat - create new group chat and set updatedConversationSid to call history :
        var callConversation = VideoCallUtils.getConversation(callHistory);
        var conversationWithNewMembers = chatService.addParticipants(callConversation, friendlyName, addEmployeeIds,
                actorEmployeeId, participatingClientId);
        Set<String> conversationUpdatedUserIdentities = null;
        if (!conversationWithNewMembers.equals(callConversation)) {
            callHistory.setUpdatedConversationSid(conversationWithNewMembers);
            callHistory.setFriendlyConversationName(friendlyName);
            conversationUpdatedUserIdentities = VideoCallUtils.getActiveIdentities(callHistory.getParticipantsHistory());
            logger.info("Updated call [{}] with roomSid [{}] conversation from [{}] to [{}]",
                    callHistory.getId(), roomSid, callHistory.getInitialConversationSid(), callHistory.getUpdatedConversationSid());

            chatService.unregisterActiveCallChat(callHistory.getInitialConversationSid());
            chatService.registerActiveCallChat(conversationWithNewMembers, callHistory.getRoomSid());
        } else {
            //chat is already registered as having active call, just register call for added participants
            userService.registerActiveCallChatAsync(calleesIdentityMap.keySet(), callConversation);
        }

        var addedParticipants = addCallees(callHistory, calleesIdentityMap, VideoCallParticipantState.NEW_MEMBER_INCOMING_CALL, Instant.now(),
                actorIdentity, null);
        videoCallHistoryService.createMissingReadStatuses(callHistory.getId());

        if (CollectionUtils.isNotEmpty(conversationUpdatedUserIdentities)) {
            var conversationUpdatedMessage = new CallConversationUpdatedServiceMessage(callHistory.getRoomSid(), conversationWithNewMembers);
            var users = userService.fromIdentities(conversationUpdatedUserIdentities);
            users.forEach(user -> sendServiceMessage(user, conversationUpdatedMessage));
        }

        return getAddParticipantOutcomeItems(addedParticipants);
    }

    @Override
    public List<AddParticipantOutcomeItem> addNonActiveOrPendingCallConversationParticipants(String roomSid, Set<Long> nonActiveOrPendingCallEmployeeIds,
                                                                                             Long actorEmployeeId) {
        logger.info("Adding non active and not pending participants [{}] to call roomSid [{}], by [{}]", nonActiveOrPendingCallEmployeeIds, roomSid, actorEmployeeId);
        if (CollectionUtils.isEmpty(nonActiveOrPendingCallEmployeeIds)) {
            return Collections.emptyList();
        }

        var actor = userService.findById(actorEmployeeId);
        validateVideoEnabledForActor(actor);

        var callHistory = findActiveCallByRoomSid(roomSid);
        var actorIdentity = userService.toIdentity(actor);

        VideoCallUtils.findFirstIdentityEntry(VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()), actorIdentity)
                .orElseThrow(() -> new BusinessException("Actor must be on call in order to add conversation members"));

        var callConversation = VideoCallUtils.getConversation(callHistory);
        var calleesIdentityMap = userService.identityUserMapByIds(nonActiveOrPendingCallEmployeeIds);
        validateConversationMembersNonPendingCallees(callHistory, calleesIdentityMap, callConversation);

        //re-register active call for users in case someone was removed from the call previously
        userService.registerActiveCallChatAsync(calleesIdentityMap.keySet(), callConversation);

        var addedParticipants = addCallees(callHistory, calleesIdentityMap, VideoCallParticipantState.NEW_MEMBER_INCOMING_CALL, Instant.now(),
                actorIdentity, null);
        videoCallHistoryService.createMissingReadStatuses(callHistory.getId());


        return getAddParticipantOutcomeItems(addedParticipants);
    }

    private void validateCallees(VideoCallHistory callHistory, Map<String, EmployeeTwilioCommunicationsUser> calleesIdentityMap) {
        var currentMembers = VideoCallUtils.getActiveIdentities(callHistory.getParticipantsHistory());
        currentMembers.forEach(calleesIdentityMap::remove);
        if (calleesIdentityMap.isEmpty()) {
            return;
        }

        validateMaxCallMembers(currentMembers.size() + calleesIdentityMap.size());
        calleesIdentityMap.values().forEach(this::validateVideoEnabled);
    }

    private void validateConversationMembersNonPendingCallees(VideoCallHistory callHistory,
                                                              Map<String, EmployeeTwilioCommunicationsUser> calleesIdentityMap,
                                                              String callConversationSid) {
        var currentMembers = VideoCallUtils.getIdentities(VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()));
        currentMembers.forEach(calleesIdentityMap::remove);
        if (calleesIdentityMap.isEmpty()) {
            return;
        }

        var withIncomingCall = VideoCallUtils.getIdentities(VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory()));
        withIncomingCall.forEach(calleesIdentityMap::remove);
        if (calleesIdentityMap.isEmpty()) {
            return;
        }

        var conversationParticipants = chatService.getConversationParticipantMap(callConversationSid);
        if (!conversationParticipants.keySet().containsAll(calleesIdentityMap.keySet())) {
            throw new BusinessException("At least one of participants is not conversation member");
        }

        validateMaxCallMembers(currentMembers.size() + calleesIdentityMap.size());
        calleesIdentityMap.values().forEach(this::validateVideoEnabled);
    }

    private List<AddParticipantOutcomeItem> getAddParticipantOutcomeItems(List<Pair<EmployeeTwilioCommunicationsUser, VideoCallParticipantHistory>> addedParticipants) {
        return addedParticipants.stream()
                .map(pair -> {
                    var user = pair.getFirst();
                    var participant = pair.getSecond();

                    var result = new AddParticipantOutcomeItem();
                    result.setId(user.getId());
                    result.setIdentity(participant.getTwilioIdentity());
                    result.setFirstName(user.getFirstName());
                    result.setLastName(user.getLastName());
                    result.setIsBusy(participant.getStateEndReason() == VideoCallParticipantStateEndReason.BUSY_CALLEE);

                    return result;
                })
                .collect(Collectors.toList());
    }

    private List<Pair<EmployeeTwilioCommunicationsUser, VideoCallParticipantHistory>> addCallees(VideoCallHistory callHistory,
                                                                                                 Map<String, EmployeeTwilioCommunicationsUser> calleesIdentityMap,
                                                                                                 VideoCallParticipantState state,
                                                                                                 Instant addedDateTime,
                                                                                                 String addedByIdentity,
                                                                                                 Consumer<VideoCallHistory> conversationSetter) {
        if (calleesIdentityMap.isEmpty()) {
            return Collections.emptyList();
        }

        calleesIdentityMap.forEach((calleeIdentity, callee) -> {
            //check isBusy before creating participant so that just added participant not flushed to DB and considered busy
            var isBusy = videoCallHistoryService.isBusy(calleeIdentity);

            var participantHistory = new VideoCallParticipantHistory(callHistory,
                    null, calleeIdentity, callee.getCareTeamRoleId(), state, addedDateTime, addedByIdentity);

            if (isBusy) {
                participantHistory.setStateEndDatetime(addedDateTime);
                participantHistory.setStateEndReason(VideoCallParticipantStateEndReason.BUSY_CALLEE);
                logger.info("Callee [{}] is busy, conversation is [{}], roomSid is [{}]", calleeIdentity, callHistory.getInitialConversationSid(),
                        callHistory.getRoomSid());
            } else {
                if (callHistory.getRoomSid() == null) {
                    var room = createRoomResource();
                    callHistory.setRoomSid(room.getSid());
                    logger.info("Assigned room [{}] to call in conversation [{}]", callHistory.getRoomSid(),
                            callHistory.getInitialConversationSid());
                }
            }
        });

        if (callHistory.getInitialConversationSid() == null) {
            //when call is started for users list - conversation creation is delayed until all validations are passed
            conversationSetter.accept(callHistory);
            logger.info("Assigned conversation [{}] to call in room [{}]", callHistory.getInitialConversationSid(), callHistory.getRoomSid());
        }

        videoCallHistoryService.save(callHistory);

        //find saved to DB just added participants
        var addedParticipants = VideoCallUtils.filterWithIncomingCall(callHistory.getParticipantsHistory())
                .filter(participant -> calleesIdentityMap.containsKey(participant.getTwilioIdentity())
                        && addedDateTime.equals(participant.getStateDatetime()))
                .collect(Collectors.toList());

        if (VideoCallUtils.isCallerInRoom(callHistory)) {
            processCalleesAdded(callHistory, addedParticipants, false);
        } else {
            taskScheduler.schedule(() -> transactionTemplate.executeWithoutResult(transactionStatus ->
                            callerNotJoinedToRoomTimeout(callHistory.getId())),
                    Instant.now().plusMillis(videoCallTimeoutMs));
        }

        return addedParticipants.stream()
                .map(participant -> new Pair<>(calleesIdentityMap.get(participant.getTwilioIdentity()), participant))
                .collect(Collectors.toList());
    }

    private void callerNotJoinedToRoomTimeout(Long callHistoryId) {
        logger.info("callerNotJoinedToRoomTimeout event, call id is [{}]", callHistoryId);
        var callHistory = videoCallHistoryService.findLockedById(callHistoryId);
        logger.info("Fetched call history [{}]", callHistoryId);

        if (!VideoCallUtils.isActiveCall(callHistory)) {
            logger.info("Call [{}] in room [{}] is active", callHistory.getId(), callHistory.getRoomSid());
            return;
        }

        if (!VideoCallUtils.isCallerInRoom(callHistory)) {
            logger.info("Caller is not in the room [{}], call id [{}]", callHistory.getRoomSid(), callHistory.getId());
            endCall(callHistory, Instant.now(), EndCallMode.TIMEOUT, callHistory.getCallerTwilioIdentity());
        }
    }

    void processCalleesAdded(VideoCallHistory callHistory,
                             List<VideoCallParticipantHistory> whoWasAdded,
                             boolean sendInitToCaller) {
        var callMembers = VideoCallUtils.getActiveIdentities(callHistory.getParticipantsHistory());

        var callMembersPartitioned = userService.findDtoByIdentities(callMembers).stream()
                .collect(Collectors.partitioningBy(d -> callHistory.getCallerTwilioIdentity().equals(d.getIdentity())));

        var caller = callMembersPartitioned.getOrDefault(Boolean.TRUE, Collections.emptyList())
                .stream().findFirst()
                .orElseGet(() -> userService.findDtoByIdentity(callHistory.getCallerTwilioIdentity()));
        var callees = callMembersPartitioned.getOrDefault(Boolean.FALSE, Collections.emptyList());

        var addedWithIncomingCallParticipants = VideoCallUtils.filterWithActiveIncomingCall(whoWasAdded).collect(Collectors.toList());

        var whoWasAddedIdentityMap = userService.fromIdentities(VideoCallUtils.getActiveIdentities(addedWithIncomingCallParticipants))
                .stream().collect(StreamUtils.toMapOfUniqueKeys(userService::toIdentity));

        addedWithIncomingCallParticipants
                .forEach(addedParticipant -> {
                    var identity = addedParticipant.getTwilioIdentity();
                    var initiateCallServiceMessage = createInitiateCallServiceMessage(
                            callHistory,
                            caller,
                            callees,
                            identity
                    );

                    sendServiceMessage(whoWasAddedIdentityMap.get(identity), initiateCallServiceMessage);
                });

        if (sendInitToCaller) {
            var identity = callHistory.getCallerTwilioIdentity();
            var initiateCallServiceMessage = createInitiateCallServiceMessage(
                    callHistory,
                    caller,
                    callees,
                    identity
            );

            sendServiceMessage(userService.fromIdentity(identity), initiateCallServiceMessage);
        }

        scheduleTimeout(callHistory, addedWithIncomingCallParticipants, Instant.now());

        //notify everyone already on call about new pending users
        var alreadyOnCall = VideoCallUtils.filterWithOnCall(callHistory.getParticipantsHistory());
        notifyAboutIncomingCallMembers(callHistory, alreadyOnCall);
    }

    private InitiateCallServiceMessage createInitiateCallServiceMessage(VideoCallHistory callHistory, IdentityListItemDto caller, List<IdentityListItemDto> callees, String identity) {
        var initiateCallServiceMessage = new InitiateCallServiceMessage(callHistory.getRoomSid());
        initiateCallServiceMessage.setCaller(caller);
        initiateCallServiceMessage.setCallees(callees);
        initiateCallServiceMessage.setConversationSid(VideoCallUtils.getConversation(callHistory));
        initiateCallServiceMessage.setConversationFriendlyName(callHistory.getFriendlyConversationName());
        initiateCallServiceMessage.setRoomAccessToken(tokenService.generateVideoToken(identity, callHistory.getRoomSid()));
        return initiateCallServiceMessage;
    }

    private Room createRoomResource() {
        return Room.creator()
                .setStatusCallback(roomCallbackUrl)
                .setType(Room.RoomType.GROUP)
                .create(twilioRestClient);
    }

    private void scheduleTimeout(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> activeEntries,
                                 Instant startFrom) {
        var ids = CareCoordinationUtils.toIdsSet(activeEntries);
        if (CollectionUtils.isEmpty(ids)) {
            logger.info("Won't schedule timeout - empty activeEntries, call [{}]", callHistory.getId());
            return;
        } else {
            logger.info("Scheduling timeout for participants [{}]", ids);
        }
        taskScheduler.schedule(() -> transactionTemplate.executeWithoutResult(transactionStatus ->
                        timeout(callHistory.getId(), ids)),
                startFrom.plusMillis(videoCallTimeoutMs));
    }

    private void timeout(Long callHistoryId, Set<Long> participantHistoryIds) {
        logger.info("Callers timeout event in call [{}], participants [{}]", callHistoryId, participantHistoryIds);
        var callHistory = videoCallHistoryService.findLockedById(callHistoryId);
        logger.info("Fetched call [{}] by id", callHistory.getId());

        var timedOutIncomingCallEntries = VideoCallUtils
                .filterWithActiveIncomingCall(callHistory.getParticipantsHistory())
                .filter(ph -> participantHistoryIds.contains(ph.getId()))
                .collect(Collectors.toList());

        if (!timedOutIncomingCallEntries.isEmpty()) {
            var timeoutRemoveMemberStrategy = new TimeoutRemoveMembersStrategy();
            timeoutRemoveMemberStrategy.removeMembersFromCall(callHistory, timedOutIncomingCallEntries, Instant.now(), null);
        } else {
            logger.info("No one timed oud in call [{}]", callHistoryId);
        }
    }

    @Override
    //intended to be invoked by webhook
    public void connectedToRoom(String roomSid, String participantSid, String participantIdentity, Instant when) {
        var callHistory = findActiveCallByRoomSid(roomSid);

        if (callHistory.getCallerTwilioIdentity().equals(participantIdentity) && !VideoCallUtils.didCallStart(callHistory)) {
            processCallerFirstTimeConnected(callHistory, participantSid, when);
        } else {
            acceptedOrJoinedCall(callHistory, participantSid, participantIdentity, when);
        }
    }

    private void processCallerFirstTimeConnected(VideoCallHistory callHistory, String participantSid, Instant when) {
        VideoCallUtils.findFirstIdentityEntry(
                VideoCallUtils.filterWithActiveState(callHistory.getParticipantsHistory()), callHistory.getCallerTwilioIdentity())
                .ifPresent(entry -> {
                    entry.setTwilioRoomParticipantSid(participantSid);
                    videoCallHistoryService.save(callHistory);
                    logger.info("Caller first-time connected to room [{}] in call [{}]", callHistory.getRoomSid(), callHistory.getId());

                    //send InitiateCallServiceMessages only after caller enters the room, including self
                    processCalleesAdded(callHistory, callHistory.getParticipantsHistory(), true);

                    chatService.registerActiveCallChat(callHistory.getInitialConversationSid(), callHistory.getRoomSid());
                });
    }

    private void acceptedOrJoinedCall(VideoCallHistory callHistory, String participantSid, String calleeIdentity, Instant acceptedTime) {
        logger.info("Participant with identity [{}] connected to call [{}], roomSid [{}]",
                calleeIdentity, callHistory.getId(), callHistory.getRoomSid());

        if (VideoCallUtils.participantWasRemoved(callHistory.getParticipantsHistory(), calleeIdentity)) {
            logger.warn("Participant [{}] attempted to connect to call [{}], roomSid [{}], but he was removed from the call",
                    calleeIdentity, callHistory.getId(), callHistory.getRoomSid());
            disconnectParticipantFromRoom(callHistory.getRoomSid(), calleeIdentity);
            return;
        }

        VideoCallUtils.findFirstIdentityEntry(
                VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory()), calleeIdentity)
                .ifPresent(calleeIncomingCallEntry -> {
                    calleeIncomingCallEntry.setStateEndDatetime(acceptedTime);
                    calleeIncomingCallEntry.setStateEndReason(VideoCallParticipantStateEndReason.CALL_ACCEPTED);
                });


        var calleeRoleId = userService.getEmployeeSystemRoleId(calleeIdentity);
        var calleeOnCallEntry = new VideoCallParticipantHistory(callHistory, participantSid, calleeIdentity, calleeRoleId,
                VideoCallParticipantState.ON_CALL, acceptedTime);

        if (callHistory.getStartDatetime() == null) {
            callHistory.setStartDatetime(acceptedTime);
        }

        boolean callerJoined = false;
        var callerOutgoingCallEntry = VideoCallUtils.findCallerOutgoingCallEntry(callHistory);
        if (VideoCallUtils.isActiveEntry(callerOutgoingCallEntry)) {
            //update caller if he is not on the call already
            callerOutgoingCallEntry.setStateEndReason(VideoCallParticipantStateEndReason.CALL_ACCEPTED);
            callerOutgoingCallEntry.setStateEndDatetime(acceptedTime);
            callerOutgoingCallEntry.setStateEndCausedByIdentity(calleeIdentity);

            var callerRoleId = userService.getEmployeeSystemRoleId(callerOutgoingCallEntry.getTwilioIdentity());

            var callerOnCallEntry = new VideoCallParticipantHistory(callHistory,
                    callerOutgoingCallEntry.getTwilioRoomParticipantSid(),
                    callerOutgoingCallEntry.getTwilioIdentity(),
                    callerRoleId,
                    VideoCallParticipantState.ON_CALL, acceptedTime);

            callerJoined = true;
        }

        videoCallHistoryService.save(callHistory);

        var onCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()).collect(Collectors.toList());
        var onCallDtos = userService.findDtoByIdentities(
                VideoCallUtils.getIdentities(onCall)
        );

        //send joined message to self and others
        var joinedMessage = new CallMemberJoinedServiceMessage(callHistory.getRoomSid(), calleeOnCallEntry.getTwilioIdentity(), onCallDtos);
        sendSameServiceMessage(onCall, joinedMessage);
        if (callerJoined) {
            joinedMessage.setIdentity(callHistory.getCallerTwilioIdentity());
            sendSameServiceMessage(onCall, joinedMessage);
        }

        //and also notify user who just joined and everyone else on the call about pending users
        notifyAboutIncomingCallMembers(callHistory, Stream.of(calleeOnCallEntry));
    }

    private void notifyAboutIncomingCallMembers(VideoCallHistory callHistory, Stream<VideoCallParticipantHistory> whoNotify) {
        var allIncomingMembers = VideoCallUtils.getIdentities(VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory()));
        if (allIncomingMembers.isEmpty()) {
            return;
        }

        var pendingParticipantsMessage = new PendingCallMembersServiceMessage(callHistory.getRoomSid(),
                userService.findDtoByIdentities(allIncomingMembers));
        sendSameServiceMessage(whoNotify, pendingParticipantsMessage);
    }

    @Override
    public void declineCall(String roomSid, Long calleeEmployeeId) {
        declineCall(roomSid, calleeEmployeeId, null);
    }

    /**
     * devicePushNotificationToken is needed to exclude push notification to the device which called
     * decline endpoint.
     * Specifically, this is needed for IOS devices, because otherwise device will try to process 'declined'
     * push notification for the call, which it already declined via endpoint and therefore which is not active
     * anymore. Because call is not active anymore, IOS device won't be able to submit decline to Call kit
     * which will cause Apple to disable all VOIP notifications for this device and the only way to fix it
     * would be to reinstall the app.
     *
     * @param roomSid
     * @param calleeEmployeeId
     * @param devicePushNotificationToken
     */
    @Override
    public void declineCall(String roomSid, Long calleeEmployeeId, String devicePushNotificationToken) {
        logger.info("Callee [{}] declined call in room [{}], device token is {}", calleeEmployeeId, roomSid,
                StringUtils.isNotEmpty(devicePushNotificationToken) ? "present" : "empty");
        var callHistory = findActiveCallByRoomSid(roomSid);

        //validate callee is call member
        var calleeIdentity = ConversationUtils.employeeIdToIdentity(calleeEmployeeId);

        var withIncomingCallState = VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory());

        var calleeParticipantHistory = VideoCallUtils.findFirstIdentityEntry(withIncomingCallState, calleeIdentity)
                .orElseThrow(() -> new BusinessException("Callee is not being called in given room"));

        var declineRemoveStrategy = new DeclinedRemoveMemberStrategy(devicePushNotificationToken);
        declineRemoveStrategy.removeMembersFromCall(callHistory, Collections.singletonList(calleeParticipantHistory), Instant.now(), null);
    }

    @Override
    //intended to be invoked by webhook
    public void disconnectedFromRoom(String roomSid, String participantSid, Instant disconnectTime) {
        //call can already become inactive if 'room-end' was received before 'participant-disconnect'
        var callHistory = videoCallHistoryService.findLockedByRoomSid(roomSid);

        var activeParticipantsInRoom = VideoCallUtils.filterActiveInRoom(callHistory.getParticipantsHistory())
                .collect(Collectors.toList());

        //if participantSid is not among list - he was already processed
        activeParticipantsInRoom.stream()
                .filter(entry -> participantSid.equals(entry.getTwilioRoomParticipantSid()))
                .findFirst() //there should be only one active entry per participantSid
                .ifPresent(disconnectedParticipantEntry -> {
                    var didCallStart = VideoCallUtils.didCallStart(callHistory);
                    if (disconnectedParticipantEntry.getTwilioIdentity().equals(callHistory.getCallerTwilioIdentity())
                            && !didCallStart) {
                        endCall(callHistory, disconnectTime, EndCallMode.CANCEL, callHistory.getCallerTwilioIdentity());
                    } else {
                        var callMemberLeftRemoveMemberStrategy = new CallMemberLeftRemoveMemberStrategy();
                        callMemberLeftRemoveMemberStrategy.removeMembersFromCall(callHistory,
                                Collections.singletonList(disconnectedParticipantEntry),
                                disconnectTime, null);
                    }
                });
    }

    @Override
    public void removeParticipants(String roomSid, Set<String> identitiesToRemove, Long actorEmployeeId) {
        logger.info("User [{}] requested to remove [{}] from call with roomSid [{}]",
                actorEmployeeId, identitiesToRemove, roomSid);

        var actorEmployee = userService.findById(actorEmployeeId);
        validateVideoEnabledForActor(actorEmployee);

        var callHistory = findActiveCallByRoomSid(roomSid);

        var actorIdentity = ConversationUtils.employeeIdToIdentity(actorEmployeeId);

        var conversationSid = VideoCallUtils.getConversation(callHistory);
        var chatOwner = chatService.findOwnerIdentity(conversationSid);
        if (chatOwner.filter(owner -> owner.equals(actorIdentity)).isEmpty()) {
            throw new BusinessException("Actor is not chat creator");
        }

        if (identitiesToRemove.contains(actorIdentity)) {
            throw new BusinessException("Can't remove self from the call");
        }

        var participantsToRemove = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory())
                .filter(entry -> identitiesToRemove.contains(entry.getTwilioIdentity()))
                .collect(Collectors.toList());

        var removeStrategy = new ParticipantsRemovedRemoveMemberStrategy();
        removeStrategy.removeMembersFromCall(callHistory, participantsToRemove, Instant.now(), actorIdentity);
    }

    @Override
    public void roomEnded(String roomSid, Instant when) {
        //just quickly write call end date if already not set so that participants are not busy anymore.
        //participant states and full end call logic will be performed by disconnectedFromRoom.
        //Participant-disconnect webhooks will be fired by twilio along with room-end event

        //typically call should already be normally ended by endCall logic.
        //this webhook is implemented just in case twilio itself forces room end without
        //participant-disconnect events (is that even possible?)

        videoCallHistoryService.writeCallEndTimeIfMissing(roomSid, when);
    }

    private List<VideoCallParticipantHistory> endCall(VideoCallHistory callHistory, Instant callEndTime, EndCallMode endCallMode,
                                                      String causedByIdentity) {
        logger.info("Ending call [{}], roomSid [{}], mode [{}], caused by [{}], end time [{}]", callHistory.getId(),
                callHistory.getRoomSid(), endCallMode, causedByIdentity, callEndTime);
        var missedCallParticipants = new ArrayList<VideoCallParticipantHistory>();

        var deactivatedParticipants = VideoCallUtils.filterWithActiveState(callHistory.getParticipantsHistory())
                .peek(participantHistory -> {
                    participantHistory.setStateEndDatetime(callEndTime);
                    participantHistory.setStateEndReason(endCallMode.resolveEndReason(participantHistory.getState()));
                    participantHistory.setStateEndCausedByIdentity(causedByIdentity);

                    if (VideoCallUtils.isIncomingCall(participantHistory)) {
                        missedCallParticipants.add(participantHistory);
                    }

                })
                .collect(Collectors.toList());

        if (callHistory.getEndDatetime() == null) {
            callHistory.setEndDatetime(callEndTime);
        }

        try {
            var conversationSid = VideoCallUtils.getConversation(callHistory);
            var friendlyName = chatService.getFriendlyName(conversationSid);
            callHistory.setFriendlyConversationName(friendlyName);
        } catch (Exception e) {
            logger.info("Failed to update conversation friendly name on call end");
        }

        videoCallHistoryService.save(callHistory);

        chatService.unregisterActiveCallChat(VideoCallUtils.getConversation(callHistory));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
                //send update to twilio only after all the changes are stored in database,
                //otherwise it leads to concurrent data access: endCall changes are not yet flushed
                //but Twilio generates new room-end and participant-disconnected events which read old data
                //from DB
                Room.updater(callHistory.getRoomSid(), Room.RoomStatus.COMPLETED).update(twilioRestClient);
            }
        });

        sendIncomingCallMissedNotifications(missedCallParticipants);

        var endCallServiceMessage = new EndCallServiceMessage(callHistory.getRoomSid());
        sendSameServiceMessage(deactivatedParticipants, endCallServiceMessage);

        writeCallEnd(callHistory);

        return deactivatedParticipants;
    }

    private void writeCallEnd(VideoCallHistory callHistory) {
        if (!VideoCallUtils.isCallerInRoom(callHistory)) {
            //dont write anything if call never actually even initiated
            return;
        }

        var conversation = VideoCallUtils.getConversation(callHistory);

        if (callHistory.getStartDatetime() == null) {
            chatService.writeSystemMessage(conversation, SystemMessage.CALL_MISSED, "Missed call");
        } else {
            var durationMillis = callHistory.getEndDatetime().toEpochMilli() - callHistory.getStartDatetime().toEpochMilli();

            var duration = DurationFormatUtils.formatDuration(durationMillis, durationMillis >= MILLIS_IN_HOUR ? "HH:mm:ss" : "mm:ss");
            var body = "Call " + duration;

            chatService.writeSystemMessage(conversation, SystemMessage.CALL_END, body);
        }
    }

    private void sendIncomingCallMissedNotifications(Collection<VideoCallParticipantHistory> historyEntries) {
//        var missedCall = historyEntries.stream().filter(VideoCallUtils::isIncomingCall).collect(Collectors.toList());
//        if (!missedCall.isEmpty()) {
//            do nothing - currently there is no business requirement to send missed call notifications
//
//        }
    }

    private VideoCallHistory findActiveCallByRoomSid(String roomSid) {
        return findOptionalActiveCallByRoomSid(roomSid).orElseThrow(() -> new BusinessException("Active call was not found"));
    }

    private Optional<VideoCallHistory> findOptionalActiveCallByRoomSid(String roomSid) {
        var callHistory = videoCallHistoryService.findLockedByRoomSid(roomSid);
        logger.info("Fetched call [{}] by roomSid [{}]", callHistory == null ? null : callHistory.getId(), roomSid);

        if (!VideoCallUtils.isActiveCall(callHistory)) {
            return Optional.empty();
        }

        return Optional.of(callHistory);
    }

    private void validateVideoEnabled(EmployeeTwilioCommunicationsUser user) {
        if (!isVideoEnabled(user)) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE,
                    "Video functionality is not available for employee [" + user.getId() + "]");
        }
    }

    private void validateVideoEnabledForActor(EmployeeTwilioCommunicationsUser user) {
        if (!isVideoEnabled(user)) {
            throw new BusinessException(BusinessExceptionType.VIDEO_CALL_DISABLED);
        }
    }

    private void validateMaxCallMembers(int size) {
        if (size > callMaxParticipants) {
            throw new BusinessException("Exceeded max number of call members(" + callMaxParticipants + "): " + size);
        }
    }

    private boolean isVideoEnabled(EmployeeTwilioSecurityFieldsAware user) {
        return isVideoEnabled && Boolean.TRUE.equals(user.getOrganizationIsVideoEnabled());
    }

    private void sendSameServiceMessage(Collection<VideoCallParticipantHistory> participantHistories,
                                        ServiceMessage message,
                                        String devicePushNotificationToken) {
        sendSameServiceMessage(participantHistories.stream(), message, devicePushNotificationToken);
    }

    private void sendSameServiceMessage(Collection<VideoCallParticipantHistory> participantHistories, ServiceMessage message) {
        sendSameServiceMessage(participantHistories, message, null);
    }

    private void sendSameServiceMessage(Stream<VideoCallParticipantHistory> participantHistories, ServiceMessage message) {
        sendSameServiceMessage(participantHistories, message, null);
    }

    private void sendSameServiceMessage(Stream<VideoCallParticipantHistory> participantHistories, ServiceMessage message,
                                        String devicePushNotificationToken) {
        var users = userService.fromIdentities(VideoCallUtils.getIdentities(participantHistories));
        users.forEach(user -> sendServiceMessage(user, message, devicePushNotificationToken));
    }

    private void sendServiceMessage(VideoCallParticipantHistory entry, ServiceMessage message) {
        sendServiceMessage(entry, message, null);
    }

    private void sendServiceMessage(VideoCallParticipantHistory entry, ServiceMessage message, String devicePushNotificationToken) {
        var user = userService.fromIdentity(entry.getTwilioIdentity());
        sendServiceMessage(user, message, devicePushNotificationToken);
    }

    private void sendServiceMessage(EmployeeTwilioCommunicationsUser user, ServiceMessage message) {
        sendServiceMessage(user, message, null);
    }

    private void sendServiceMessage(EmployeeTwilioCommunicationsUser user, ServiceMessage message, String devicePushNotificationToken) {
        try {
            chatService.sendServiceMessage(user, message, devicePushNotificationToken);
            logger.info("Sent service message type [{}] to user [{}]", message.getType(), user.getId());
        } catch (Exception ex) {
            logger.warn("Failed to send service message [{}] to [{}]", message, user.getId(), ex);
        }
    }

    private enum EndCallMode {
        CANCEL {
            @Override
            public VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state) {
                switch (state) {
                    case ON_CALL:
                        throw new BusinessException("The call have already started");
                    case OUTGOING_CALL:
                        return VideoCallParticipantStateEndReason.CALL_CANCELLED;
                    case INCOMING_CALL:
                    case NEW_MEMBER_INCOMING_CALL:
                        return VideoCallParticipantStateEndReason.CALL_MISSED;
                }
                throw unknownState(state);
            }

            ;
        },

        TIMEOUT {
            @Override
            public VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state) {
                switch (state) {
                    case OUTGOING_CALL:
                    case ON_CALL:
                        return VideoCallParticipantStateEndReason.CALL_TIMEOUT;
                    case INCOMING_CALL:
                    case NEW_MEMBER_INCOMING_CALL:
                        return VideoCallParticipantStateEndReason.CALL_MISSED;
                }
                throw unknownState(state);
            }
        },

        DECLINED {
            @Override
            public VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state) {
                switch (state) {
                    case OUTGOING_CALL:
                    case ON_CALL:
                        return VideoCallParticipantStateEndReason.CALL_DECLINED;
                    case INCOMING_CALL:
                    case NEW_MEMBER_INCOMING_CALL:
                        throw new BusinessException("There are other participants with incoming call state");
                }
                throw new BusinessException("Can't end call - there are other participants");
            }
        },

        LAST_PARTICIPANT_LEFT {
            @Override
            public VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state) {
                switch (state) {
                    case INCOMING_CALL:
                    case NEW_MEMBER_INCOMING_CALL:
                        return VideoCallParticipantStateEndReason.CALL_MISSED;
                    case ON_CALL:
                    case OUTGOING_CALL:
                        return VideoCallParticipantStateEndReason.CALL_END;
                }
                throw unknownState(state);
            }
        },

        LAST_PARTICIPANT_REMOVED {
            @Override
            public VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state) {
                switch (state) {
                    case OUTGOING_CALL: //only caller on call remove members
                        throw new BusinessException("The call have not started yet");
                    case ON_CALL:
                        return VideoCallParticipantStateEndReason.CALL_END;
                    case INCOMING_CALL:
                    case NEW_MEMBER_INCOMING_CALL:
                        throw new BusinessException("Someone is being called yet");
                }
                throw unknownState(state);
            }
        };

        private static IllegalArgumentException unknownState(VideoCallParticipantState state) {
            return new IllegalArgumentException("Unknown VideoCallParticipantState: " + state);
        }

        public abstract VideoCallParticipantStateEndReason resolveEndReason(VideoCallParticipantState state);
    }

    private abstract class BaseRemoveMembersStrategy {

        void removeMembersFromCall(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries,
                                   Instant removedTime, String causedByIdentity) {
            if (CollectionUtils.isEmpty(removedEntries)) {
                return;
            }

            logger.info("Removing members [{}] from call, strategy is [{}]", VideoCallUtils.getIdentities(removedEntries), this.getClass().getSimpleName());
            if (!isRemovedEntriesSizeValid(removedEntries.size())) {
                throw new IllegalArgumentException("Illegal removedEntries size: " + removedEntries.size());
            }

            removedEntries.forEach(removedEntry -> {
                removedEntry.setStateEndDatetime(removedTime);
                removedEntry.setStateEndReason(getRemovedEntryEndReason(removedEntry));
                removedEntry.setStateEndCausedByIdentity(causedByIdentity);
            });

            videoCallHistoryService.save(callHistory);

            postRemovedStateUpdate(callHistory, removedEntries);

            if (shouldEndCall(callHistory)) {
                TwilioVideoCallServiceImpl.this.endCall(callHistory, removedTime, getCallEndMode(),
                        resolveCallEndCausedBy(callHistory, removedEntries, causedByIdentity));
            }
        }

        protected abstract boolean isRemovedEntriesSizeValid(int removedEntriesSize);

        protected abstract VideoCallParticipantStateEndReason getRemovedEntryEndReason(VideoCallParticipantHistory entry);

        protected abstract void postRemovedStateUpdate(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries);

        protected abstract boolean shouldEndCall(VideoCallHistory callHistory);

        protected abstract EndCallMode getCallEndMode();

        protected abstract String resolveCallEndCausedBy(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries, String causedByIdentity);
    }

    private class DeclinedRemoveMemberStrategy extends BaseRemoveMembersStrategy {

        private final String devicePushNotificationToken;

        public DeclinedRemoveMemberStrategy(String devicePushNotificationToken) {
            this.devicePushNotificationToken = devicePushNotificationToken;
        }

        @Override
        protected boolean isRemovedEntriesSizeValid(int removedEntriesSize) {
            //only one person can decline call per invocation
            return removedEntriesSize == 1;
        }

        @Override
        protected VideoCallParticipantStateEndReason getRemovedEntryEndReason(VideoCallParticipantHistory entry) {
            return VideoCallParticipantStateEndReason.CALL_DECLINED;
        }

        @Override
        protected void postRemovedStateUpdate(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries) {
            var leftInRoomAndSelf = VideoCallUtils.filterActiveInRoom(callHistory.getParticipantsHistory())
                    .collect(Collectors.toList());
            leftInRoomAndSelf.addAll(removedEntries);

            removedEntries.forEach(declinedEntry -> {
                var declinedMessage = new CallMemberDeclinedServiceMessage(callHistory.getRoomSid(), declinedEntry.getTwilioIdentity());
                sendSameServiceMessage(leftInRoomAndSelf, declinedMessage, devicePushNotificationToken);
            });
        }

        @Override
        protected boolean shouldEndCall(VideoCallHistory callHistory) {
            var leftWithIncomingCallState = VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory())
                    .count();
            var leftOnCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory())
                    .count();

            //end the call for the only one who left in the room. It can be either a caller waiting for callee to
            //accept the call (leftOnCall = 0) or someone who hangs in group call and waits for the last one to
            //accept call after everyone else disconnected from the call (leftOnCall = 1)
            return leftWithIncomingCallState == 0 && leftOnCall < 2;
        }

        @Override
        protected EndCallMode getCallEndMode() {
            return EndCallMode.DECLINED;
        }

        @Override
        protected String resolveCallEndCausedBy(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries, String causedByIdentity) {
            //the one who declined the call
            return removedEntries.stream().findFirst().map(VideoCallParticipantHistory::getTwilioIdentity).orElseThrow();
        }
    }

    private class TimeoutRemoveMembersStrategy extends BaseRemoveMembersStrategy {

        @Override
        protected boolean isRemovedEntriesSizeValid(int removedEntriesSize) {
            //multiple incoming calls added in single batch will timeout at the same time
            return removedEntriesSize > 0;
        }

        @Override
        protected VideoCallParticipantStateEndReason getRemovedEntryEndReason(VideoCallParticipantHistory entry) {
            return VideoCallParticipantStateEndReason.CALL_MISSED;
        }

        @Override
        protected void postRemovedStateUpdate(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries) {
            var timedOutIdentities = VideoCallUtils.getIdentities(removedEntries);
            var timeoutMessage = new CallMembersTimeoutServiceMessage(callHistory.getRoomSid(), timedOutIdentities);

            var inRoom = VideoCallUtils.filterActiveInRoom(callHistory.getParticipantsHistory());
            //send to both ones in room and ones who timed out
            sendSameServiceMessage(Stream.concat(inRoom, removedEntries.stream()), timeoutMessage);

            sendIncomingCallMissedNotifications(removedEntries);
        }

        @Override
        protected boolean shouldEndCall(VideoCallHistory callHistory) {
            var leftWithIncomingCallState = VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory())
                    .count();
            var leftOnCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory())
                    .count();

            //end the call for the only one who left in the room. It can be either a caller who waited for callee to
            //accept the call (leftOnCall = 0) or someone who hangs in group call and waits for the last one to
            //accept call after everyone else disconnected from the call (leftOnCall = 1)
            return leftWithIncomingCallState == 0 && leftOnCall < 2;
        }

        @Override
        protected EndCallMode getCallEndMode() {
            return EndCallMode.TIMEOUT;
        }

        @Override
        protected String resolveCallEndCausedBy(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries, String causedByIdentity) {
            //nobody
            return null;
        }
    }

    private class CallMemberLeftRemoveMemberStrategy extends BaseRemoveMembersStrategy {

        @Override
        protected boolean isRemovedEntriesSizeValid(int removedEntriesSize) {
            //only one callee leaves per invocation
            return removedEntriesSize == 1;
        }

        @Override
        protected VideoCallParticipantStateEndReason getRemovedEntryEndReason(VideoCallParticipantHistory entry) {
            return VideoCallParticipantStateEndReason.PARTICIPANT_LEFT;
        }

        @Override
        protected void postRemovedStateUpdate(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries) {
            removedEntries.forEach(entry -> {
                var leftCallMessage = new CallMemberLeftServiceMessage(callHistory.getRoomSid(), entry.getTwilioIdentity());
                sendServiceMessage(entry, leftCallMessage);
            });
        }

        @Override
        protected boolean shouldEndCall(VideoCallHistory callHistory) {
            var leftWithIncomingCallState = VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory())
                    .count();
            var leftOnCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory())
                    .count();

            //end the call for the only one who left in the room. It can be the last one on the call who waited for
            //call accept (leftOnCall == 0 and any leftWithIncomingCallState)
            //or if last one left in the room and no one else is being called (leftOnCall == 1 && leftWithIncomingCallState == 0
            return leftOnCall == 0 || leftOnCall == 1 && leftWithIncomingCallState == 0;
        }

        @Override
        protected EndCallMode getCallEndMode() {
            return EndCallMode.LAST_PARTICIPANT_LEFT;
        }

        @Override
        protected String resolveCallEndCausedBy(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries, String causedByIdentity) {
            //the one who left
            return removedEntries.stream().findFirst().map(VideoCallParticipantHistory::getTwilioIdentity).orElseThrow();
        }
    }

    private class ParticipantsRemovedRemoveMemberStrategy extends BaseRemoveMembersStrategy {

        @Override
        protected boolean isRemovedEntriesSizeValid(int removedEntriesSize) {
            //multiple users can be removed in a single batch
            return removedEntriesSize > 0;
        }

        @Override
        protected VideoCallParticipantStateEndReason getRemovedEntryEndReason(VideoCallParticipantHistory entry) {
            //either kicked for ON_CALL or missed for incoming calls?
            return VideoCallParticipantStateEndReason.PARTICIPANT_REMOVED;
        }

        @Override
        protected void postRemovedStateUpdate(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries) {
            removeParticipantsFromRoom(callHistory, removedEntries);
            //unregister call for removed participants so that they can no longer join the call via Join button
            userService.unregisterActiveCallChatAsync(
                    VideoCallUtils.getIdentities(removedEntries),
                    VideoCallUtils.getConversation(callHistory)
            );

            removedEntries.forEach(
                    removedEntry -> {
                        var removedMessage = new CallMemberRemovedServiceMessage(
                                callHistory.getRoomSid(),
                                removedEntry.getTwilioIdentity(),
                                removedEntry.getStateEndCausedByIdentity()
                        );
                        sendServiceMessage(removedEntry, removedMessage);
                    }
            );

            var currentlyOnCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory());
            notifyAboutIncomingCallMembers(callHistory, currentlyOnCall); //or create special PendingMemberRemoved service message?
        }

        private void removeParticipantsFromRoom(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries) {
            removedEntries.stream()
                    .map(VideoCallParticipantHistory::getTwilioRoomParticipantSid)
                    .filter(StringUtils::isNotEmpty)
                    .forEach(participantSid -> disconnectParticipantFromRoom(callHistory.getRoomSid(), participantSid));

        }

        @Override
        protected boolean shouldEndCall(VideoCallHistory callHistory) {
            var leftWithIncomingCallState = VideoCallUtils.filterWithActiveIncomingCall(callHistory.getParticipantsHistory())
                    .count();
            var leftOnCall = VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory())
                    .count();

            //the one who removed everyone is not waiting for anyone else to accept the call
            return leftOnCall == 1 && leftWithIncomingCallState == 0;
//            leftOnCall == 0 is not possible - cant remove self
        }

        @Override
        protected EndCallMode getCallEndMode() {
            return EndCallMode.LAST_PARTICIPANT_REMOVED;
        }

        @Override
        protected String resolveCallEndCausedBy(VideoCallHistory callHistory, Collection<VideoCallParticipantHistory> removedEntries, String causedByIdentity) {
            //who removed everyone
            return causedByIdentity;
        }
    }

    private void disconnectParticipantFromRoom(String roomSid, String participantSid) {
        Participant.updater(roomSid, participantSid)
                .setStatus(Participant.Status.DISCONNECTED)
                .update(twilioRestClient);
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<String, String> getActiveCallRoomWithToken(Long actorEmployeeId, String conversationSid) {
        logger.info("Fetching room token in conversation [{}] for [{}]", conversationSid, actorEmployeeId);

        var actorEmployee = userService.findById(actorEmployeeId);
        validateVideoEnabledForActor(actorEmployee);

        if (!chatService.isAnyChatParticipant(conversationSid, Collections.singletonList(actorEmployeeId))) {
            throw new BusinessException("Actor is not a member of conversation");
        }

        var callHistory = videoCallHistoryService.findLockedActiveByConversationSid(conversationSid)
                .orElseThrow(() -> new BusinessException("Active call is not found"));

        var actorIdentity = ConversationUtils.employeeIdToIdentity(actorEmployeeId);

        if (VideoCallUtils.participantWasRemoved(callHistory.getParticipantsHistory(), actorIdentity)) {
            throw new BusinessException("Actor was removed from call");
        }

        return new Pair<>(callHistory.getRoomSid(), tokenService.generateVideoToken(actorIdentity, callHistory.getRoomSid()));
    }

    @Override
    public void muteParticipant(String roomSid, Long employeeId, Long actorEmployeeId) {
        var callHistory = findActiveCallByRoomSid(roomSid);

        var actorIdentity = ConversationUtils.employeeIdToIdentity(actorEmployeeId);
        var employeeIdentity = ConversationUtils.employeeIdToIdentity(employeeId);

        VideoCallUtils.findFirstIdentityEntry(
                VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()),
                actorIdentity
        )
                .orElseThrow(() -> new BusinessException("Actor must be on call in order to mute members"));

        var participantToBeMuted = VideoCallUtils.findFirstIdentityEntry(
                VideoCallUtils.filterWithActiveOnCall(callHistory.getParticipantsHistory()),
                employeeIdentity
        )
                .orElseThrow(() -> new BusinessException("Muted user must be on call"));

        var muteParticipantServiceMessage = new CallMemberMuteServiceMessage(callHistory.getRoomSid(), employeeIdentity, actorIdentity);
        sendServiceMessage(participantToBeMuted, muteParticipantServiceMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCallActiveAndEmployeeOnCallOrHasIncomingCall(String roomSid, Long employeeId) {
        return findOptionalActiveCallByRoomSid(roomSid).flatMap(callHistory -> {
            var employeeIdentity = ConversationUtils.employeeIdToIdentity(employeeId);

            return VideoCallUtils.getLatestHistoryEntryForIdentity(callHistory.getParticipantsHistory(), employeeIdentity)
                    .map(employeeLatestEntry ->
                            VideoCallUtils.isActiveEntry(employeeLatestEntry) &&
                                    (VideoCallUtils.isIncomingCall(employeeLatestEntry)) ||
                                    VideoCallUtils.isOnCall(employeeLatestEntry));

        }).orElse(false);
    }

    @Override
    public void synchronizeActiveCalls() {
        if (!isVideoEnabled) {
            logger.info("Won't synchronize active calls - calls are disabled");
            return;
        }
        var activeCalls = videoCallHistoryService.findActiveCalls();
        for (var videoCall : activeCalls) {
            var room = fetchRoomResource(videoCall.getRoomSid());
            if (!Room.RoomStatus.IN_PROGRESS.equals(room.getStatus())) {
                logger.info("Detected non-active call with room [{}], conversation [{}] without endDate " +
                        "in database, will end call", room.getSid(), VideoCallUtils.getConversation(videoCall));
                videoCallHistoryService.writeCallEndTimeIfMissing(
                        room.getSid(),
                        Optional.ofNullable(room.getEndTime())
                                .map(ZonedDateTime::toInstant)
                                .orElse(Instant.now())
                );
                chatService.unregisterActiveCallChat(VideoCallUtils.getConversation(videoCall));
            }
        }
    }

    private Room fetchRoomResource(String roomSid) {
        return Room.fetcher(roomSid).fetch(twilioRestClient);
    }
}
