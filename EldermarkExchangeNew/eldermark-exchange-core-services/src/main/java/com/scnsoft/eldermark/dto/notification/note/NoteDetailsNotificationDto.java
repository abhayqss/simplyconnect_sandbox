package com.scnsoft.eldermark.dto.notification.note;

import com.scnsoft.eldermark.dto.NoteViewData;
import com.scnsoft.eldermark.dto.notification.event.ClientInfoNotificationDto;

public class NoteDetailsNotificationDto implements NoteViewData<NoteEncounterMailDto, NoteServiceStatusCheckMailDto, NoteClientProgramMailDto> {

    boolean isNew;
    private String clientName;
    private boolean isGroupNote;

    private String typeTitle;
    private String subTypeTitle;

    private String noteName;

    private Long admitDate;

    private Long eventId;
    private Long eventDate;
    private String eventTypeTitle;

    private String statusTitle;
    private ClientInfoNotificationDto clientInfo;
    private Long lastModified;
    private String author;
    private String authorRoleTitle;

    private NoteEncounterMailDto encounter;
    private NoteServiceStatusCheckMailDto serviceStatusCheck;
    private NoteClientProgramMailDto clientProgram;

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;

    public ClientInfoNotificationDto getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfoNotificationDto clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public String getClientName() {
        return clientName;
    }

    @Override
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isGroupNote() {
        return isGroupNote;
    }

    public void setGroupNote(boolean groupNote) {
        isGroupNote = groupNote;
    }

    @Override
    public String getTypeTitle() {
        return typeTitle;
    }

    @Override
    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    @Override
    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    @Override
    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
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
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public Long getEventDate() {
        return eventDate;
    }

    @Override
    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String getEventTypeTitle() {
        return eventTypeTitle;
    }

    @Override
    public void setEventTypeTitle(String eventTypeTitle) {
        this.eventTypeTitle = eventTypeTitle;
    }

    @Override
    public String getStatusTitle() {
        return statusTitle;
    }

    @Override
    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public Long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthorRoleTitle() {
        return authorRoleTitle;
    }

    @Override
    public void setAuthorRoleTitle(String authorRoleTitle) {
        this.authorRoleTitle = authorRoleTitle;
    }

    @Override
    public NoteEncounterMailDto getEncounter() {
        return encounter;
    }

    @Override
    public void setEncounter(NoteEncounterMailDto encounter) {
        this.encounter = encounter;
    }

    @Override
    public NoteServiceStatusCheckMailDto getServiceStatusCheck() {
        return serviceStatusCheck;
    }

    @Override
    public void setServiceStatusCheck(NoteServiceStatusCheckMailDto serviceStatusCheck) {
        this.serviceStatusCheck = serviceStatusCheck;
    }

    @Override
    public NoteClientProgramMailDto getClientProgram() {
        return clientProgram;
    }

    @Override
    public void setClientProgram(NoteClientProgramMailDto clientProgram) {
        this.clientProgram = clientProgram;
    }

    @Override
    public String getSubjective() {
        return subjective;
    }

    @Override
    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    @Override
    public String getObjective() {
        return objective;
    }

    @Override
    public void setObjective(String objective) {
        this.objective = objective;
    }

    @Override
    public String getAssessment() {
        return assessment;
    }

    @Override
    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    @Override
    public String getPlan() {
        return plan;
    }

    @Override
    public void setPlan(String plan) {
        this.plan = plan;
    }

    @Override
    public String getNoteName() {
        return noteName;
    }

    @Override
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }
}
