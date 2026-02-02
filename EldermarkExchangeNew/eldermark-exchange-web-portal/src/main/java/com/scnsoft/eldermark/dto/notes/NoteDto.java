package com.scnsoft.eldermark.dto.notes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.security.projection.dto.NoteSecurityFieldsAware;
import com.scnsoft.eldermark.dto.NoteViewData;
import com.scnsoft.eldermark.validation.SpELAssert;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.ViewableIdentifiedActiveAwareNamedEntityDto;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        value = "#isNotEmpty(subjective) || #isNotEmpty(objective) || #isNotEmpty(assessment) || #isNotEmpty(plan)",
                        message = "At least one of the fields: subjective, objective, assessment, plan shouldn't be empty",
                        helpers = StringUtils.class
                )
        }
)
public class NoteDto implements NoteViewData<EncounterDto, ServiceStatusCheckDto, ClientProgramDto>, NoteSecurityFieldsAware {

    private Long id;

    private String author;
    private String authorRoleTitle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long clientId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clientName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ViewableIdentifiedActiveAwareNamedEntityDto> clients;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String noteName;

    @NotNull
    private Long noteDate;
    private Long lastModified;
    private Long admitDateId;
    private Long admitDate;

    private Long eventId;
    private Long eventDate;
    private String eventTypeTitle;

    @NotNull
    private Long subTypeId;
    private String subTypeTitle;
    private String typeName;
    private String typeTitle;
    private String statusName;
    private String statusTitle;

    @Valid
    private EncounterDto encounter;

    @Valid
    private ServiceStatusCheckDto serviceStatusCheck;

    @Valid
    private ClientProgramDto clientProgram;

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;

    private boolean canEdit;
    private boolean canViewClient;
    private Boolean clientActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorRoleTitle() {
        return authorRoleTitle;
    }

    public void setAuthorRoleTitle(String authorRoleTitle) {
        this.authorRoleTitle = authorRoleTitle;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<ViewableIdentifiedActiveAwareNamedEntityDto> getClients() {
        return clients;
    }

    public void setClients(List<ViewableIdentifiedActiveAwareNamedEntityDto> clients) {
        this.clients = clients;
    }

    @Override
    public String getNoteName() {
        return noteName;
    }

    @Override
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public Long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(Long noteDate) {
        this.noteDate = noteDate;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getAdmitDateId() {
        return admitDateId;
    }

    public void setAdmitDateId(Long admitDateId) {
        this.admitDateId = admitDateId;
    }

    @Override
    public Long getAdmitDate() {
        return admitDate;
    }

    @Override
    public void setAdmitDate(Long admitDate) {
        this.admitDate = admitDate;
    }

    @Override
    public Long getEventId() {
        return eventId;
    }

    @Override
    @JsonIgnore
    public List<Long> getClientIds() {
        if (getClients() == null) {
            return null;
        }
        return getClients().stream()
                .map(IdentifiedNamedEntityDto::getId)
                .collect(Collectors.toList());
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTypeTitle() {
        return eventTypeTitle;
    }

    public void setEventTypeTitle(String eventTypeTitle) {
        this.eventTypeTitle = eventTypeTitle;
    }

    public Long getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(Long subTypeId) {
        this.subTypeId = subTypeId;
    }

    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
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

    public EncounterDto getEncounter() {
        return encounter;
    }

    public void setEncounter(EncounterDto encounter) {
        this.encounter = encounter;
    }

    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanViewClient() {
        return canViewClient;
    }

    public void setCanViewClient(boolean canViewClient) {
        this.canViewClient = canViewClient;
    }

    public Boolean getClientActive() {
        return clientActive;
    }

    public void setClientActive(Boolean clientActive) {
        this.clientActive = clientActive;
    }

    public ServiceStatusCheckDto getServiceStatusCheck() {
        return serviceStatusCheck;
    }

    public void setServiceStatusCheck(ServiceStatusCheckDto serviceStatusCheck) {
        this.serviceStatusCheck = serviceStatusCheck;
    }

    @Override
    public ClientProgramDto getClientProgram() {
        return clientProgram;
    }

    @Override
    public void setClientProgram(ClientProgramDto clientProgram) {
        this.clientProgram = clientProgram;
    }
}
