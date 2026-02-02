package com.scnsoft.eldermark.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.dao.phr.UserMobileNotificationPreferencesDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.UserMobileNotificationPreferences;
import com.scnsoft.eldermark.service.validation.CareTeamValidator;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.web.entity.NotificationSettingsDto;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author phomal
 * Created on 6/7/2017
 */
@Service
public class NotificationPreferencesService {

    @Autowired
    UserMobileNotificationPreferencesDao userMobileNotificationPreferencesDao;

    @Autowired
    private EventTypeCareTeamRoleXrefDao eventTypeCareTeamRoleXrefDao;

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    private CareReceiverService careReceiverService;

    @Autowired
    CareTeamMemberNotificationPreferencesDao careTeamMemberNotificationPreferencesDao;

    // === Mobile User Notification Preferences

    private static final ImmutableMap<NotificationType, Boolean> phrNotificationChannels = Maps.immutableEnumMap(ImmutableMap.of(
            NotificationType.PUSH_NOTIFICATION, Boolean.TRUE,
            NotificationType.SECURITY_MESSAGE, Boolean.FALSE,
            NotificationType.EMAIL, Boolean.TRUE,
            NotificationType.SMS, Boolean.FALSE));

    @Transactional
    public NotificationSettingsDto setNotificationSettings(Long userId, NotificationSettingsDto notificationSettingsDto) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        Map<NotificationType, Boolean> notificationChannels = notificationSettingsDto.getNotificationChannels();
        List<EventTypeDto> eventTypes = notificationSettingsDto.getEventTypes();

        List<UserMobileNotificationPreferences> notificationPreferences = transform(userId, eventTypes, notificationChannels);
        userMobileNotificationPreferencesDao.deleteByUserId(userId);
        userMobileNotificationPreferencesDao.save(notificationPreferences);

        return transform(notificationPreferences);
    }

    @Transactional
    List<UserMobileNotificationPreferences> setDefaultNotificationSettings(Long userId, boolean force) {
        if (force || CollectionUtils.isEmpty(userMobileNotificationPreferencesDao.getByUserId(userId))) {
            List<EventTypeDto> allEventTypes = eventTypeService.getEventTypes();
            List<UserMobileNotificationPreferences> notificationPreferences = transform(userId, allEventTypes, phrNotificationChannels);
            if (force) {
                userMobileNotificationPreferencesDao.deleteByUserId(userId);
            }
            userMobileNotificationPreferencesDao.save(notificationPreferences);

            return notificationPreferences;
        }

        return null;
    }

    public NotificationSettingsDto getNotificationSettings(Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        List<UserMobileNotificationPreferences> notificationPreferences = userMobileNotificationPreferencesDao.getByUserId(userId);

        return transform(notificationPreferences);
    }

    public static Map<NotificationType, String> getPhrNotificationChannels() {
        Map<NotificationType, String> map = new HashMap<>();
        for (NotificationType notificationType : phrNotificationChannels.keySet()) {
            map.put(notificationType, notificationType.getDescription());
        }
        return map;
    }

    // === Care Team Member Notification Preferences

    void createDefaultCareTeamMemberNotificationPreferences(final CareTeamMember careTeamMember) {
        careTeamMember.setCareTeamMemberNotificationPreferencesList(new ArrayList<CareTeamMemberNotificationPreferences>());

        final List<EventTypeCareTeamRoleXref> defaultSettings = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamMember.getCareTeamRole().getId());

        for (EventTypeCareTeamRoleXref xref : defaultSettings) {
            final CareTeamMemberNotificationPreferences preferences = new CareTeamMemberNotificationPreferences();
            final CareTeamMemberNotificationPreferences preferences2 = new CareTeamMemberNotificationPreferences();

            preferences.setResponsibility(xref.getResponsibility());
            preferences.setEventType(xref.getEventType());
            preferences.setNotificationType(NotificationType.EMAIL);
            preferences.setCareTeamMember(careTeamMember);
            preferences2.setResponsibility(xref.getResponsibility());
            preferences2.setEventType(xref.getEventType());
            preferences2.setNotificationType(NotificationType.PUSH_NOTIFICATION);
            preferences2.setCareTeamMember(careTeamMember);

            careTeamMember.getCareTeamMemberNotificationPreferencesList().add(preferences);
            careTeamMember.getCareTeamMemberNotificationPreferencesList().add(preferences2);
        }
    }

    /**
     * @param userId User ID of the current user-provider
     * @param receiverId Care team member ID of a care receiver
     * @return Care Team Member's event notification preferences
     */
    @Transactional(readOnly = true)
    public NotificationSettingsDto getNotificationSettings(Long userId, Long receiverId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        final ResidentCareTeamMember careReceiver = careReceiverService.getCareReceiverOrThrow(userId, receiverId);

        return transformCtmPreferences(careReceiver);
    }

    /**
     * @param userId User ID of the current user-provider
     * @param receiverId Care team member ID of a care receiver
     * @param body New notification preferences
     */
    @Transactional
    public NotificationSettingsDto setNotificationSettings(Long userId, Long receiverId, NotificationSettingsDto body) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        final ResidentCareTeamMember careReceiver = careReceiverService.getCareReceiverOrThrow(userId, receiverId);
        CareTeamValidator.validateNotificationChannelsAvailabilityOrThrow(careReceiver, body);

        final List<CareTeamMemberNotificationPreferences> notificationPreferences = transformCtmPreferences(careReceiver, body);
        deleteByCtmId(receiverId);
        careTeamMemberNotificationPreferencesDao.save(notificationPreferences);
        careReceiver.setCareTeamMemberNotificationPreferencesList(notificationPreferences);
        careTeamMemberNotificationPreferencesDao.flush();

        return transformCtmPreferences(careReceiver);
    }

    void deleteByCtmId(Long ctmId) {
        careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(ctmId);
    }

    // === Utility

    private List<UserMobileNotificationPreferences> transform(Long userId, List<EventTypeDto> eventTypes, Map<NotificationType, Boolean> notificationChannels) {
        List<UserMobileNotificationPreferences> notificationPreferences = new ArrayList<>();

        for (NotificationType notificationType : notificationChannels.keySet()) {
            if (!Boolean.TRUE.equals(notificationChannels.get(notificationType))) {
                continue;
            }
            for (EventTypeDto eventType : eventTypes) {
                if (Boolean.FALSE.equals(eventType.getEnabled())) {
                    continue;
                }

                UserMobileNotificationPreferences userMobileNotificationPreferences = new UserMobileNotificationPreferences();
                userMobileNotificationPreferences.setUserId(userId);
                userMobileNotificationPreferences.setEventType(eventTypeService.getById(eventType.getId()));
                userMobileNotificationPreferences.setNotificationType(notificationType);
                userMobileNotificationPreferences.setResponsibility(Responsibility.I);

                notificationPreferences.add(userMobileNotificationPreferences);
            }
        }

        return notificationPreferences;
    }

    private NotificationSettingsDto transform(List<UserMobileNotificationPreferences> notificationPreferences) {
        final Set<String> enabledEventTypes = new HashSet<>();
        final NotificationSettingsDto dto = initNotificationSettingsDto();

        for (UserMobileNotificationPreferences notificationPreference : notificationPreferences) {
            EventType eventType = notificationPreference.getEventType();
            enabledEventTypes.add(eventType.getCode());

            NotificationType notificationType = notificationPreference.getNotificationType();
            dto.getNotificationChannels().put(notificationType, Boolean.TRUE);
        }

        final List<EventTypeDto> eventTypeDtos = eventTypeService.getSortedEventTypes(enabledEventTypes);
        dto.setEventTypes(eventTypeDtos);

        return dto;
    }

    private NotificationSettingsDto transformCtmPreferences(final CareTeamMember ctm) {
        final Set<String> enabledEventTypes = new HashSet<>();
        final NotificationSettingsDto dto = initNotificationSettingsDto();

        for (CareTeamMemberNotificationPreferences ctmNotificationPreferences : ctm.getCareTeamMemberNotificationPreferencesList()) {
            final EventType eventType = ctmNotificationPreferences.getEventType();
            if (!Responsibility.N.equals(ctmNotificationPreferences.getResponsibility())) {
                enabledEventTypes.add(eventType.getCode());
            }

            final NotificationType notificationType = ctmNotificationPreferences.getNotificationType();
            if (phrNotificationChannels.containsKey(notificationType)) {
                dto.getNotificationChannels().put(notificationType, Boolean.TRUE);
            }
        }

        final List<EventTypeCareTeamRoleXref> defaultSettings = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(ctm.getCareTeamRole().getId());
        final List<EventTypeDto> eventTypeDtos = eventTypeService.getSortedEventTypes(enabledEventTypes, defaultSettings);
        dto.setEventTypes(eventTypeDtos);

        return dto;
    }

    private List<CareTeamMemberNotificationPreferences> transformCtmPreferences(final CareTeamMember ctm, final NotificationSettingsDto dto) {
        final List<EventTypeCareTeamRoleXref> defaultSettings = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(ctm.getCareTeamRole().getId());
        List<CareTeamMemberNotificationPreferences> notificationPreferences = new ArrayList<>();
        final Map<NotificationType, Boolean> notificationChannels = dto.getNotificationChannels();

        for (NotificationType notificationType : notificationChannels.keySet()) {
            if (!Boolean.TRUE.equals(notificationChannels.get(notificationType)) || !phrNotificationChannels.containsKey(notificationType)) {
                continue;
            }
            for (EventTypeDto eventType : dto.getEventTypes()) {
                CareTeamMemberNotificationPreferences notificationPreference = new CareTeamMemberNotificationPreferences();
                notificationPreference.setCareTeamMember(ctm);
                notificationPreference.setEventType(eventTypeService.getById(eventType.getId()));
                notificationPreference.setNotificationType(notificationType);
                if (Boolean.TRUE.equals(eventType.getEnabled())) {
                    notificationPreference.setResponsibility(getDefaultResponsibility(eventType.getId(), defaultSettings));
                } else {
                    notificationPreference.setResponsibility(Responsibility.N);
                }

                notificationPreferences.add(notificationPreference);
            }
        }

        return notificationPreferences;
    }

    private Responsibility getDefaultResponsibility(Long eventTypeId, List<EventTypeCareTeamRoleXref> defaultSettings) {
        for (EventTypeCareTeamRoleXref xref : defaultSettings) {
            if (xref.getEventType().getId().equals(eventTypeId)) {
                return xref.getResponsibility();
            }
        }
        return Responsibility.I;
    }

    private static NotificationSettingsDto initNotificationSettingsDto() {
        final NotificationSettingsDto dto = new NotificationSettingsDto();
        final Map<NotificationType, Boolean> notificationChannelsDto = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        for (NotificationType notificationType : phrNotificationChannels.keySet()) {
            notificationChannelsDto.put(notificationType, Boolean.FALSE);
        }
        dto.setNotificationChannels(notificationChannelsDto);

        return dto;
    }

}
