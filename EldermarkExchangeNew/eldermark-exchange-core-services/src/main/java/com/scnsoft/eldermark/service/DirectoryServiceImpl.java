package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.audit.AuditLogActivityGroup;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dto.prospect.ProspectStatus;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
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
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.service.internal.EntityListUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private PrimaryFocusDao primaryFocusDao;

    @Autowired
    private ServicesTreatmentApproachDao servicesTreatmentApproachDao;

    @Autowired
    private LanguageServiceDao languageServiceDao;

    @Autowired
    private MedicalDeviceTypeDao medicalDeviceTypeDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private NoteSubTypeDao noteSubTypeDao;

    @Autowired
    private EventTypeDao eventTypeDao;

    @Autowired
    EventTypeCareTeamRoleXrefDao eventTypeCareTeamRoleXrefDao;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private ProgramTypeDao programTypeDao;

    @Autowired
    private ProgramSubTypeDao programSubTypeDao;

    @Autowired
    private EncounterNoteTypeDao encounterNoteTypeDao;

    @Autowired
    private ClientProgramNoteTypeDao clientProgramNoteTypeDao;

    @Autowired
    private MappingDataToServiceTreatmentApproachService mappingDataToServiceTreatmentApproachService;

    @Autowired
    private ReferralPriorityDao referralPriorityDao;

    @Autowired
    private ReferralIntentDao referralIntentDao;

    @Autowired
    private ReferralCategoryGroupDao referralCategoryGroupDao;

    @Autowired
    private ReferralDeclineReasonDao referralDeclineReasonDao;

    @Autowired
    private IncidentTypeDao incidentTypeDao;

    @Autowired
    private IncidentTypeHelpDao incidentTypeHelpDao;

    @Autowired
    private ClassMemberTypeDao classMemberTypeDao;

    @Autowired
    private IncidentPlaceTypeDao incidentPlaceTypeDao;

    @Autowired
    private IncidentWeatherConditionTypeDao incidentWeatherConditionTypeDao;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ServiceCategoryDao serviceCategoryDao;

    @Override
    @Transactional(readOnly = true)
    public Stream<PrimaryFocus> getPrimaryFocuses(Long domainId, Long programSubTypeId) {
        List<Long> primaryFocusIds = null;
        if (programSubTypeId != null) {
            primaryFocusIds = mappingDataToServiceTreatmentApproachService.findByProgramSubType(programSubTypeId)
                    .stream()
                    .map(m -> m.getServicesTreatmentApproach().getPrimaryFocusId())
                    .collect(Collectors.toList());
        }
        if (domainId != null && CollectionUtils.isEmpty(primaryFocusIds)) {
            primaryFocusIds = mappingDataToServiceTreatmentApproachService.findByServicePlanNeedType(ServicePlanNeedType.findByDomainId(domainId))
                    .stream()
                    .map(m -> m.getServicesTreatmentApproach().getPrimaryFocusId())
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(primaryFocusIds)) {
            return primaryFocusDao.findByIdIn(primaryFocusIds, Sort.unsorted()).stream();
        } else {
            return primaryFocusDao.findByOrderByDisplayNameAsc().stream();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ServicesTreatmentApproach> getServicesTreatmentApproaches(List<Long> primaryFocusIds, Long domainId, Long programSubTypeId) {
        if (CollectionUtils.isNotEmpty(primaryFocusIds)) {
            Sort sort = Sort.by(Direction.ASC, "primaryFocusId", "displayName");
            List<ServicesTreatmentApproach> list = new ArrayList<>();
            List<ServicesTreatmentApproach> servicesTreatmentApproachList = servicesTreatmentApproachDao
                    .findByPrimaryFocusIdIn(primaryFocusIds, sort);
            primaryFocusIds.forEach(pf -> {
                List<ServicesTreatmentApproach> service = servicesTreatmentApproachList.stream()
                        .filter(x -> x.getPrimaryFocusId() == pf.longValue()).collect(Collectors.toList());
                EntityListUtils.moveItemToEnd(service, "Other");
                list.addAll(service);
            });
            return list.stream();
        }
        if (programSubTypeId != null) {
            var mappingData = mappingDataToServiceTreatmentApproachService.findByProgramSubType(programSubTypeId);
            if (CollectionUtils.isNotEmpty(mappingData)) {
                return mappingData.stream().map(ProgramSubTypeToServiceTreatmentApproach::getServicesTreatmentApproach);
            }
        }
        if (domainId != null) {
            var mappingData = mappingDataToServiceTreatmentApproachService.findByServicePlanNeedType(ServicePlanNeedType.findByDomainId(domainId));
            if (CollectionUtils.isNotEmpty(mappingData)) {
                return mappingData.stream().map(ServicePlanNeedTypeToServiceTreatmentApproach::getServicesTreatmentApproach);
            }
        }
        return servicesTreatmentApproachDao.findByOrderByDisplayNameAsc().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<LanguageService> getLanguageServices() {
        var StreamLanguageServices = languageServiceDao.findByOrderByDisplayNameAsc();
        EntityListUtils.moveItemToEnd(StreamLanguageServices, "Other languages");
        return StreamLanguageServices.stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<PrimaryFocus> findPrimaryFocuses(List<Long> ids) {
        return primaryFocusDao.findAllById(ids).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ServicesTreatmentApproach> findServicesTreatmentApproachesByIds(List<Long> ids) {
        return servicesTreatmentApproachDao.findAllById(ids).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<LanguageService> findLanguageServicesByIds(List<Long> ids) {
        return languageServiceDao.findAllById(ids).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<CcdCode> getMaritalStatus() {
        return ccdCodeDao.findByValueSetName("MaritalStatus").stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<CcdCode> getGenders() {
        return ccdCodeDao.findByValueSetName("AdministrativeGender").stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<State> getStates() {
        return stateDao.findAll(Sort.by(Direction.ASC, "name")).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ServicePlanNeedType> getDomains() {
        return Arrays.asList(ServicePlanNeedType.values()).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ServicePlanNeedPriority> getPriorities() {
        return Arrays.stream(ServicePlanNeedPriority.values());
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<NoteSubType> getNoteTypes() {
        return noteSubTypeDao.findAll().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalDeviceType> getMedicalDeviceTypes() {
        return medicalDeviceTypeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<EmployeeStatus> getEmployeeStatuses() {
        return Arrays.asList(EmployeeStatus.values()).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<CareTeamRole> getSystemRoles() {
        return careTeamRoleDao.findAll(Sort.by(CareTeamRole_.NAME)).stream();
    }

    @Override
    public Stream<ClientStatus> getClientStatuses() {
        return Arrays.asList(ClientStatus.values()).stream();
    }

    @Override
    public Stream<ProspectStatus> getProspectStatuses() {
        return Arrays.stream(ProspectStatus.values());
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<EventType> getEventTypes(PermissionFilter permissionFilter) {
        var roles = permissionFilter.getEmployees().stream().map(employee -> employee.getCareTeamRole().getCode()).collect(Collectors.toList());
        var allEventTypes = eventTypeDao.findAll().stream();
        var disabledIds = new HashSet<>(eventTypeService.findDisabledIdsByRoles(roles));
        return allEventTypes.filter(eventType -> !disabledIds.contains(eventType.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ProgramType> getProgramTypes() {
        return programTypeDao.findAll().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ProgramSubType> getProgramSubTypes() {
        return programSubTypeDao.findAll().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<EncounterNoteType> getEncounterNoteTypes() {
        return encounterNoteTypeDao.findAll().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ClientProgramNoteType> getClientProgramNoteTypes() {
        return clientProgramNoteTypeDao.findAll().stream();
    }

    @Override
    public Stream<ReferralServiceStatus> getReferralServiceStatuses() {
        return Arrays.stream(ReferralServiceStatus.values());
    }

    @Override
    public Stream<ReferralServiceRequestStatus> getReferralServiceControlRequestStatuses() {
        return Arrays.stream(ReferralServiceRequestStatus.values());
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ReferralPriority> getReferralPriorities() {
        return referralPriorityDao.findAll(Sort.by(ReferralPriority_.ORDER)).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ReferralIntent> getReferralIntents() {
        return referralIntentDao.findAll(Sort.by(ReferralIntent_.ORDER)).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ReferralCategoryGroup> getReferralCategoriesGrouped() {
        return referralCategoryGroupDao.findAll(Sort.by(ReferralCategoryGroup_.ORDER)).stream();
    }


    @Override
    @Transactional(readOnly = true)
    public Stream<ReferralDeclineReason> getReferralDeclineReasons() {
        return referralDeclineReasonDao.findAll().stream();
    }

    @Override
    public Stream<LabResearchOrderReason> getLabResearchReasons() {
        return Arrays.stream(LabResearchOrderReason.values());
    }

    @Override
    public Stream<LabResearchOrderStatus> getLabResearchOrderStatuses() {
        return Arrays.stream(LabResearchOrderStatus.values());
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<CcdCode> getRaces() {
        return ccdCodeDao.findByValueSetNameAndDisplayNameInAndInterpretation("Race", CareCoordinationConstants.AVAILABLE_RACES, false).stream();
    }

    @Override
    public List<IncidentType> getIncidentTypes(Integer incidentLevel) {
        if (incidentLevel == null) {
            return incidentTypeDao.findAll();
        }
        return incidentTypeDao.findByIncidentLevel(incidentLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentTypeHelp getIncidentTypeHelp(Integer incidentLevel) {
        return incidentTypeHelpDao.findByIncidentLevel(incidentLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassMemberType> getClassMemberTypes() {
        return classMemberTypeDao.findAll();
    }

    @Override
    public List<IncidentPlaceType> getIncidentPlaceTypes() {
        List<IncidentPlaceType> incidentPlaceTypes = incidentPlaceTypeDao.findByOrderByName();
        moveOtherIncidentPlaceToEnd(incidentPlaceTypes);
        return incidentPlaceTypes;
    }

    private void moveOtherIncidentPlaceToEnd(List<IncidentPlaceType> list) {
        IncidentPlaceType itemToBeMoved = null;
        for (IncidentPlaceType item : list) {
            if (item.getName().contains("Other")) {
                itemToBeMoved = item;
                list.remove(item);
                break;
            }
        }
        list.add(itemToBeMoved);
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<IncidentWeatherConditionType> getIncidentWeatherConditionTypes() {
        return incidentWeatherConditionTypeDao.findAll().stream();
    }

    @Override
    public Stream<IncidentReportStatus> getIncidentReportStatuses() {
        return Arrays.stream(IncidentReportStatus.values()).filter(incidentReportStatus -> !incidentReportStatus.equals(IncidentReportStatus.DELETED));
    }

    @Override
    public Stream<RelatedPartyRelationship> getRelatedPartyRelationships() {
        return Arrays.stream(RelatedPartyRelationship.values());
    }

    @Override
    public Map<AuditLogActivityGroup, TreeSet<AuditLogActivity>> getAuditLogActivityTypes() {
        return Arrays.stream(AuditLogActivity.values())
                .filter(a -> a.getGroup() != null)
                .collect(Collectors.groupingBy(
                        AuditLogActivity::getGroup,
                        () -> new TreeMap<>(Comparator.comparing(AuditLogActivityGroup::getDisplayName)),
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AuditLogActivity::getDisplayName)))
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<ServiceCategory> findServiceCategories(Collection<Long> ids) {
        return serviceCategoryDao.findAllById(ids).stream();
    }
}
