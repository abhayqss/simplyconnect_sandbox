package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.conversation.AccessibleChatCommunityCareTeamFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.event.incident.*;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.EventNotificationService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.VideoCallService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class IncidentReportDtoConverter implements Converter<IncidentReport, IncidentReportDto> {
    private static final Logger logger = LoggerFactory.getLogger(IncidentReportDtoConverter.class);

    @Autowired
    private BiFunction<IncidentReport, PermissionFilter, IncidentClientDto> incidentClientDtoConverter;

    @Autowired
    private ListAndItemConverter<IncidentReportIncidentPlaceTypeFreeText, TextDto> incidentPlaceTypeFreeTextDtoListConverter;

    @Autowired
    private ListAndItemConverter<Individual, IncidentIndividualDto> incidentIndividualDtoListConverter;

    @Autowired
    private ListAndItemConverter<IncidentWeatherConditionTypeFreeText, TextDto> incidentWeatherConditionTypeFreeTextDtoListConverter;

    @Autowired
    private ListAndItemConverter<IncidentWitness, IncidentWitnessDto> incidentWitnessesDtoListConverter;

    @Autowired
    private ListAndItemConverter<IncidentPicture, IncidentPictureDto> incidentPictureDtoListConverter;

    @Autowired
    private Converter<IncidentVitalSigns, IncidentVitalSignsDto> incidentVitalSignsDtoConverter;

    @Autowired
    private Converter<List<IncidentReportNotification>, IncidentNotificationsDto> incidentNotificationDtoConverter;

    @Autowired
    private ListAndItemConverter<IncidentInjury, CoordinatesDto> incidentInjuryDtoConverter;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public IncidentReportDto convert(IncidentReport source) {
        if (source == null) {
            return null;
        }
        var target = new IncidentReportDto();
        target.setId(source.getId());
        target.setCompletedBy(source.getReportAuthor());
        target.setCompletedByPosition(source.getReportAuthorTitle());
        target.setCompletedByPhone(source.getReportAuthorPhone());
        target.setCompletedDate(DateTimeUtils.toEpochMilli(source.getReportCompletedDate()));
        target.setReportDate(DateTimeUtils.toEpochMilli(source.getReportDate()));
        target.setReportedBy(source.getReportedBy());
        target.setReportedByPosition(source.getReportedByTitle());
        target.setReportedByPhone(source.getReportedByPhone());
        var curStatus = Optional.ofNullable(source.getStatus())
                .orElseGet(() -> source.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT);
        target.setStatusName(curStatus.name());
        target.setStatusTitle(curStatus.getDisplayName());
        target.setAssignedTo(source.getEmployee().getFullName());

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        target.setClient(incidentClientDtoConverter.apply(source, permissionFilter));
        target.setIncidentDate(DateTimeUtils.toEpochMilli(source.getIncidentDatetime()));
        target.setIncidentDiscoveredDate(DateTimeUtils.formatLocalDate(source.getIncidentDiscoveredDate()));
        target.setWasProviderPresentOrScheduled(source.getWasProviderPresentOrScheduled());
        target.setPlaces(filterList(incidentPlaceTypeFreeTextDtoListConverter.convertList(source.getIncidentPlaceTypes())));
        target.setWeatherConditions(filterList(incidentWeatherConditionTypeFreeTextDtoListConverter.convertList(source.getIncidentWeatherConditionTypes())));
        target.setInvolvedIndividuals(filterList(incidentIndividualDtoListConverter.convertList(source.getIndividuals())));
        target.setWereOtherIndividualsInvolved(source.getWereOtherIndividualsInvolved());
        target.setIncidentDetails(source.getNarrative());
        target.setWasIncidentParticipantTakenToHospital(source.getWasIncidentParticipantTakenToHospital());
        target.setIncidentParticipantHospitalName(source.getIncidentParticipantHospitalName());
        target.setWitnesses(filterList(incidentWitnessesDtoListConverter.convertList(source.getWitnesses())));
        target.setIncidentPictures(filterList(incidentPictureDtoListConverter.convertList(source.getPictures())));
        target.setWereApparentInjuries(source.getWereApparentInjuries());
        target.setInjuries(filterList(incidentInjuryDtoConverter.convertList(source.getIncidentInjuries())));
        target.setCurrentInjuredClientCondition(source.getInjuredClientCondition());
        target.setVitalSigns(incidentVitalSignsDtoConverter.convert(source.getVitalSigns()));
        target.setNotification(incidentNotificationDtoConverter.convert(source.getNotifications()));
        target.setImmediateIntervention(source.getImmediateIntervention());
        target.setFollowUpInformation(source.getFollowUpInformation());
        target.setEventId(source.getEventId());
        target.setCanDelete(Optional.ofNullable(source.getStatus())
                .orElseGet(() -> source.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT) == IncidentReportStatus.DRAFT);
        target.setArchived(source.getArchived());
        target.setEventNotificationCount(eventNotificationService.count(source.getEventId()));
        target.setConversationSid(source.getTwilioConversationSid());

        if (source.getEvent().getClient().getHieConsentPolicyType() != HieConsentPolicyType.OPT_OUT &&
                Boolean.TRUE.equals(source.getEvent().getClient().getActive())) {
            var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
            if (source.getTwilioConversationSid() != null) {
                target.setIsConversationParticipant(chatService.isGroupChatParticipant(source.getTwilioConversationSid(), currentEmployeeId));
            }

            var filter = new AccessibleChatCommunityCareTeamFilter();
            filter.setCommunityIds(Collections.singleton(source.getEvent().getClient().getCommunityId()));
            filter.setExcludedEmployeeId(currentEmployeeId);

            logger.info("target.setHasCommunityCareTeamMembersWithEnabledVideoConversations start");
            var start = System.currentTimeMillis();
            target.setHasCommunityCareTeamMembersWithEnabledVideoConversations(
                    videoCallService.isVideoCallEnabled(currentEmployeeId) &&
                            communityCareTeamMemberService.hasVideoCallAccessibleCommunityCareTeamMember(permissionFilter, filter)
            );
            logger.info("target.setHasCommunityCareTeamMembersWithEnabledVideoConversations end in {}ms", System.currentTimeMillis() - start);

            if (target.getHasCommunityCareTeamMembersWithEnabledVideoConversations()) {
                //in case can call, then also can chat
                target.setHasCommunityCareTeamMembersWithEnabledConversations(true);
            } else {
                logger.info("target.setHasCommunityCareTeamMembersWithEnabledConversations start");
                start = System.currentTimeMillis();
                target.setHasCommunityCareTeamMembersWithEnabledConversations(
                        chatService.isChatEnabled(currentEmployeeId) &&
                                communityCareTeamMemberService.hasChatAccessibleCommunityCareTeamMember(permissionFilter, filter)
                );
                logger.info("target.setHasCommunityCareTeamMembersWithEnabledConversations end in {}ms", System.currentTimeMillis() - start);
            }
        }

        return target;
    }

    private <T> List<T> filterList(List<T> list) {
        var filteredList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredList)) {
            return null;
        }
        return filteredList;
    }
}
