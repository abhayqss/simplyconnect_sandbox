package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.validation.SpELAssert;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        groups = ValidationGroups.Update.class,
                        applyIf = "#isTrue(wereApparentInjuries)",
                        value = "#isNotEmpty(injuries)",
                        message = "injuries {javax.validation.constraints.NotEmpty.message}",
                        helpers = {BooleanUtils.class, CollectionUtils.class}
                )
        }
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentReportDto {

    private Long id;

    @NotEmpty
    @Size(max = 256)
    private String completedBy;

    @NotEmpty
    @Size(max = 256)
    private String completedByPosition;

    @NotEmpty(groups = ValidationGroups.Update.class)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String completedByPhone;

    @NotNull
    private Long completedDate;

    @NotEmpty(groups = ValidationGroups.Update.class)
    private String reportedBy;

    @NotEmpty(groups = ValidationGroups.Update.class)
    private String reportedByPosition;

    @NotEmpty(groups = ValidationGroups.Update.class)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String reportedByPhone;

    @NotNull
    private Long reportDate;

    @NotEmpty
    private String statusName;
    private String statusTitle;

    private String assignedTo;

    @NotNull
    @Valid
    private IncidentClientDto client;

    @NotNull(groups = ValidationGroups.Update.class)
    private Long incidentDate;

    @NotEmpty(groups = ValidationGroups.Update.class)
    private String incidentDiscoveredDate;

    @NotNull(groups = ValidationGroups.Update.class)
    private Boolean wasProviderPresentOrScheduled;

    @NotEmpty(groups = ValidationGroups.Update.class)
    private List<TextDto> places;

    private List<TextDto> weatherConditions;

    @Size(max = 20_000)
    private String incidentDetails;

    private Boolean wasIncidentParticipantTakenToHospital;
    @Size(max = 256)
    private String incidentParticipantHospitalName;

    private Boolean wereApparentInjuries;
    private List<CoordinatesDto> injuries;
    @Size(max = 5_000)
    private String currentInjuredClientCondition;

    @Valid
    private IncidentVitalSignsDto vitalSigns;

    private List<@Valid IncidentWitnessDto> witnesses;

    private Boolean wereOtherIndividualsInvolved;
    private List<@Valid IncidentIndividualDto> involvedIndividuals;

    @Size(max = 10)
    private List<MultipartFile> incidentPictureFiles;
    private List<IncidentPictureDto> incidentPictures;

    @Valid
    private IncidentNotificationsDto notification;

    @NotEmpty(groups = ValidationGroups.Update.class)
    @Size(max = 20_000)
    private String immediateIntervention;

    @NotEmpty(groups = ValidationGroups.Update.class)
    @Size(max = 20_000)
    private String followUpInformation;

    @NotNull
    private Long eventId;

    private boolean canDelete;

    @JsonProperty("isArchived")
    private boolean archived;

    private boolean hasCommunityCareTeamMembersWithEnabledConversations;
    private boolean hasCommunityCareTeamMembersWithEnabledVideoConversations;

    private long eventNotificationCount;

    private String conversationSid;

    private Boolean isConversationParticipant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getCompletedByPosition() {
        return completedByPosition;
    }

    public void setCompletedByPosition(String completedByPosition) {
        this.completedByPosition = completedByPosition;
    }

    public String getCompletedByPhone() {
        return completedByPhone;
    }

    public void setCompletedByPhone(String completedByPhone) {
        this.completedByPhone = completedByPhone;
    }

    public Long getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Long completedDate) {
        this.completedDate = completedDate;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportedByPosition() {
        return reportedByPosition;
    }

    public void setReportedByPosition(String reportedByPosition) {
        this.reportedByPosition = reportedByPosition;
    }

    public String getReportedByPhone() {
        return reportedByPhone;
    }

    public void setReportedByPhone(String reportedByPhone) {
        this.reportedByPhone = reportedByPhone;
    }

    public Long getReportDate() {
        return reportDate;
    }

    public void setReportDate(Long reportDate) {
        this.reportDate = reportDate;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public IncidentClientDto getClient() {
        return client;
    }

    public void setClient(IncidentClientDto client) {
        this.client = client;
    }

    public Long getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Long incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentDiscoveredDate() {
        return incidentDiscoveredDate;
    }

    public void setIncidentDiscoveredDate(String incidentDiscoveredDate) {
        this.incidentDiscoveredDate = incidentDiscoveredDate;
    }

    public Boolean getWasProviderPresentOrScheduled() {
        return wasProviderPresentOrScheduled;
    }

    public void setWasProviderPresentOrScheduled(Boolean wasProviderPresentOrScheduled) {
        this.wasProviderPresentOrScheduled = wasProviderPresentOrScheduled;
    }

    public List<TextDto> getPlaces() {
        return places;
    }

    public void setPlaces(List<TextDto> places) {
        this.places = places;
    }

    public List<TextDto> getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(List<TextDto> weatherConditions) {
        this.weatherConditions = weatherConditions;
    }

    public String getIncidentDetails() {
        return incidentDetails;
    }

    public void setIncidentDetails(String incidentDetails) {
        this.incidentDetails = incidentDetails;
    }

    public Boolean getWasIncidentParticipantTakenToHospital() {
        return wasIncidentParticipantTakenToHospital;
    }

    public void setWasIncidentParticipantTakenToHospital(Boolean wasIncidentParticipantTakenToHospital) {
        this.wasIncidentParticipantTakenToHospital = wasIncidentParticipantTakenToHospital;
    }

    public String getIncidentParticipantHospitalName() {
        return incidentParticipantHospitalName;
    }

    public void setIncidentParticipantHospitalName(String incidentParticipantHospitalName) {
        this.incidentParticipantHospitalName = incidentParticipantHospitalName;
    }

    public Boolean getWereApparentInjuries() {
        return wereApparentInjuries;
    }

    public void setWereApparentInjuries(Boolean wereApparentInjuries) {
        this.wereApparentInjuries = wereApparentInjuries;
    }

    public List<CoordinatesDto> getInjuries() {
        return injuries;
    }

    public void setInjuries(List<CoordinatesDto> injuries) {
        this.injuries = injuries;
    }

    public String getCurrentInjuredClientCondition() {
        return currentInjuredClientCondition;
    }

    public void setCurrentInjuredClientCondition(String currentInjuredClientCondition) {
        this.currentInjuredClientCondition = currentInjuredClientCondition;
    }

    public IncidentVitalSignsDto getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(IncidentVitalSignsDto vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public List<IncidentWitnessDto> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<IncidentWitnessDto> witnesses) {
        this.witnesses = witnesses;
    }

    public Boolean getWereOtherIndividualsInvolved() {
        return wereOtherIndividualsInvolved;
    }

    public void setWereOtherIndividualsInvolved(Boolean wereOtherIndividualsInvolved) {
        this.wereOtherIndividualsInvolved = wereOtherIndividualsInvolved;
    }

    public List<IncidentIndividualDto> getInvolvedIndividuals() {
        return involvedIndividuals;
    }

    public void setInvolvedIndividuals(List<IncidentIndividualDto> involvedIndividuals) {
        this.involvedIndividuals = involvedIndividuals;
    }

    public List<MultipartFile> getIncidentPictureFiles() {
        return incidentPictureFiles;
    }

    public void setIncidentPictureFiles(List<MultipartFile> incidentPictureFiles) {
        this.incidentPictureFiles = incidentPictureFiles;
    }

    public List<IncidentPictureDto> getIncidentPictures() {
        return incidentPictures;
    }

    public void setIncidentPictures(List<IncidentPictureDto> incidentPictures) {
        this.incidentPictures = incidentPictures;
    }

    public IncidentNotificationsDto getNotification() {
        return notification;
    }

    public void setNotification(IncidentNotificationsDto notification) {
        this.notification = notification;
    }

    public String getImmediateIntervention() {
        return immediateIntervention;
    }

    public void setImmediateIntervention(String immediateIntervention) {
        this.immediateIntervention = immediateIntervention;
    }

    public String getFollowUpInformation() {
        return followUpInformation;
    }

    public void setFollowUpInformation(String followUpInformation) {
        this.followUpInformation = followUpInformation;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean getArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public long getEventNotificationCount() {
        return eventNotificationCount;
    }

    public void setEventNotificationCount(long eventNotificationCount) {
        this.eventNotificationCount = eventNotificationCount;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public Boolean getIsConversationParticipant() {
        return isConversationParticipant;
    }

    public void setIsConversationParticipant(Boolean conversationParticipant) {
        isConversationParticipant = conversationParticipant;
    }

    public boolean getHasCommunityCareTeamMembersWithEnabledConversations() {
        return hasCommunityCareTeamMembersWithEnabledConversations;
    }

    public void setHasCommunityCareTeamMembersWithEnabledConversations(boolean hasCommunityCareTeamMembersWithEnabledConversations) {
        this.hasCommunityCareTeamMembersWithEnabledConversations = hasCommunityCareTeamMembersWithEnabledConversations;
    }

    public boolean getHasCommunityCareTeamMembersWithEnabledVideoConversations() {
        return hasCommunityCareTeamMembersWithEnabledVideoConversations;
    }

    public void setHasCommunityCareTeamMembersWithEnabledVideoConversations(boolean hasCommunityCareTeamMembersWithEnabledVideoConversations) {
        this.hasCommunityCareTeamMembersWithEnabledVideoConversations = hasCommunityCareTeamMembersWithEnabledVideoConversations;
    }
}
