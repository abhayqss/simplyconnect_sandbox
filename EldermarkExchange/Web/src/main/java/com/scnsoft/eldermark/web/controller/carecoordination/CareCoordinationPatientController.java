package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.dao.exceptions.NotUniqueValueException;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.IncidentFacadeWeb;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.services.marketplace.internal.InNetworkInsurancesDtoSupplier;
import com.scnsoft.eldermark.services.marketplace.internal.InsurancePlansDtoSupplier;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.SharingOption;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentsFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesGroupDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.form.DocumentFilter;
import com.scnsoft.eldermark.shared.form.UploadDocumentForm;
import com.scnsoft.eldermark.web.exception.BadRequestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by pzhurba on 21-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/patients/patient/{patientId}")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationPatientController {

    @Autowired
    PatientFacade patientFacade;

    @Autowired
    CareTeamService careTeamService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CareTeamRoleService careTeamRoleService;

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    EventService eventService;

    @Autowired
    StateService stateService;

    @Autowired
    ContactService contactService;

    @Autowired
    private MPIService mpiService;

    @Autowired
    CommunityCrudService communityCrudService;

    @Autowired
    CareCoordinationCommunityDao careCoordinationCommunityDao;

    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DocumentFacade documentFacade;

    @Autowired
    private NucleusInfoService nucleusService;

    @Autowired
    CcdFacade ccdFacade;

    @Autowired
    private InNetworkInsurancesDtoSupplier inNetworkInsurancesDtoSupplier;

    @Autowired
    private InsurancePlansDtoSupplier insurancePlansDtoSupplier;

    @Autowired
    ResidentDeviceService residentDeviceService;

    @Autowired
    private IncidentFacadeWeb incidentFacade;

    @RequestMapping(value = "/validateDevices", method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceDto> checkDeviceID(@RequestParam("deviceId") String deviceId,
            @RequestParam("deviceIdSecondary") String deviceIdSecondary, @RequestParam("facilityId") Long facilityId,
            @PathVariable("patientId") Long patientId) {
        ResidentDevice device;
        List<DeviceDto> dtoList = new ArrayList<>();

        if (patientId == 0) {
            device = residentDeviceService.findByDeviceIdAndFacilityId(deviceId, facilityId);
            if (device != null) {
                dtoList.add(deviceToDto(device));
            }
            if (!deviceIdSecondary.isEmpty() && deviceIdSecondary != null) {
                device = residentDeviceService.findByDeviceIdAndFacilityId(deviceIdSecondary, facilityId);
                if (device != null) {
                    dtoList.add(deviceToDto(device));
                }
            }
        } else {
            device = residentDeviceService.findIfUsedByAnotherResidentAndFacility(deviceId, patientId, facilityId);
            if (device != null) {
                dtoList.add(deviceToDto(device));
            }
            if (!deviceIdSecondary.isEmpty() && deviceIdSecondary != null) {
                device = residentDeviceService.findIfUsedByAnotherResidentAndFacility(deviceIdSecondary, patientId,
                        facilityId);
                if (device != null) {
                    dtoList.add(deviceToDto(device));
                }
            }
        }
        return dtoList;
    }

    @RequestMapping(value = "/events/{eventId}/event-details", method = RequestMethod.GET)
    public String initEventDetails(@PathVariable("eventId") Long eventId, Model model) {
        eventService.checkAccess(eventId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("event", eventService.getEventDetails(eventId));
        model.addAttribute("canCurrentUserCreateIr", incidentFacade.canCurrentUserCreateIncidentReport(eventId));
        return "patient.event.details";
    }

    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    public Page<EventListItemDto> getEvents(@PathVariable("patientId") Long patientId, Pageable pageRequest) {
        EventFilterDto eventFilter = new EventFilterDto();
        eventFilter.setPatientId(patientId);
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        return eventService.list(employeeIds, eventFilter, pageRequest);
    }

    @RequestMapping(value = "/event/{eventId}/page-number", method = RequestMethod.GET)
    @ResponseBody
    public Integer getSentNotifications(@PathVariable("eventId") Long eventId,
            @PathVariable("patientId") Long patientId) {
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        return eventService.getPageNumber(eventId, employeeIds, patientId);
    }

    @RequestMapping(value = "/event-new", method = RequestMethod.GET)
    public String initNewEventView(@PathVariable("patientId") Long patientId, final Model model) {
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientFacade.getPatientDto(patientId, false, false));
        eventDto.setEmployee(new EmployeeDto());
        eventDto.getEmployee().setFirstName(SecurityUtils.getAuthenticatedUser().getEmployeeFirstName());
        eventDto.getEmployee().setLastName(SecurityUtils.getAuthenticatedUser().getEmployeeLastName());
        eventDto.setEventDetails(new EventDetailsDto());

        model.addAttribute("eventDto", eventDto);
        return "event.new";
    }

    @RequestMapping(value = "/event-new", method = RequestMethod.POST)
    @ResponseBody
    public void saveEvent(@ModelAttribute("eventDto") EventDto eventDto) {
        eventDto.getEmployee().setFirstName(SecurityUtils.getAuthenticatedUser().getEmployeeFirstName());
        eventDto.getEmployee().setLastName(SecurityUtils.getAuthenticatedUser().getEmployeeLastName());
        eventService.processManualEvent(eventDto);
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String getPatientDetailsView(@PathVariable("patientId") Long patientId, Model model) {
        System.out.println("getPatientDetailsView start!");
        long startTime = System.currentTimeMillis();
        PatientDto patient = patientFacade.getPatientDetailsDto(patientId);
        long stopTime0 = System.currentTimeMillis();
        System.out.println("patientFacade.getPatientDto:" + (stopTime0 - startTime) + "ms");
        checkPatientDetailsAccess(patient);
        List<Long> mergedResidentIds = mpiService.listMergedResidents(patientId);
        long stopTime1 = System.currentTimeMillis();
        System.out.println("mpiService.listMergedResidents:" + (stopTime1 - stopTime0) + "ms");

        boolean hasAddAffiliatedCtm = false;
        boolean editable = false;
        Set<Long> employeeIds = getEmployeeIdsAvailableForPatient(patientId);
        long stopTime2 = System.currentTimeMillis();
        System.out.println("getEmployeeIdsAvailableForPatient:" + (stopTime2 - stopTime1) + "ms");

        if (CollectionUtils.isNotEmpty(employeeIds)) {
            for (Long employeeId : employeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser()
                        .getEmployeeAuthoritiesMap().get(employeeId);
                Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId)
                        .getCommunityId();
                CareCoordinationResident resident = careCoordinationResidentService.get(patientId);
                editable = editable || careCoordinationResidentService.isResidentEditable(currentEmployeeAuthorities,
                        employeeCommunityId, resident);
                hasAddAffiliatedCtm = hasAddAffiliatedCtm || SecurityUtils.hasAnyRole(currentEmployeeAuthorities,
                        CareTeamRoleCode.ROLES_CAN_ADD_EDIT_AFF_PATIENT_CARE_TEAM_MEMBERS);
            }
        }
        long stopTime3 = System.currentTimeMillis();
        System.out.println("misc:" + (stopTime3 - stopTime2) + "ms");

        patient.setEditable(editable);
        final boolean canAddCtm = CareTeamSecurityUtils.canAddCtm(SecurityUtils.getAuthenticatedUser(), employeeIds,
                patient.getOrganizationId(), patient.getCommunityId());

        final EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setPatientId(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("residentId", patient.getId());
        model.addAttribute("aggregated", Boolean.TRUE);
        model.addAttribute("databaseId", patient.getOrganizationId());
        // workaround for hash key interceptor
        model.addAttribute("hashKey", patient.getHashKey());
        model.addAttribute("documentFilter", new DocumentFilter());
        model.addAttribute("eventFilter", eventFilterDto);
        model.addAttribute("searchScope", SearchScope.ELDERMARK.getCode());
        model.addAttribute("uploadDocumentForm", new UploadDocumentForm());
        model.addAttribute("showMessageCompose", SecurityUtils.isEldermarkUser());
        boolean affiliatedView = SecurityUtils.isAffiliatedView();
        model.addAttribute("affiliatedView", affiliatedView);
        if (!affiliatedView) {
            model.addAttribute("canAddCtm", canAddCtm);
        }
        model.addAttribute("hasAddAffiliatedCtm", hasAddAffiliatedCtm);

        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            model.addAttribute("hasAffiliated", communityCrudService.hasAffiliatedCommunitiesForResident(patientId));
        }
        model.addAttribute("hasMerged", CollectionUtils.isNotEmpty(mergedResidentIds));
        model.addAttribute("assessmentsFilter", new AssessmentsFilterDto());

        addNucleusAttributes(patientId, model);

        return "care.coordination.patient.details";
    }

    private DeviceDto deviceToDto(ResidentDevice device) {
        DeviceDto dto = new DeviceDto();
        dto.setDeviceID(device.getDeviceId());
        dto.setPatientName(device.getResident().getFullName());
        return dto;
    }

    private void addNucleusAttributes(Long patientId, Model model) {
        if (nucleusService.isNucleusIntegrationEnabled()) {
            final Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            final String loggedInEmployeeNucleusUserId = nucleusService.findByEmployeeId(employeeId);
            final String patientNucleusUserId = nucleusService.findByResidentId(patientId);
            if (StringUtils.isNotBlank(loggedInEmployeeNucleusUserId)) {
                model.addAttribute("loggedInEmployeeNucleusUserId", loggedInEmployeeNucleusUserId);
                model.addAttribute("patientNucleusUserId", patientNucleusUserId);
                model.addAttribute("nucleusAuthToken", nucleusService.getNucleusAuthToken());
                model.addAttribute("nucleusHost", nucleusService.getNucleusHost());
            }

            final List<String> testerLogins = Arrays.asList("dweber", "phomal@scnsoft.com");
            final Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
            final String loginName = StringUtils.lowerCase(employee.getLoginName());
            final boolean isCurrentUserTester = testerLogins.contains(loginName);
            model.addAttribute("showTestIncomingCallLink", isCurrentUserTester);
        }
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER, CareTeamRoleCode.PARENT_GUARDIAN,
            CareTeamRoleCode.PERSON_RECEIVING_SERVICES })
    @RequestMapping(value = "/care-team/{careTeamMemberId}/{affiliated}", method = RequestMethod.GET)
    public String getEditCareTeamMemberTemplate(Model model, @PathVariable("patientId") Long patientId,
            @PathVariable(value = "careTeamMemberId") final Long careTeamMemberId,
            @PathVariable("affiliated") Boolean affiliated) {
        return getCareTeamMemberTemplate(model, careTeamMemberId, patientId, affiliated);
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER })
    @RequestMapping(value = "/care-team/{affiliated}", method = RequestMethod.GET)
    public String getCreateCareTeamMemberTemplate(Model model, @PathVariable("patientId") Long patientId,
            @PathVariable("affiliated") Boolean affiliated) {
        return getCareTeamMemberTemplate(model, null, patientId, affiliated);
    }

    private String getCareTeamMemberTemplate(final Model model, final Long careTeamMemberId, Long patientId,
            Boolean affiliated) {
        final CareTeamMemberDto careTeamMemberDto = new CareTeamMemberDto();
        model.addAttribute("careTeamMemberDto", careTeamMemberDto);

        model.addAttribute("notificationTypeList", Arrays.asList(NotificationType.SMS));
        model.addAttribute("responsibility", Responsibility.A);

        final List<KeyValueDto> employees = new ArrayList<KeyValueDto>();
        if (careTeamMemberId == null) {
            Set<Long> employeeIds = getEmployeeIdsAvailableForPatient(patientId);
            employees.add(new KeyValueDto(null, "-- Select Contact --"));
            employees.addAll(contactService.getEmployeeSelectList(null, patientId, affiliated, employeeIds, null));
        } else {
            employees.add(careTeamService.getEmployeeForCareTeamMember(careTeamMemberId));
        }

        final List<CareTeamRoleDto> roles = new ArrayList<CareTeamRoleDto>();
        roles.add(new CareTeamRoleDto(null, "-- Select Role --", null));
        roles.addAll(careCoordinationResidentService.getAllowedCareTeamRoles(careTeamMemberId, patientId));

        model.addAttribute("employees", employees);
        model.addAttribute("roles", roles);

        if (careTeamMemberId != null) {
            careTeamMemberDto.setCanChangeEmployee(false);
            careTeamMemberDto
                    .setIncludeInFaceSheet(careTeamService.getIncludedInFaceSheetForCareTeamMember(careTeamMemberId));
        } else {
            careTeamMemberDto.setCanChangeEmployee(true);
        }

        return "care.coordination.create.careteam.member";
    }

    private Set<Long> getEmployeeIdsAvailableForPatient(Long patientId) {
        return careCoordinationResidentService.getLoggedEmployeeIdsAvailableForPatient(patientId);
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER, CareTeamRoleCode.PARENT_GUARDIAN,
            CareTeamRoleCode.PERSON_RECEIVING_SERVICES })
    @RequestMapping(value = "/care-team", method = RequestMethod.PUT, headers = "Accept=application/json")
    @ResponseBody
    public void savePatientCareTeamMember(@PathVariable("patientId") final Long patientId,
            @RequestBody final CareTeamMemberDto patientCareTeamMemberDto) {
        careCoordinationResidentService
                .checkAddEditCareTeamAccessToPatientOrThrow(patientCareTeamMemberDto.getCareTeamMemberId(), patientId);
        if (patientCareTeamMemberDto.getCareTeamMemberId() == null) {
            if (patientCareTeamMemberDto.getCareTeamRoleSelect() == null) {
                throw new BadRequestException("Please Select Role");
            }
            if (patientCareTeamMemberDto.getCareTeamEmployeeSelect() == null) {
                throw new BadRequestException("Please Select Employee");
            }
        }
        if (CollectionUtils.isEmpty(patientCareTeamMemberDto.getNotificationPreferences())) {
            throw new BadRequestException("Notification preferences is Mandatory");
        }
        careTeamService.createOrUpdateResidentCareTeamMember(patientId, patientCareTeamMemberDto);
    }

    private void checkPatientDetailsAccess(PatientDto patient) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return;
        }
        ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
        // Long patientCommunityId =
        // careCoordinationResidentService.getCommunityId(patient);
        if (SecurityUtils.isAffiliatedView()) {
            Set<Long> affiliatedOrgIds = organizationService.getAffiliatedOrgIds(details.getCurrentDatabaseId());
            if (CollectionUtils.isNotEmpty(affiliatedOrgIds)) {
                for (LinkedContactDto linkedEmployee : details.getLinkedEmployees()) {
                    if (affiliatedOrgIds.contains(linkedEmployee.getDatabaseId())) {
                        Set<GrantedAuthority> currentEmployeeAuthorities = details.getEmployeeAuthoritiesMap()
                                .get(linkedEmployee.getId());
                        if (SecurityUtils.hasRole(currentEmployeeAuthorities,
                                CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                            List<Long> initialOrgIds = careCoordinationCommunityDao.getInitialOrganizationIds(
                                    linkedEmployee.getCommunityId(), details.getCurrentDatabaseId(),
                                    linkedEmployee.getDatabaseId());
                            if (initialOrgIds.contains(patient.getCommunityId()) || initialOrgIds.contains(null)) {
                                return;
                            }
                        }
                        List<Long> initialOrgIds = careCoordinationCommunityDao.getInitialOrganizationIds(
                                details.getCurrentDatabaseId(), linkedEmployee.getDatabaseId());
                        if (initialOrgIds.contains(patient.getCommunityId()) || initialOrgIds.contains(null)) {
                            if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.ADMINISTRATOR)) {
                                return;
                            } else if (careTeamService.checkHasCareTeamMember(details.getEmployeeAndLinkedEmployeeIds(),
                                    patient.getId(), patient.getCommunityId())) {
                                return;
                            }
                        }
                    }
                }
            }
        }
        if (SecurityUtils.hasRole(CareTeamRoleCode.ADMINISTRATOR)) {
            if (details.getCurrentAndLinkedDatabaseIds().contains(patient.getOrganizationId())) {
                return;
            }
        }

        if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
            if (details.getEmployeeAndLinkedEmployeesCommunityIds().contains(patient.getCommunityId())) {
                return;
            }
            if (careTeamService.checkHasCareTeamMember(details.getEmployeeAndLinkedEmployeeIds(), patient.getId(),
                    patient.getCommunityId())) {
                return;
            }
        }
        if (!SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_ALL_ADMINISTRATORS)) {
            if (careTeamService.checkHasCareTeamMember(details.getEmployeeAndLinkedEmployeeIds(), patient.getId(),
                    patient.getCommunityId())) {
                return;
            }
        }
        if (details.getEmployeeAndLinkedEmployeeIds()
                .contains(careCoordinationResidentService.getCreatedById(patient.getId()))) {
            return;
        }
        throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
    }

    // --
    @RequestMapping(value = "/care-team/{affiliated}", method = RequestMethod.POST)
    @ResponseBody
    public Page<CareTeamMemberListItemDto> getPatientCareTeam(@PathVariable("patientId") Long patientId,
            @PathVariable("affiliated") Boolean affiliated, final Pageable pageable) {
        final Page<CareTeamMemberListItemDto> result = careTeamService.getPatientCareTeam(patientId, affiliated,
                pageable);
        return result;
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER, CareTeamRoleCode.PARENT_GUARDIAN,
            CareTeamRoleCode.PERSON_RECEIVING_SERVICES })

    @RequestMapping(value = "/care-team/notification-preferences", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificationPreferencesGroupDto> getNotificationPreferences(
            @RequestParam(value = "careTeamRoleId", required = false) Long careTeamRoleId,
            @RequestParam(value = "careTeamMemberId", required = false) Long careTeamMemberId,
            @RequestParam(value = "employeeId", required = false) Long employeeId) {
        return careTeamService.getAvailableNotificationPreferences(careTeamRoleId, careTeamMemberId, employeeId);
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER, CareTeamRoleCode.PARENT_GUARDIAN,
            CareTeamRoleCode.PERSON_RECEIVING_SERVICES })
    @RequestMapping(value = "/care-team/{careTeamMemberId}/delete", method = RequestMethod.GET)
    public String getDeleteCareTeamMemberTemplate(@PathVariable("careTeamMemberId") Long careTeamMemberId) {
        // Security is checked in deleteCareTeamMember. There is no need to do this 2
        // times.
        return "care.coordination.delete.careteam.member";
    }

    @Secured(value = { CareTeamRoleCode.CASE_MANAGER, CareTeamRoleCode.CARE_COORDINATOR,
            CareTeamRoleCode.SERVICE_PROVIDER, CareTeamRoleCode.PARENT_GUARDIAN,
            CareTeamRoleCode.PERSON_RECEIVING_SERVICES })
    @RequestMapping(value = "/care-team/{careTeamMemberId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteCareTeamMember(@PathVariable("patientId") Long patientId,
            @PathVariable("careTeamMemberId") Long careTeamMemberId) {
        careCoordinationResidentService.checkAddEditCareTeamAccessToPatientOrThrow(careTeamMemberId, patientId);

        careTeamService.deleteResidentCareTeamMember(careTeamMemberId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String editPatientTemplate(@PathVariable("patientId") Long patientId, Model model) {
        String[] rolesToCheck = null;
        if (patientId == null || patientId == 0L) {
            rolesToCheck = CareTeamRoleCode.ROLES_CAN_ADD_PATIENT;
        } else {
            rolesToCheck = CareTeamRoleCode.ROLES_CAN_EDIT_PATIENTS;
        }

        final Set<KeyValueDto> availableCommunities = getAvailableCommunities(rolesToCheck);
        final Set<KeyValueDto> availableOrganizations = getAvailableOrganizations();

        if (patientId == null || patientId == 0L) {
            SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_PATIENT);
            PatientDto dto = new PatientDto();

            dto.setOrganizationId(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId());
            dto.setOrganization(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseName());
            dto.setIntakeDate(new Date());

            if (availableCommunities.size() == 1) {
                for (KeyValueDto comm : availableCommunities) {
                    dto.setCommunityId(comm.getId());
                    dto.setCommunity(comm.getLabel());
                }
            }

            if (SecurityUtils.hasAnyRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
                for (KeyValueDto org : availableOrganizations) {
                    if (org.getId().equals(0L)) {
                        dto.setOrganizationId(org.getId());
                        dto.setOrganization(org.getLabel());
                    }
                }
                for (KeyValueDto comm : availableCommunities) {
                    if (comm.getId() != null && comm.getId().equals(0L)) {
                        dto.setCommunityId(comm.getId());
                        dto.setCommunity(comm.getLabel());
                    }
                }
            }
            model.addAttribute("patientDto", dto);
        } else {
            SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_EDIT_PATIENTS);
            Set<Long> allEmployeeIdsForDatabase = SecurityUtils.getAuthenticatedUser()
                    .getEmployeeIdsForCurrentDatabase();
            PatientDto dto = patientFacade.getEditPatientDto(patientId);
            // in case when there is no linked accounts, community cannot be changed while
            // editing patient
            if (CollectionUtils.isNotEmpty(allEmployeeIdsForDatabase) && allEmployeeIdsForDatabase.size() == 1) {
                availableCommunities.clear();
            }
            if (dto.getCommunityId() != null) {
                availableCommunities.add(new KeyValueDto(dto.getCommunityId(), dto.getCommunity()));
            }
            model.addAttribute("patientDto", dto);
        }

        List<KeyValueDto> availableCommunitiesList = new ArrayList<KeyValueDto>(availableCommunities);
        Collections.sort(availableCommunitiesList, keyValueDtoComparator());

        List<KeyValueDto> availableOrganizationsList = new ArrayList<KeyValueDto>(availableOrganizations);
        Collections.sort(availableOrganizationsList, keyValueDtoComparator());

        model.addAttribute("communities", availableCommunitiesList);
        model.addAttribute("organizations", availableOrganizationsList);
        return "care.coordination.patient.edit";
    }

    private Set<KeyValueDto> getAvailableOrganizations() {
        Long currentId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        List<Pair<Long, String>> orgsSource = organizationService.listBrief();
        KeyValueDto select = new KeyValueDto();
        select.setId(0L);
        select.setLabel("-- Select --");
        final Set<KeyValueDto> availableOrganizations = new HashSet<KeyValueDto>();
        availableOrganizations.add(select);
        for (Pair<Long, String> dto : orgsSource) {
            availableOrganizations.add(new KeyValueDto(dto.getFirst(), dto.getSecond()));
        }
        return availableOrganizations;
    }

    private Set<KeyValueDto> getAvailableCommunities(String[] rolesToCheck) {
        final Set<KeyValueDto> availableCommunities = new HashSet<KeyValueDto>();
        KeyValueDto select = new KeyValueDto();
        select.setId(0L);
        select.setLabel("-- Select --");

        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            availableCommunities.clear();
            availableCommunities.add(select);
        } else {
            Set<Long> allEmployeeIdsForDatabase = SecurityUtils.getAuthenticatedUser()
                    .getEmployeeIdsForCurrentDatabase();
            for (Long employeeId : allEmployeeIdsForDatabase) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser()
                        .getEmployeeAuthoritiesMap().get(employeeId);
                if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, rolesToCheck)) {
                    List<CommunityListItemDto> communitiesSource = communityCrudService.filterListDto().getContent();
                    for (CommunityListItemDto communityListItemDto : communitiesSource) {
                        availableCommunities
                                .add(new KeyValueDto(communityListItemDto.getId(), communityListItemDto.getName()));
                    }
                    if (availableCommunities.size() > 1) {
                        availableCommunities.add(select);
                    }
                }
            }
        }
        return availableCommunities;
    }

    private Comparator<KeyValueDto> keyValueDtoComparator() {
        Comparator<KeyValueDto> comparator = new Comparator<KeyValueDto>() {
            @Override
            public int compare(KeyValueDto o1, KeyValueDto o2) {
                String name1 = o1.getLabel();
                String name2 = o2.getLabel();
                if (name1 != null && name2 != null)
                    return name1.compareToIgnoreCase(name2);
                else if (name1 != null && name2 == null)
                    return -1;
                else if (name1 == null && name2 != null)
                    return 1;
                else
                    return 0;
            }
        };
        return comparator;
    }

    @ExceptionHandler(value = NotUniqueValueException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleEmailAlreadyExistsException(NotUniqueValueException e) {
        return e.getMessage();
    }

    @RequestMapping(value = "/toggle-activation", method = RequestMethod.POST)
    public @ResponseBody Boolean toggleActivation(@PathVariable("patientId") Long patientId) {
        SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_EDIT_PATIENTS);
        return patientFacade.toggleActivation(patientId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Long doEditPatient(@PathVariable("patientId") Long patientId, @ModelAttribute PatientDto dto,
            Model model) {
        if (patientId == null || patientId == 0L) {
            SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_ADD_PATIENT);
        } else {
            SecurityUtils.hasAnyRoleOrThrowException(CareTeamRoleCode.ROLES_CAN_EDIT_PATIENTS);
        }
        Long newId = patientFacade.createOrEditPatient(patientId, dto);
        return newId;
    }

    @RequestMapping(value = "/getMergedPatients/{showDeactivated}", method = RequestMethod.GET)
    @ResponseBody
    public List<PatientListItemDto> getMergedPatients(@PathVariable("patientId") Long patientId,
            @PathVariable("showDeactivated") Boolean showDeactivated) {
        return patientFacade.getMergedResidents(patientId, showDeactivated);
    }

    @ModelAttribute("notificationTypes")
    public NotificationType[] getNotificationTypes() {
        return NotificationType.values();
    }

    @ModelAttribute("responsibilities")
    public Responsibility[] getResponsibilities() {
        return Responsibility.values();
    }

    @ModelAttribute("careTeamRoles")
    public List<KeyValueDto> getCareTeamRoles() {
        List<KeyValueDto> result = new ArrayList<KeyValueDto>();
        result.add(new KeyValueDto(null, "-- Select Role --"));
        result.addAll(careTeamRoleService.getAllCareTeamRoles());
        return result;
    }

    @ModelAttribute("eventTypes")
    public List<KeyValueDto> getEventServices() {
        return eventTypeService.getAllEventTypes();
    }

    @ModelAttribute("states")
    public List<KeyValueDto> getStates() {
        return stateService.getStates();
    }

    @ModelAttribute("insurances")
    public List<KeyValueDto> getInsurances() {
        final List<KeyValueDto> insurances = new ArrayList<>();
        insurances.add(new KeyValueDto(null, "Search by network name"));
        insurances.addAll(inNetworkInsurancesDtoSupplier.getMemoized());
        return insurances;
    }

    @RequestMapping(value = "/insuranceId/{insuranceId}/plans", method = RequestMethod.GET)
    @ResponseBody
    public List<AlphabetableKeyTwoValuesDto> getInsurancePlans(@PathVariable(value = "insuranceId") Long insuranceId) {
        List<AlphabetableKeyTwoValuesDto> plans = new ArrayList<>();
        if (insurancePlansDtoSupplier.getMemoized().containsKey(insuranceId)) {
            plans.addAll(insurancePlansDtoSupplier.getMemoized().get(insuranceId));
        }
        return plans;
    }

    @RequestMapping(value = "/custom/{documentId}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteDocument(@PathVariable(value = "documentId") Long documentId) {
        documentFacade.deleteDocument(documentId);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void uploadDocument(@PathVariable("patientId") Long patientId,
            @ModelAttribute(value = "uploadDocumentForm") final UploadDocumentForm uplForm,
            @AuthenticationPrincipal ExchangeUserDetails userDetails) {

        final CommonsMultipartFile doc = uplForm.getDocument();
        DocumentMetadata documentMetadata = new DocumentMetadata.Builder().setDocumentTitle(doc.getOriginalFilename())
                .setFileName(doc.getOriginalFilename()).setMimeType(doc.getContentType()).build();

        List<Long> idsOfDatabasesToShareWith = new ArrayList<Long>();
        if (SharingOption.MY_COMPANY.equals(uplForm.getSharingOption())) {
            idsOfDatabasesToShareWith.add(userDetails.getCurrentDatabaseId());
        }

        long authorId = userDetails.getEmployeeId();

        documentFacade.saveDocument(documentMetadata, patientId, authorId,
                SharingOption.ALL.equals(uplForm.getSharingOption()), idsOfDatabasesToShareWith,
                new SaveDocumentCallbackImpl() {
                    @Override
                    public void saveToFile(File file) {
                        try {
                            FileCopyUtils.copy(doc.getInputStream(), new FileOutputStream(file));
                        } catch (IOException e) {
                            throw new FileIOException("Failed to save file " + doc.getOriginalFilename(), e);
                        }
                    }
                });
    }

    @RequestMapping(value = "/{reportType}/download", method = RequestMethod.GET)
    public void downloadReport(@PathVariable(value = "patientId") Long patientId,
            @PathVariable(value = "reportType") String reportType,
            @RequestParam(value = "aggregated") Boolean aggregated, HttpServletResponse response) {
        documentFacade.downloadOrViewReport(patientId, reportType, response, false, aggregated);
    }

    @RequestMapping(value = "/custom/{documentId}/download", method = RequestMethod.GET)
    public void downloadDocument(@PathVariable(value = "documentId") Long documentId, HttpServletResponse response) {
        DocumentBean document = documentFacade.findDocument(documentId);
        documentFacade.downloadOrViewCustomDocument(document, response, false);
    }
}
