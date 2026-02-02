package com.scnsoft.eldermark.mobile.dto.home;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeSectionsDto {

    private List<DocumentHomeSectionDto> documents;
    private List<CareTeamUpdateHomeSectionDto> careTeamUpdates;
    private List<MedicationUpdateHomeSectionDto> medicationUpdates;
    private List<MissedChatsAndCallsHomeSectionDto> missedChatsAndCalls;

    public List<DocumentHomeSectionDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentHomeSectionDto> documents) {
        this.documents = documents;
    }

    public List<CareTeamUpdateHomeSectionDto> getCareTeamUpdates() {
        return careTeamUpdates;
    }

    public void setCareTeamUpdates(List<CareTeamUpdateHomeSectionDto> careTeamUpdates) {
        this.careTeamUpdates = careTeamUpdates;
    }

    public List<MedicationUpdateHomeSectionDto> getMedicationUpdates() {
        return medicationUpdates;
    }

    public void setMedicationUpdates(List<MedicationUpdateHomeSectionDto> medicationUpdates) {
        this.medicationUpdates = medicationUpdates;
    }

    public List<MissedChatsAndCallsHomeSectionDto> getMissedChatsAndCalls() {
        return missedChatsAndCalls;
    }

    public void setMissedChatsAndCalls(List<MissedChatsAndCallsHomeSectionDto> missedChatsAndCalls) {
        this.missedChatsAndCalls = missedChatsAndCalls;
    }
}
