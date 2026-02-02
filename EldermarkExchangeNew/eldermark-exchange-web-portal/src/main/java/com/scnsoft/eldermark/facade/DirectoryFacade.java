package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.notes.NoteTypeDto;
import com.scnsoft.eldermark.dto.referral.ReferralGroupedCategoriesDto;
import com.scnsoft.eldermark.dto.serviceplan.DomainDto;
import com.scnsoft.eldermark.dto.serviceplan.PriorityDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramSubTypeDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramTypeDto;
import com.scnsoft.eldermark.web.commons.dto.basic.*;

import java.util.Collection;
import java.util.List;

public interface DirectoryFacade {

    @Deprecated
    List<KeyValueDto<Long>> getPrimaryFocuses(Long domainId, Long programSubTypeId);

    @Deprecated
    List<PrimaryFocusAwareKeyValueDto> getServicesTreatmentApproaches(List<Long> primaryFocusIds, Long domainId, Long programSubTypeId);

    List<KeyValueDto<Long>> getLanguageServices();

    List<KeyValueDto<Long>> getMaritalStatus();

    List<KeyValueDto<Long>> getGenders(Boolean biologicalOnly);

    List<DirectoryStateListItemDto<Long>> getStates();

    List<DomainDto> getDomains();

    List<PriorityDto> getPriorities();

    List<RoleDto> getSystemRoles(Boolean includeExternal);

    List<DeviceTypeDto> getDeviceTypes();

    List<NoteTypeDto> getNoteTypes();

    List<NamedTitledValueEntityDto<Integer>> getEmployeeStatuses();

    List<NamedTitledEntityDto> getClientStatuses();

    List<NamedTitledEntityDto> getProspectStatuses();
    List<NamedTitledEntityDto> getRelatedPartyRelationships();

    List<NotificationsPreferencesDto> getDefaultNotificationPreferences(Long careTeamRoleId);

    List<EventTypeGroupDto> getEventGroups();

    List<ProgramTypeDto> getProgramTypes();

    List<ProgramSubTypeDto> getProgramSubTypes();

    List<IdentifiedNamedTitledEntityDto> getEncounterNoteTypes();

    List<IdentifiedNamedTitledEntityDto> getClientProgramNoteTypes();

    List<IdentifiedNamedTitledEntityDto> getReferralServiceStatuses();

    List<IdentifiedNamedTitledEntityDto> getReferralServiceControlRequestStatuses();

    List<IdentifiedNamedTitledEntityDto> getReferralPriorities();

    List<IdentifiedNamedTitledEntityDto> getReferralIntents();

    List<ReferralGroupedCategoriesDto> getReferralCategoriesGrouped();

    List<IdentifiedNamedTitledEntityDto> getReferralDeclineReasons();

    List<NamedTitledEntityDto> getReferralStatuses();

    List<ServiceTypeListItemDto> getServicesInUseExcluding(Long excludeCommunityId);

    List<NamedTitledEntityDto> getLabResearchReasons();

    List<NamedTitledEntityDto> getLabResearchOrderStatuses();

    List<NamedTitledEntityDto> getLabResearchOrderPolicyHolderRelations();

    List<IdentifiedTitledEntityDto> getRaces();

    List<IncidentTypeDto> getIncidentTypes(Integer incidentLevel);

    IncidentLevelReportingSettingsDto getIncidentTypeHelp(Integer incidentLevel);

    List<NamedTitledEntityDto> getIncidentReportStatuses();

    List<IdentifiedNamedEntityDto> getClassMemberTypes();

    List<IdentifiedTitledValueEntityDto<Boolean>> getIncidentPlaceTypes();

    List<IdentifiedTitledValueEntityDto<Boolean>> getIncidentWeatherTypes();

    List<NamedTitledEntityDto> getDocumentESignRequestNotificationMethods();

    List<NamedTitledEntityDto> getDocumentESignSignatureStatuses();

    List<NamedTitledEntityDto> getDocumentESignRequestRecipientTypes();

    List<KeyValueDto<Long>> getSupportTicketTypes();

    List<NamedTitledEntityDto> getDeactivationReasons();

    List<NamedTitledEntityDto> getAppointmentNotificationMethods();

    List<NamedTitledEntityDto> getAppointmentReminderTypes();

    List<NamedTitledEntityDto> getAppointmentServiceCategories();

    List<NamedTitledEntityDto> getAppointmentStatuses();

    List<NamedTitledEntityDto> getAppointmentTypes();

    List<NamedTitledEntityDto> getClientExpenseTypes();

    List<AuditLogActionGroupDto> getAuditLogActivityTypes();

    List<IdentifiedNamedTitledEntityDto> getServiceCategories(Boolean isAccessibleOnly);

    List<ServiceCategoryAwareIdentifiedTitledDto> getServiceTypes(Collection<Long> serviceCategoryIds, String searchText, Boolean isAccessibleOnly);
}
