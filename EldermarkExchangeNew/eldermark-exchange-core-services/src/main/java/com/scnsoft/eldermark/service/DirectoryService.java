package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.prospect.ProspectStatus;
import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.audit.AuditLogActivityGroup;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.prospect.ProspectStatus;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.event.EventType;
import com.scnsoft.eldermark.entity.event.incident.*;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderReason;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteType;
import com.scnsoft.eldermark.entity.note.EncounterNoteType;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.prospect.RelatedPartyRelationship;
import com.scnsoft.eldermark.entity.referral.ReferralCategoryGroup;
import com.scnsoft.eldermark.entity.referral.ReferralDeclineReason;
import com.scnsoft.eldermark.entity.referral.ReferralIntent;
import com.scnsoft.eldermark.entity.referral.ReferralPriority;
import com.scnsoft.eldermark.entity.serviceplan.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

public interface DirectoryService {

    @Deprecated
    Stream<PrimaryFocus> getPrimaryFocuses(Long domainId, Long programSubTypeId);

    @Deprecated
    Stream<ServicesTreatmentApproach> getServicesTreatmentApproaches(List<Long> primaryFocusIds, Long domainId, Long programSubTypeId);

    Stream<LanguageService> getLanguageServices();

    @Deprecated
    Stream<PrimaryFocus> findPrimaryFocuses(List<Long> ids);

    @Deprecated
    Stream<ServicesTreatmentApproach> findServicesTreatmentApproachesByIds(List<Long> ids);

    Stream<LanguageService> findLanguageServicesByIds(List<Long> ids);

    Stream<CcdCode> getMaritalStatus();

    Stream<CcdCode> getGenders();

    Stream<State> getStates();

    Stream<ServicePlanNeedType> getDomains();

    Stream<ServicePlanNeedPriority> getPriorities();

    Stream<NoteSubType> getNoteTypes();

    List<MedicalDeviceType> getMedicalDeviceTypes();

    Stream<EmployeeStatus> getEmployeeStatuses();

    Stream<CareTeamRole> getSystemRoles();

    Stream<ClientStatus> getClientStatuses();

    Stream<ProspectStatus> getProspectStatuses();

    Stream<EventType> getEventTypes(PermissionFilter permissionFilter);

    Stream<ProgramType> getProgramTypes();

    Stream<ProgramSubType> getProgramSubTypes();

    Stream<EncounterNoteType> getEncounterNoteTypes();

    Stream<ClientProgramNoteType> getClientProgramNoteTypes();

    Stream<ReferralServiceStatus> getReferralServiceStatuses();

    Stream<ReferralServiceRequestStatus> getReferralServiceControlRequestStatuses();

    Stream<ReferralPriority> getReferralPriorities();

    Stream<ReferralIntent> getReferralIntents();

    Stream<ReferralCategoryGroup> getReferralCategoriesGrouped();

    Stream<ReferralDeclineReason> getReferralDeclineReasons();

    Stream<LabResearchOrderReason> getLabResearchReasons();

    Stream<LabResearchOrderStatus> getLabResearchOrderStatuses();

    Stream<CcdCode> getRaces();

    List<IncidentType> getIncidentTypes(Integer incidentLevel);

    IncidentTypeHelp getIncidentTypeHelp(Integer incidentLevel);

    List<ClassMemberType> getClassMemberTypes();

    List<IncidentPlaceType> getIncidentPlaceTypes();

    Stream<IncidentWeatherConditionType> getIncidentWeatherConditionTypes();

    Stream<IncidentReportStatus> getIncidentReportStatuses();

    Stream<RelatedPartyRelationship> getRelatedPartyRelationships();

    Map<AuditLogActivityGroup, TreeSet<AuditLogActivity>> getAuditLogActivityTypes();

    Stream<ServiceCategory> findServiceCategories(Collection<Long> ids);
}
