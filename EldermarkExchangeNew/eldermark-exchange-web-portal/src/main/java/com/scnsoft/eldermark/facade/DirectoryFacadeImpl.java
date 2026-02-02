package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.ReferralPriority;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.notes.NoteTypeDto;
import com.scnsoft.eldermark.dto.referral.ReferralGroupedCategoriesDto;
import com.scnsoft.eldermark.dto.serviceplan.DomainDto;
import com.scnsoft.eldermark.dto.serviceplan.PriorityDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramSubTypeDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramTypeDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedCodedEntity;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.basic.DisplayablePrimaryFocusAwareEntity;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.client.appointment.*;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseType;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.event.EventType;
import com.scnsoft.eldermark.entity.event.incident.ClassMemberType;
import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;
import com.scnsoft.eldermark.entity.event.incident.IncidentType;
import com.scnsoft.eldermark.entity.event.incident.IncidentTypeHelp;
import com.scnsoft.eldermark.entity.lab.LabOrderPolicyHolder;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteType;
import com.scnsoft.eldermark.entity.note.EncounterNoteType;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import com.scnsoft.eldermark.entity.serviceplan.ProgramSubType;
import com.scnsoft.eldermark.entity.serviceplan.ProgramType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedPriority;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.entity.signature.SignatureRequestRecipientType;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.*;
import com.scnsoft.eldermark.web.commons.utils.EnumListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.ClientDeactivationReason.OTHER;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class DirectoryFacadeImpl implements DirectoryFacade {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ListAndItemConverter<ClassMemberType, IdentifiedNamedEntityDto> classMemberTypeEntityToDtoConverter;

    @Autowired
    private ListAndItemConverter<DisplayableNamedEntity, KeyValueDto<Long>> displayableEntityListDtoConverter;

    @Autowired
    private ListAndItemConverter<DisplayablePrimaryFocusAwareEntity, PrimaryFocusAwareKeyValueDto> primaryFocusAwareKeyValueDtoConverter;

    @Autowired
    ListAndItemConverter<ServicePlanNeedType, DomainDto> domainListItemDtoConverter;

    @Autowired
    private ListAndItemConverter<ServicePlanNeedPriority, PriorityDto> priorityListItemDtoConverter;

    @Autowired
    private ListAndItemConverter<CcdCode, KeyValueDto<Long>> ccdCodeDirectoryConverter;

    @Autowired
    private ListAndItemConverter<MedicalDeviceType, DeviceTypeDto> deviceTypeDtoConverter;

    @Autowired
    private ListAndItemConverter<State, DirectoryStateListItemDto<Long>> stateDirectoryConverter;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private ListAndItemConverter<NoteSubType, NoteTypeDto> noteTypeListItemDtoConverter;

    @Autowired
    private Converter<CareTeamRole, RoleDto> careTeamRoleDtoConverter;

    @Autowired
    private ListAndItemConverter<EventTypeCareTeamRoleXref, NotificationsPreferencesDto> defaultNotificationsPreferencesDtoConverter;

    @Autowired
    private ListAndItemConverter<ProgramSubType, ProgramSubTypeDto> programSubTypeDtoConverter;

    @Autowired
    private ListAndItemConverter<ProgramType, ProgramTypeDto> programTypeDtoConverter;

    @Autowired
    private Converter<DisplayableNamedCodedEntity, IdentifiedNamedTitledEntityDto> displayableNamedCodedEntityConverter;

    @Autowired
    private Converter<List<IncidentType>, List<IncidentTypeDto>> incidentTypeListEntityToDtoConverter;

    @Autowired
    private Converter<IncidentTypeHelp, IncidentLevelReportingSettingsDto> incidentTypeHelpEntityToDtoConverter;

    @Autowired
    private ListAndItemConverter<IncidentPlaceType, IdentifiedTitledValueEntityDto<Boolean>> incidentPlaceTypeEntityToDtoConverter;

    @Autowired
    private Converter<List<ServiceType>, List<ServiceTypeListItemDto>> serviceTypeListItemDtoConverter;

    @Autowired
    private SupportTicketTypeService supportTicketTypeService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private Converter<List<ServiceType>, List<ServiceCategoryAwareIdentifiedTitledDto>> serviceTypeConverter;

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @Override
    public List<KeyValueDto<Long>> getPrimaryFocuses(Long domainId, Long programSubTypeId) {
        var primaryFocusesList = directoryService.getPrimaryFocuses(domainId, programSubTypeId).collect(Collectors.toList());
        return displayableEntityListDtoConverter.convertList(primaryFocusesList);
    }

    @Override
    public List<PrimaryFocusAwareKeyValueDto> getServicesTreatmentApproaches(List<Long> primaryFocusIds, Long domainId, Long programSubTypeId) {
        var servicesTreatmentApproaches = directoryService.getServicesTreatmentApproaches(primaryFocusIds, domainId, programSubTypeId)
                .collect(Collectors.toList());
        return primaryFocusAwareKeyValueDtoConverter.convertList(servicesTreatmentApproaches);
    }

    @Override
    public List<KeyValueDto<Long>> getLanguageServices() {
        var languageServicesList = directoryService.getLanguageServices().collect(Collectors.toList());
        return displayableEntityListDtoConverter.convertList(languageServicesList);
    }

    @Override
    public List<DirectoryStateListItemDto<Long>> getStates() {
        var target = directoryService.getStates().collect(Collectors.toList());
        return stateDirectoryConverter.convertList(target);
    }

    @Override
    public List<DomainDto> getDomains() {
        var domainsList = directoryService.getDomains().sorted((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == ServicePlanNeedType.OTHER) {
                return 1;
            }
            if (o2 == ServicePlanNeedType.OTHER) {
                return -1;
            }

            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }).collect(Collectors.toList());
        return domainListItemDtoConverter.convertList(domainsList);
    }

    @Override
    public List<PriorityDto> getPriorities() {
        return directoryService.getPriorities()
                .sorted(Comparator.comparing(ServicePlanNeedPriority::getNumberPriority).reversed())
                .map(priorityListItemDtoConverter::convert)
                .collect(toList());
    }

    @Override
    public List<RoleDto> getSystemRoles(Boolean includeExternal) {
        List<CareTeamRoleCode> rolesToExclude = new ArrayList<>();
        rolesToExclude.add(CareTeamRoleCode.ROLE_NOTIFY_USER);
        if (BooleanUtils.isNotTrue(includeExternal)) {
            rolesToExclude.add(CareTeamRoleCode.ROLE_EXTERNAL_USER);
        }
        return directoryService.getSystemRoles().filter(x -> x != null && !rolesToExclude.contains(x.getCode()))
                .map(careTeamRoleDtoConverter::convert).collect(Collectors.toList());
    }

    public List<NoteTypeDto> getNoteTypes() {
        return directoryService.getNoteTypes().sorted(Comparator.comparing(NoteSubType::getPosition).thenComparing(NoteSubType::getDescription))
                .map(noteTypeListItemDtoConverter::convert).collect(toList());
    }

    @Override
    public List<NamedTitledValueEntityDto<Integer>> getEmployeeStatuses() {
        return directoryService.getEmployeeStatuses()
                .filter(employeeStatus -> !employeeStatus.getHidden())
                .sorted(Comparator.comparing(EmployeeStatus::getText))
                .map(employeeStatus -> new NamedTitledValueEntityDto<>(employeeStatus.name(), employeeStatus.getText(), employeeStatus.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KeyValueDto<Long>> getMaritalStatus() {
        var target = directoryService.getMaritalStatus().collect(Collectors.toList());
        return ccdCodeDirectoryConverter.convertList(target);
    }

    @Override
    public List<KeyValueDto<Long>> getGenders(Boolean biologicalOnly) {
        var target = directoryService.getGenders()
                .filter(ccdCode -> !BooleanUtils.isTrue(biologicalOnly) || (ccdCode.getCode().equals("F") || ccdCode.getCode().equals("M")))
                .collect(Collectors.toList());
        return ccdCodeDirectoryConverter.convertList(target);
    }

    @Override
    public List<NotificationsPreferencesDto> getDefaultNotificationPreferences(Long careTeamRoleId) {
        return defaultNotificationsPreferencesDtoConverter.convertList(careTeamMemberService.getResponsibilitiesForRole(careTeamRoleId));
    }

    @Override
    public List<EventTypeGroupDto> getEventGroups() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var groupedEventTypes = directoryService.getEventTypes(permissionFilter).collect(Collectors.groupingBy(EventType::getEventGroup));
        return groupedEventTypes.entrySet().stream().sorted(Comparator.comparing(evenGroup -> evenGroup.getKey().getPriority()))
                .map(entrySet -> new EventTypeGroupDto(
                        entrySet.getKey().getId(),
                        entrySet.getKey().getName(),
                        entrySet.getKey().getCode(),
                        entrySet.getKey().getService(),
                        entrySet.getValue().stream()
                                .sorted(Comparator.comparing(EventType::getDescription))
                                .map(eventType -> new EventTypeDto(
                                        eventType.getId(), eventType.getCode(),
                                        eventType.getDescription(), eventType.isService())).collect(toList()))).collect(Collectors.toList());
    }

    @Override
    public List<DeviceTypeDto> getDeviceTypes() {
        return deviceTypeDtoConverter.convertList(directoryService.getMedicalDeviceTypes());
    }

    @Override
    public List<NamedTitledEntityDto> getClientStatuses() {
        return directoryService.getClientStatuses()
                .map(clientStatus -> new NamedTitledEntityDto(clientStatus.name(), clientStatus.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NamedTitledEntityDto> getProspectStatuses() {
        return directoryService.getProspectStatuses()
                .map(status -> new NamedTitledEntityDto(status.name(), status.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NamedTitledEntityDto> getRelatedPartyRelationships() {
        return directoryService.getRelatedPartyRelationships()
                .map(relationship -> new NamedTitledEntityDto(relationship.name(), relationship.getTitle()))
                .collect(toList());
    }

    @Override
    public List<ProgramTypeDto> getProgramTypes() {
        return directoryService.getProgramTypes().map(programTypeDtoConverter::convert).collect(toList());
    }

    @Override
    public List<ProgramSubTypeDto> getProgramSubTypes() {
        return directoryService.getProgramSubTypes().map(programSubTypeDtoConverter::convert).collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getEncounterNoteTypes() {
        return directoryService.getEncounterNoteTypes().sorted(Comparator.comparing(EncounterNoteType::getDescription))
                .map(item -> new IdentifiedNamedTitledEntityDto(item.getId(), item.getCode(), item.getDescription())).collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getClientProgramNoteTypes() {
        return directoryService.getClientProgramNoteTypes().sorted(Comparator.comparing(ClientProgramNoteType::getDescription))
                .map(item -> new IdentifiedNamedTitledEntityDto(item.getId(), item.getCode(), item.getDescription())).collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getReferralServiceStatuses() {
        return directoryService.getReferralServiceStatuses()
                .map(item -> new IdentifiedNamedTitledEntityDto(item.getStatusId(), item.name(), item.getDisplayName())).collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getReferralServiceControlRequestStatuses() {
        return directoryService.getReferralServiceControlRequestStatuses()
                .map(item -> new IdentifiedNamedTitledEntityDto(item.getRequestStatusId(), item.name(), item.getDisplayName())).collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getReferralPriorities() {
        List<String> prioritiesToExclude = new ArrayList<>();
        prioritiesToExclude.add(ReferralPriority.ASAP.name());
        prioritiesToExclude.add(ReferralPriority.STAT.name());
        return directoryService.getReferralPriorities()
                .map(displayableNamedCodedEntityConverter::convert)
                .filter(Objects::nonNull)
                .filter(dto -> !prioritiesToExclude.contains(dto.getName()))
                .collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getReferralIntents() {
        return directoryService.getReferralIntents()
                .map(displayableNamedCodedEntityConverter::convert)
                .collect(toList());
    }

    @Override
    public List<ReferralGroupedCategoriesDto> getReferralCategoriesGrouped() {
        return directoryService.getReferralCategoriesGrouped()
                .map(group -> new ReferralGroupedCategoriesDto(group.getId(), group.getCode(), group.getDisplayName(),
                        group.getCategories().stream().map(displayableNamedCodedEntityConverter::convert).collect(toList())
                ))
                .collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getReferralDeclineReasons() {
        return directoryService.getReferralDeclineReasons().map(displayableNamedCodedEntityConverter::convert).collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getReferralStatuses() {
        return Stream.of(ReferralStatus.values())
                .map(s -> new NamedTitledEntityDto(s.name(), s.getDisplayName()))
                .sorted(Comparator.comparing(NamedTitledEntityDto::getTitle))
                .collect(toList());
    }

    @Override
    public List<ServiceTypeListItemDto> getServicesInUseExcluding(Long excludeCommunityId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var services = serviceTypeService.findAllowedForReferralInUse(permissionFilter, excludeCommunityId);
        return serviceTypeListItemDtoConverter.convert(services);
    }

    @Override
    public List<NamedTitledEntityDto> getLabResearchReasons() {
        return directoryService.getLabResearchReasons()
                .map(labResearchOrderReason -> new NamedTitledEntityDto(labResearchOrderReason.name(), labResearchOrderReason.getValue()))
                .sorted(Comparator.comparing(NamedTitledEntityDto::getTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<NamedTitledEntityDto> getLabResearchOrderStatuses() {
        return directoryService.getLabResearchOrderStatuses()
                .map(labResearchOrderStatus -> new NamedTitledEntityDto(labResearchOrderStatus.name(), labResearchOrderStatus.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NamedTitledEntityDto> getLabResearchOrderPolicyHolderRelations() {
        return Stream.of(LabOrderPolicyHolder.values())
                .map(relation -> new NamedTitledEntityDto(relation.name(), relation.getDisplayName()))
                .collect(toList());
    }

    @Override
    public List<IdentifiedTitledEntityDto> getRaces() {
        return directoryService.getRaces()
                .map(ccdCode -> new IdentifiedTitledEntityDto(ccdCode.getId(), ccdCode.getDisplayName()))
                .sorted(byTitleDescOtherLast())
                .collect(toList());
    }

    public Comparator<IdentifiedTitledEntityDto> byTitleDescOtherLast() {
        return (race1, race2) -> {
            if (CareCoordinationConstants.OTHER_RACE_DISPLAY_NAME.equalsIgnoreCase(race1.getTitle())) {
                return 1;
            }
            if (CareCoordinationConstants.OTHER_RACE_DISPLAY_NAME.equalsIgnoreCase(race2.getTitle())) {
                return -1;
            }
            return race2.getTitle().compareTo(race1.getTitle());
        };
    }

    @Override
    public List<IncidentTypeDto> getIncidentTypes(Integer incidentLevel) {
        return incidentTypeListEntityToDtoConverter.convert(directoryService.getIncidentTypes(incidentLevel));
    }

    @Override
    public IncidentLevelReportingSettingsDto getIncidentTypeHelp(Integer incidentLevel) {
        return incidentTypeHelpEntityToDtoConverter.convert(directoryService.getIncidentTypeHelp(incidentLevel));
    }

    @Override
    public List<IdentifiedNamedEntityDto> getClassMemberTypes() {
        return classMemberTypeEntityToDtoConverter.convertList(directoryService.getClassMemberTypes());
    }

    @Override
    public List<IdentifiedTitledValueEntityDto<Boolean>> getIncidentPlaceTypes() {
        return incidentPlaceTypeEntityToDtoConverter.convertList(directoryService.getIncidentPlaceTypes());
    }

    @Override
    public List<IdentifiedTitledValueEntityDto<Boolean>> getIncidentWeatherTypes() {
        return directoryService.getIncidentWeatherConditionTypes()
                .map(weatherType -> new IdentifiedTitledValueEntityDto<>(weatherType.getId(), weatherType.getName(), weatherType.getFreeText()))
                .collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getIncidentReportStatuses() {
        return directoryService.getIncidentReportStatuses()
                .map(s -> new NamedTitledEntityDto(s.name(), s.getDisplayName()))
                .collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getDocumentESignRequestNotificationMethods() {
        return Stream.of(SignatureRequestNotificationMethod.values())
                .map(m -> new NamedTitledEntityDto(m.name(), m.getTitle()))
                .collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getDocumentESignSignatureStatuses() {
        return Stream.of(DocumentSignatureStatus.values())
                .map(it -> new NamedTitledEntityDto(it.name(), it.getTitle()))
                .collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getDocumentESignRequestRecipientTypes() {
        return Stream.of(SignatureRequestRecipientType.values())
                .map(it -> new NamedTitledEntityDto(it.name(), it.getTitle()))
                .collect(toList());
    }

    @Override
    public List<KeyValueDto<Long>> getSupportTicketTypes() {
        return supportTicketTypeService.findAll().stream()
                .sorted(Comparator.comparing(SupportTicketType::getTitle))
                .map(it -> new KeyValueDto<>(it.getId(), it.getTitle()))
                .collect(toList());
    }

    @Override
    public List<NamedTitledEntityDto> getDeactivationReasons() {
        var reasons = Stream.of(ClientDeactivationReason.values())
                .sorted(Comparator.comparing(ClientDeactivationReason::getTitle))
                .map(it -> new NamedTitledEntityDto(it.name(), it.getTitle()))
                .collect(toList());
        EnumListUtils.moveItemToEnd(reasons, OTHER.getTitle());
        return reasons;
    }

    @Override
    public List<NamedTitledEntityDto> getAppointmentNotificationMethods() {
        var notificationMethods = Stream.of(ClientAppointmentNotificationMethod.values())
                .map(item -> new NamedTitledEntityDto(item.name(), item.getDisplayName()))
                .collect(toList());
        return notificationMethods;
    }

    @Override
    public List<NamedTitledEntityDto> getAppointmentReminderTypes() {
        var reminderTypes = Stream.of(ClientAppointmentReminder.values())
                .map(item -> new NamedTitledEntityDto(item.name(), item.getDisplayName()))
                .collect(toList());
        return reminderTypes;
    }

    @Override
    public List<NamedTitledEntityDto> getAppointmentServiceCategories() {
        var serviceCategories = Stream.of(ClientAppointmentServiceCategory.values())
                .sorted(Comparator.comparing(ClientAppointmentServiceCategory::getDisplayName))
                .map(item -> new NamedTitledEntityDto(item.name(), item.getDisplayName()))
                .collect(toList());
        EnumListUtils.moveItemToEnd(serviceCategories, ClientAppointmentServiceCategory.OTHER.getDisplayName());
        return serviceCategories;
    }

    @Override
    public List<NamedTitledEntityDto> getAppointmentStatuses() {
        var statuses = Stream.of(ClientAppointmentStatus.values())
                .map(item -> new NamedTitledEntityDto(item.name(), item.getDisplayName()))
                .collect(toList());
        return statuses;
    }

    @Override
    public List<NamedTitledEntityDto> getAppointmentTypes() {
        var types = Stream.of(ClientAppointmentType.values())
                .sorted(Comparator.comparing(ClientAppointmentType::getDisplayName))
                .map(item -> new NamedTitledEntityDto(item.name(), item.getDisplayName()))
                .collect(toList());
        EnumListUtils.moveItemToEnd(types, ClientAppointmentType.OTHER.getDisplayName());
        return types;
    }

    @Override
    public List<NamedTitledEntityDto> getClientExpenseTypes() {
        return Stream.of(ClientExpenseType.values())
                .sorted(Comparator.comparing(ClientExpenseType::getDisplayName))
                .map(it -> new NamedTitledEntityDto(it.name(), it.getDisplayName()))
                .collect(toList());
    }

    @Override
    public List<AuditLogActionGroupDto> getAuditLogActivityTypes() {
        return directoryService.getAuditLogActivityTypes().entrySet().stream()
                .map(set -> new AuditLogActionGroupDto(
                        set.getKey().name(),
                        set.getKey().getDisplayName(),
                        set.getValue().stream()
                                .map(a -> new IdentifiedNamedTitledEntityDto(
                                        a.getId(),
                                        a.name(),
                                        a.getDisplayName()))
                                .collect(Collectors.toList())))
                .collect(toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getServiceCategories(Boolean isAccessibleOnly) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return serviceCategoryService.findAllSortByDisplayName(isAccessibleOnly, permissionFilter).stream()
                .map(sc -> new IdentifiedNamedTitledEntityDto(sc.getId(), sc.getKey(), sc.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceCategoryAwareIdentifiedTitledDto> getServiceTypes(Collection<Long> serviceCategoryIds, String searchText, Boolean isAccessibleOnly) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return serviceTypeConverter.convert(serviceTypeService.findAllByCategoryIdsAndDisplayNameLike(serviceCategoryIds, searchText, isAccessibleOnly, permissionFilter));
    }
}
