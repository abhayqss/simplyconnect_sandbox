package com.scnsoft.eldermark.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opentok.exception.OpenTokException;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.PhrOpenTokSessionDetailDao;
import com.scnsoft.eldermark.dao.phr.PhrVideoCallParticipantsDao;
import com.scnsoft.eldermark.dao.phr.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.phr.ApnsModel;
import com.scnsoft.eldermark.entity.phr.OpentokEntity;
import com.scnsoft.eldermark.entity.phr.PhrOpenTokSessionDetail;
import com.scnsoft.eldermark.entity.phr.PhrVideoCallParticipants;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.phr.chat.dto.VideoCallLogResponseDto;
import com.scnsoft.eldermark.services.apns.ApnsNotificationService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.opentok.OpentokService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.shared.carecoordination.service.InvitationDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import com.scnsoft.eldermark.web.entity.TokBoxTokenDto;
import com.scnsoft.eldermark.web.entity.UserDetailDto;
import com.scnsoft.eldermark.web.entity.UserPersonalDetailsDto;
import com.scnsoft.eldermark.web.entity.UsersNotificationIdDto;
import com.scnsoft.eldermark.web.entity.VideoCallEventDto;
import com.scnsoft.eldermark.web.entity.VideoCallLogDto;
import com.scnsoft.eldermark.web.entity.VideoCallResponseDto;
import com.scnsoft.eldermark.web.entity.VideoCallResponseDto.Call;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;

/**
 * @author phomal Created on 6/2/2017
 */
@Service
@Transactional
public class VideoCallService extends BasePhrService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHealthProviderService.class);

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PushNotificationRegistrationDao pushNotificationRegistrationDao;

    @Autowired
    private PhrOpenTokSessionDetailDao phrOpenTokSessionDetailDao;

    @Autowired
    private PhrVideoCallParticipantsDao phrVideoCallParticipantsDao;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private OpentokService opentokService;

    @Autowired
    private ApnsNotificationService apnsNotificationService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Value("${inactive.user.subject}")
    private String subject;

    @Value("${inactive.user.message}")
    private String message;

    @Value("${call.log.day}")
    private int NoOfDaysCallLog;

    public TokBoxTokenDto createVideoConference(Long userId, Long userId2) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        // TODO implement
        return null;
    }

    public VideoCallResponseDto createMultiVideoConference(Long callerId, UsersNotificationIdDto calleeIds)
            throws OpenTokException, JsonProcessingException, InterruptedException, ExecutionException {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(callerId);
        Date openTokDateTime = DateTime.now(DateTimeZone.UTC).toDate();
        User user = userDao.getOne(callerId);
        // build opentok session
        OpentokEntity opentokBuilderDto = opentokService.createSession();
        // save opentok session details in database
        PhrOpenTokSessionDetail phrVideoSession = new PhrOpenTokSessionDetail();
        phrVideoSession.setIsSessionActive(Boolean.TRUE);
        phrVideoSession.setOpentokSession(opentokBuilderDto.getSessionId());
        phrVideoSession.setUser(user);
        phrVideoSession.setSessionCreatedAt(openTokDateTime);
        phrVideoSession.setOpentokToken(opentokBuilderDto.getToken());
        phrOpenTokSessionDetailDao.save(phrVideoSession);
        List<UserPersonalDetailsDto> userPersonalCalleeList = new ArrayList<>();
        List<PhrVideoCallParticipants> callParticipants = new ArrayList<PhrVideoCallParticipants>();
        VideoCallResponseDto videoCallResponseDto = new VideoCallResponseDto();

        // Caller detail
        if (user.getId() != null) {
            PhrVideoCallParticipants phrVideoCallUserStatus = new PhrVideoCallParticipants();
            phrVideoCallUserStatus.setPhrOpenTokSessionDetail(phrVideoSession);
            phrVideoCallUserStatus.setUser(user);
            phrVideoCallUserStatus.setIsUserActive(true);
            phrVideoCallUserStatus.setCallType(Call.INCOMING.toString());
            callParticipants.add(phrVideoCallUserStatus);
            UserPersonalDetailsDto userPersonalDetailsDto = new UserPersonalDetailsDto();
            userPersonalDetailsDto.setUserFullName(user.getFullName());
            userPersonalDetailsDto.setUserId(user.getId());
            userPersonalDetailsDto.setUserPhotoUrl(StringUtils.EMPTY);
            userPersonalDetailsDto.setIsUserAvailable(Boolean.TRUE);
            userPersonalCalleeList.add(userPersonalDetailsDto);
            videoCallResponseDto.setCallerDetails(userPersonalDetailsDto);
        }
        // Callee detail
        for (UserDetailDto callee : calleeIds.getCalleeList()) {
            if (callee.getUserId() != null) {
                user = userDao.findOne(callee.getUserId());
                PhrVideoCallParticipants phrVideoCallUserStatus = new PhrVideoCallParticipants();
                phrVideoCallUserStatus.setPhrOpenTokSessionDetail(phrVideoSession);
                phrVideoCallUserStatus.setUser(user);
                phrVideoCallUserStatus.setIsUserActive(true);
                phrVideoCallUserStatus.setCallType(Call.OUTGOING.toString());
                callParticipants.add(phrVideoCallUserStatus);
                UserPersonalDetailsDto userPersonalDetailsDto = new UserPersonalDetailsDto();
                userPersonalDetailsDto.setUserFullName(user.getFullName());
                userPersonalDetailsDto.setUserId(user.getId());
                userPersonalDetailsDto.setUserPhotoUrl(StringUtils.EMPTY);
                userPersonalDetailsDto.setIsUserAvailable(Boolean.TRUE);
                userPersonalCalleeList.add(userPersonalDetailsDto);
            } else {
                // Inactive user
                ResidentCareTeamMember residentCareTeamMember = residentCareTeamMemberDao.get(callee.getId());
                Employee employee = residentCareTeamMember.getEmployee();
                for (PersonTelecom person : employee.getPerson().getTelecoms()) {
                    if ("EMAIL".equalsIgnoreCase(person.getUseCode()) && !person.getValue().isEmpty()) {
                        InvitationDto invitationDto = new InvitationDto();
                        invitationDto.setCreator(userDao.getOne(callerId).getFullName());
                        invitationDto.setToEmail(person.getValue());
                        invitationDto.setCareReceiver(employee.getFullName());
                        exchangeMailService.sendVideoCallInviationNotification(invitationDto);
                        break;
                    }
                }
            }
        }

        videoCallResponseDto.setOpentokData(opentokBuilderDto);
        videoCallResponseDto.setCallStartTime(openTokDateTime.toString());
        videoCallResponseDto.setSessionParticipants(userPersonalCalleeList);
        List<Long> userForNotify = new ArrayList<>();
        for (UserDetailDto userData : calleeIds.getCalleeList()) {
            if (userData.getUserId() != null)
                userForNotify.add(userData.getUserId());
        }
        sendNotification(userForNotify, videoCallResponseDto);
        phrVideoCallParticipantsDao.save(callParticipants);
        videoCallResponseDto.setCallType(Call.OUTGOING);
        return videoCallResponseDto;
    }

    public boolean videoCallEvent(Long userId, VideoCallEventDto videoCallEventDto) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        if (videoCallEventDto.getOpentokSessionId() == null)
            throw new PhrException("Session id not present");
        List<Long> activeUsersIds = phrVideoCallParticipantsDao
                .getAllActiveUserIdsFromSession(videoCallEventDto.getOpentokSessionId());
        if (CollectionUtils.isEmpty(activeUsersIds))
            throw new PhrException("User not present in call");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("eventName", videoCallEventDto.getEventName().toString());
        body.put("userId", userId);
        body.put("openTokSessionId", videoCallEventDto.getOpentokSessionId());
        body.put("show_in_foreground", Boolean.TRUE);

        switch (videoCallEventDto.getEventName()) {
            case CALL_ACCEPT: {
                Date date = DateTime.now(DateTimeZone.UTC).toDate();
                PhrOpenTokSessionDetail phrOpenTokSessionDetail = phrOpenTokSessionDetailDao
                        .getFromSession(videoCallEventDto.getOpentokSessionId());
                List<Long> performerIds = new ArrayList<Long>();
                performerIds.add(phrOpenTokSessionDetail.getUser().getId());
                performerIds.add(userId);
                for (Long performerId : performerIds) {
                    PhrVideoCallParticipants eventPerformer = phrVideoCallParticipantsDao
                            .getActiveUserFromActiveSession(performerId, videoCallEventDto.getOpentokSessionId());
                    if (eventPerformer.getCallStartTime() == null) {
                        eventPerformer.setCallStartTime(date);
                        phrVideoCallParticipantsDao.save(eventPerformer);
                    }
                }
    
                body.put("isSessionContinue", true);
                body.put("isAudioActive", videoCallEventDto.getIsAudioActive());
                logger.info("Call accept event perform by: {}", body);
                fcmService(body, activeUsersIds, true);
                return Boolean.TRUE;
            }
            case CALL_DISCONNECT:
            case CALL_DECLINE:
            case CALL_NO_ANSWER: {
                Long callEndAt = DateTime.now(DateTimeZone.UTC).toDate().getTime();
                PhrVideoCallParticipants eventPerformer = phrVideoCallParticipantsDao.getActiveUserFromActiveSession(userId,
                        videoCallEventDto.getOpentokSessionId());
                if (eventPerformer == null)
                    throw new PhrException("User not active on call");
                if (activeUsersIds.size() > 2) {
                    eventPerformer.setIsUserActive(Boolean.FALSE);
                    if (eventPerformer.getCallStartTime() != null) {
                        eventPerformer.setCallDuration((callEndAt - eventPerformer.getCallStartTime().getTime()) / 1000);
                    }
                    eventPerformer.setEvent(videoCallEventDto.getEventName().toString());
                    phrVideoCallParticipantsDao.save(eventPerformer);
                    body.put("isSessionContinue", Boolean.TRUE);
                } else {
                    List<PhrVideoCallParticipants> activeUsersOnCall = phrVideoCallParticipantsDao
                            .getAllUserFromSession(videoCallEventDto.getOpentokSessionId());
                    List<PhrVideoCallParticipants> particpantsList = new ArrayList<PhrVideoCallParticipants>();
                    for (PhrVideoCallParticipants activeUserOnCall : activeUsersOnCall) {
                        activeUserOnCall.setIsUserActive(Boolean.FALSE);
                        if (activeUserOnCall.getCallStartTime() != null) {
                            activeUserOnCall
                                    .setCallDuration((callEndAt - activeUserOnCall.getCallStartTime().getTime()) / 1000);
                        }
                        activeUserOnCall.setEvent(videoCallEventDto.getEventName().toString());
                        particpantsList.add(activeUserOnCall);
    
                    }
                    if (CollectionUtils.isNotEmpty(particpantsList)) {
                        particpantsList.get(0).getPhrOpenTokSessionDetail().setIsSessionActive(Boolean.FALSE);
                        phrVideoCallParticipantsDao.save(particpantsList);
                    }
                    body.put("isSessionContinue", Boolean.FALSE);
                }
                logger.info("{0} event perform by {1}", videoCallEventDto.getEventName(), body);
                body.put("isAudioActive", videoCallEventDto.getIsAudioActive());
                // apns voip send only disconnect case
                if ("CALL_DISCONNECT".equalsIgnoreCase(videoCallEventDto.getEventName().toString())) {
                    apnsService(body, activeUsersIds);
                }
                fcmService(body, activeUsersIds, true);
                return Boolean.TRUE;
            }
            case CALL_ON_MUTE: {
                body.put("isAudioActive", videoCallEventDto.getIsAudioActive());
                logger.info("{0} event perform by {1}", videoCallEventDto.getEventName(), body);
                fcmService(body, activeUsersIds, true);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public VideoCallLogResponseDto callLog(Long userId, Long receiverId, int page, int size) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        size = size == 0 ? 20 : size;
        page = page == 0 ? page : page - 1;
        Page<PhrVideoCallParticipants> phrVideoCallParticipants = phrVideoCallParticipantsDao.getCallLogs(userId,
                receiverId, DateTime.now(DateTimeZone.UTC).plusDays(-NoOfDaysCallLog).toDate(),
                new PageRequest(page, size));
        VideoCallLogResponseDto videoCallLogResponseDto = new VideoCallLogResponseDto();
        videoCallLogResponseDto.setTotalCount(phrVideoCallParticipants.getTotalElements());
        videoCallLogResponseDto.setVideoCallLogDto(callLogTransform(phrVideoCallParticipants.getContent()));
        return videoCallLogResponseDto;
    }

    private List<VideoCallLogDto> callLogTransform(List<PhrVideoCallParticipants> phrVideoCallParticipants) {
        List<VideoCallLogDto> videoCallLogList = new ArrayList<VideoCallLogDto>();
        for (PhrVideoCallParticipants phrVideoCallParticipant : phrVideoCallParticipants) {
            VideoCallLogDto videoCallLogDto = new VideoCallLogDto();
            videoCallLogDto.setId(phrVideoCallParticipant.getId());
            videoCallLogDto.setCallDuration(phrVideoCallParticipant.getCallDuration());
            videoCallLogDto.setCallEvent(phrVideoCallParticipant.getEvent());
            videoCallLogDto.setCallType(phrVideoCallParticipant.getCallType());
            videoCallLogDto.setDateTime(phrVideoCallParticipant.getPhrOpenTokSessionDetail().getSessionCreatedAt());
            videoCallLogDto.setUserId(phrVideoCallParticipant.getUser().getId());
            videoCallLogDto.setCreatedBy(phrVideoCallParticipant.getPhrOpenTokSessionDetail().getUser().getId());
            videoCallLogList.add(videoCallLogDto);
        }
        return videoCallLogList;
    }

    private void sendNotification(List<Long> userIds, VideoCallResponseDto videoCallResponseDto)
            throws JsonProcessingException {
        if (CollectionUtils.isNotEmpty(userIds)) {
            videoCallResponseDto.setCallType(Call.INCOMING);
            HashMap<String, Object> pushData = new Gson().fromJson(mapper.writeValueAsString(videoCallResponseDto),
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
            apnsService(pushData, userIds);
            fcmService(pushData, userIds, false);
        }
    }

    // apns voip service call send notification to only ios device
    @Async
    private void apnsService(Map<String, Object> pushData, List<Long> userIds) {
        Collection<String> voipTokensList = pushNotificationRegistrationDao
                .getTokenMultiUserIdAndServiceProviderAndAppName(userIds, PushNotificationRegistration.ServiceProvider.APNS_PK,
                        PushNotificationRegistration.PHR_APP);
        if (CollectionUtils.isNotEmpty(voipTokensList)) {
            Map<String, Object> apnsPayload = new HashMap<String, Object>();
            apnsPayload.put("content-available", 1);
            ApnsModel apnsModelDto = new ApnsModel();
            apnsModelDto.setApnsKey("apns");
            apnsModelDto.setAps(apnsPayload);
            apnsModelDto.setData(pushData);
            apnsModelDto.setToken(voipTokensList);
            apnsNotificationService.voipPush(apnsModelDto);
        }
    }

    // fcm service call send notification to cell phone devices
    @Async
    private void fcmService(Map<String, Object> pushData, List<Long> userIds, boolean isNotification) {
        List<String> fcmtokensList = pushNotificationRegistrationDao.getTokenMultiUserIdAndServiceProviderAndAppName(userIds,
                PushNotificationRegistration.ServiceProvider.FCM, PushNotificationRegistration.PHR_APP);
        if (CollectionUtils.isNotEmpty(fcmtokensList)) {
            PushNotificationVO pushNotificationVO = new PushNotificationVO();
            pushNotificationVO.setTokens(fcmtokensList);
            pushNotificationVO.setServiceProvider(PushNotificationRegistration.ServiceProvider.FCM);
            pushNotificationVO.setPayload(pushData);
            pushNotificationVO.setAttributeKey(StringUtils.EMPTY);
            if (isNotification) {
                Map<String, Object> customNotification = new HashMap<String, Object>();
                customNotification.put("content_available", true);
                pushNotificationVO.setNotification(customNotification);
            }
            pushNotificationService.send(pushNotificationVO);
        }
    }
}
