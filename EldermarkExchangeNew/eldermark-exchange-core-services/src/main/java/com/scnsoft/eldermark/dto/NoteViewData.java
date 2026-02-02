package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.dto.notification.note.NoteClientProgramViewData;
import com.scnsoft.eldermark.dto.notification.note.NoteEncounterViewData;
import com.scnsoft.eldermark.dto.notification.note.NoteServiceStatusCheckViewData;

public interface NoteViewData<T extends NoteEncounterViewData, SCC extends NoteServiceStatusCheckViewData, CP extends NoteClientProgramViewData> {

    String getClientName();

    void setClientName(String clientName);  //client

    String getTypeTitle();

    void setTypeTitle(String typeTitle); //type

    String getSubTypeTitle(); // subtype

    void setSubTypeTitle(String subTypeTitle);

    Long getAdmitDate(); // admit/intake date

    void setAdmitDate(Long admitDate);

    Long getEventId(); //event

    void setEventId(Long eventId);

    Long getEventDate();

    void setEventDate(Long eventDate);

    String getEventTypeTitle();

    void setEventTypeTitle(String eventTypeTitle);

    String getStatusTitle(); //status

    void setStatusTitle(String statusTitle);

    String getNoteName();

    void setNoteName(String noteName);

    Long getLastModified(); //date

    void setLastModified(Long lastModified);

    String getAuthor(); //author

    void setAuthor(String author);

    String getAuthorRoleTitle(); //role

    void setAuthorRoleTitle(String authorRoleTitle);

    T getEncounter();

    void setEncounter(T encounter);

    String getSubjective();

    void setSubjective(String subjective);

    String getObjective();

    void setObjective(String objective);

    String getAssessment();

    void setAssessment(String assessment);

    String getPlan();

    void setPlan(String plan);

    SCC getServiceStatusCheck();

    void setServiceStatusCheck(SCC serviceStatusCheck);

    CP getClientProgram();

    void setClientProgram(CP clientProgram);
}
