package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesGroupDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.scnsoft.eldermark.services.carecoordination.CareTeamSecurityUtils.canEditNotificationSettings;

/**
 * @author averazub
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 20-Oct-15.
 */
@Service
public class CareTeamServiceImpl implements CareTeamService {
    @Autowired
    ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    private CareTeamMemberNotificationPreferencesDao careTeamMemberNotificationPreferencesDao;

    @Autowired
    CareTeamMemberDao careTeamMemberDao;

    @Autowired
    EventTypeCareTeamRoleXrefDao eventTypeCareTeamRoleXrefDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    ResidentDao residentDao;

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    AccessRightsService accessRightsService;

    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;

    @Autowired
    CareTeamRoleDao careTeamRoleDao;

    @Autowired
    OrganizationDao organizationDao;

    @Autowired
    private CommunityCrudService communityCrudService;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private NucleusInfoService nucleusInfoService;

    @Autowired
    private ResidentUpdateQueueProducer residentUpdateQueueProducer;


    @Override
    public Page<CareTeamMemberListItemDto> getPatientCareTeam(Long patientId, Boolean affiliated, final Pageable pageable) {

        final List<Long> mergedResidentIds = mpiService.listMergedResidents(patientId);
        final Set<Long> residentIds = new HashSet<Long>(mergedResidentIds.size() + 1);

        residentIds.add(patientId);
        if (CollectionUtils.isNotEmpty(mergedResidentIds)) {
            residentIds.addAll(mergedResidentIds);
        }

        final List<ResidentCareTeamMember> careTeamMembers = residentCareTeamMemberDao.getCareTeamMembers(residentIds, affiliated, pageable);
        final List<CareTeamMemberListItemDto> result = transform(patientId, careTeamMembers);

        return new PageImpl<>(result, pageable, residentCareTeamMemberDao.getCareTeamMembersCount(patientId));
    }

    private List<CareTeamMemberListItemDto> transform(Long patientId, Iterable<ResidentCareTeamMember> careTeamMembers) {
        final Set<Long> employeeIds = careCoordinationResidentService.getLoggedEmployeeIdsAvailableForPatient(patientId);
        final List<CareTeamMemberListItemDto> dtoList = new ArrayList<CareTeamMemberListItemDto>();
        final ExchangeUserDetails authenticatedUser = SecurityUtils.getAuthenticatedUser();

        final String nucleusUserIdForAuthenticatedUser = nucleusInfoService.findByEmployeeId(SecurityUtils.getAuthenticatedUser().getEmployeeId());

        for (ResidentCareTeamMember careTeamMember : careTeamMembers) {
            final boolean isEditable = canEditNotificationSettings(authenticatedUser, employeeIds, patientId, careTeamMember);
            CareTeamMemberListItemDto dto = toCareTeamMemberDto(careTeamMember);
            dto.setEditable(isEditable);
            dto.setDeletable(isEditable);
            if (null != nucleusUserIdForAuthenticatedUser) {
                final String nucleusUserId = nucleusInfoService.findByEmployeeId(careTeamMember.getEmployee().getId());
                // check that a CTM is not the same Nucleus user as the currently logged in Employee
                if (!StringUtils.equalsIgnoreCase(nucleusUserId, nucleusUserIdForAuthenticatedUser)) {
                    dto.setNucleusUserId(nucleusUserId);
                }
            }
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public Page<CareTeamMemberListItemDto> getCommunityCareTeam(final Long organizationId, Boolean affiliated, final Pageable pageable) {
        final List<CareTeamMemberListItemDto> result = new ArrayList<CareTeamMemberListItemDto>();
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
//        Long currentDatabaseId = userDetails.getDatabaseId();
//        boolean affiliatedView = false;
//        if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) &&
//                userDetails.getEmployee().getDatabaseId() != currentDatabaseId) {
//            affiliatedView = true;
//        }
        Set<Long> allEmployeeIds = userDetails.getEmployeeAndLinkedEmployeeIds();
        Set<Long> employeeIds = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(allEmployeeIds)) {
            for (Long employeeId : allEmployeeIds) {
                List<Long> communityIds = communityCrudService.getUserCommunityIds(false, employeeId, false);
                if (communityIds == null || communityIds.contains(organizationId)) {
                    employeeIds.add(employeeId);
                }
            }
        }
        boolean affiliatedView = SecurityUtils.isAffiliatedView();
        boolean canAddEditCareTeam = false;
        boolean canAddEditSelfForCareTeam = false;

        final String nucleusUserIdForAuthenticatedUser = nucleusInfoService.findByEmployeeId(userDetails.getEmployeeId());

        Set<Long> selfEditEmployeeIds = new HashSet<Long>();
        for (Long employeeId : employeeIds) {
            Set<GrantedAuthority> currentEmployeeAuthorities = userDetails.getEmployeeAuthoritiesMap().get(employeeId);
            if (!affiliatedView && (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS))) {
                canAddEditCareTeam = true;
            }
            boolean canEditSelf = SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_EDIT_SELF_CARE_TEAM_MEMBERS);
            canAddEditSelfForCareTeam = canAddEditSelfForCareTeam || canEditSelf;
            if (canEditSelf) {
                selfEditEmployeeIds.add(employeeId);
            }
        }
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            canAddEditCareTeam = true;
            canAddEditSelfForCareTeam = true;
        }

        for (OrganizationCareTeamMember careTeamMember : organizationCareTeamMemberDao.getOrganizationCareTeamMembers(organizationId, affiliated, userDetails.getCurrentDatabaseId(), pageable)) {
            CareTeamMemberListItemDto item = toCareTeamMemberDto(careTeamMember);
            result.add(item);
            if (affiliatedView) {
                canAddEditCareTeam = false;
                for (Long employeeId : employeeIds) {
                    Set<GrantedAuthority> currentEmployeeAuthorities = userDetails.getEmployeeAuthoritiesMap().get(employeeId);
                    Long employeeCommunityId = userDetails.getLinkedEmployeeById(employeeId).getCommunityId();
                    Long employeeDatabaseId = userDetails.getLinkedEmployeeById(employeeId).getDatabaseId();
                    if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                        canAddEditCareTeam = canAddEditCareTeam || careTeamMember.getEmployee().getCommunityId() != null &&
                                careTeamMember.getEmployee().getCommunityId().equals(employeeCommunityId);
                    } else if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS)) {
                        canAddEditCareTeam = canAddEditCareTeam || (careTeamMember.getEmployee().getDatabaseId() == employeeDatabaseId);
                    }
                }
            }

            if (canAddEditCareTeam) {
                item.setEditable(true);
                item.setRoleEditable(true);
            } else if (!affiliatedView && !item.isEditable() && CollectionUtils.isNotEmpty(employeeIds)) {
                for (Long employeeId : employeeIds) {
                    Set<GrantedAuthority> currentEmployeeAuthorities = userDetails.getEmployeeAuthoritiesMap().get(employeeId);
                    Long employeeCommunityId = userDetails.getLinkedEmployeeById(employeeId).getCommunityId();
                    if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                        if (employeeCommunityId.equals(careTeamMember.getEmployee().getCommunityId())) {
                            item.setEditable(true);
                            item.setRoleEditable(true);
                        }
                    }
                }
            }
            if (!item.isEditable() && canAddEditSelfForCareTeam) {
                item.setEditable(selfEditEmployeeIds.contains(item.getEmployee().getId()));
                item.setRoleEditable(false);
            }
            item.setDeletable(item.isEditable());

            if (null != nucleusUserIdForAuthenticatedUser) {
                final String nucleusUserId = nucleusInfoService.findByEmployeeId(careTeamMember.getEmployee().getId());
                // check that a CTM is not the same Nucleus user as the currently logged in Employee
                if (!StringUtils.equalsIgnoreCase(nucleusUserId, nucleusUserIdForAuthenticatedUser)) {
                    item.setNucleusUserId(nucleusUserId);
                }
            }
        }
        return new PageImpl<>(result, pageable, organizationCareTeamMemberDao.getOrganizationCareTeamMembersCount(organizationId));
    }

    @Override
    public List<CareTeamMember> getCareTeamMembersForPatient(final CareCoordinationResident resident) {
        final List<CareTeamMember> careTeamMembers = new ArrayList<CareTeamMember>();
        careTeamMembers.addAll(residentCareTeamMemberDao.getCareTeamMembers(resident.getId()));

        if (resident.getFacility() != null) {
            careTeamMembers.addAll(organizationCareTeamMemberDao.getOrganizationCareTeamMembers(resident.getFacility().getId(), null, null, null));
        }
        return careTeamMembers;

    }

//    @Override
//    public List<CareTeamMember> getCareTeamMembersAvailableToReceiveEventNotificationsForPatient(CareCoordinationResident resident, Set<Long> mergedResidentIds) {
//        final List<CareTeamMember> careTeamMembers = getCareTeamMembersExcludingDuplicates(resident, mergedResidentIds);
//
//        if (resident.getFacility() != null) {
//            careTeamMembers.addAll(organizationCareTeamMemberDao.getOrganizationCareTeamMembersExcludeInactive(resident.getFacility().getId()));
//        }
//        return careTeamMembers;
//
//    }

    @Override
    public List<CareTeamMember> getCareTeamMembersAvailableToReceiveEventNotificationsForPatient(CareCoordinationResident resident, Set<Long> mergedResidentIds) {
        final List<CareTeamMember> careTeamMembers = new ArrayList<CareTeamMember>(residentCareTeamMemberDao.getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(mergedResidentIds,
                accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)));

        final List<CareTeamMember> affiliatedCareTeamMembers = new ArrayList<CareTeamMember>(residentCareTeamMemberDao.getAffiliatedCareTeamMembersWithAccessRightCheckExcludeInactive(mergedResidentIds,
                accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)));

        final List<CareTeamMember> organizationCareTeamMembers = new ArrayList<>();
        if (resident.getFacility() != null) {
            organizationCareTeamMembers.addAll(organizationCareTeamMemberDao.getOrganizationCareTeamMembersExcludeInactive(resident.getFacility().getId()));
        }

        final Map<Long, CareTeamMember> allCareTeamMembers = new HashMap<>();
        addNotExisting(allCareTeamMembers, careTeamMembers);
        addNotExisting(allCareTeamMembers, affiliatedCareTeamMembers);
        addNotExisting(allCareTeamMembers, organizationCareTeamMembers);
        return new ArrayList<>(allCareTeamMembers.values());
    }

    private void addNotExisting(Map<Long, CareTeamMember> allCareTeamMembers, List<CareTeamMember> affiliatedCareTeamMembers) {
        if (CollectionUtils.isNotEmpty(affiliatedCareTeamMembers)) {
            for (CareTeamMember member : affiliatedCareTeamMembers) {
                if (!allCareTeamMembers.containsKey(member.getEmployee().getId())) {
                    allCareTeamMembers.put(member.getEmployee().getId(), member);
                }
            }
        }
    }

    private NotificationPreferencesDto searchPreference(Long eventTypeId, List<NotificationPreferencesGroupDto> result) {
        for (NotificationPreferencesGroupDto groupDto : result)
            for (NotificationPreferencesDto preferencesDto : groupDto.getNotificationPreferences()) {
                if (preferencesDto.getEventTypeId() == eventTypeId) {
                    return preferencesDto;
                }
            }
        return new NotificationPreferencesDto();
    }

    @Override
    public List<NotificationPreferencesGroupDto> getAvailableNotificationPreferences(final Long careTeamRoleId, final Long careTeamMemberId, final Long employeeId) {
        final List<NotificationPreferencesGroupDto> result = new ArrayList<>();
        //final List<NotificationPreferencesDto> preferencesDtoList = new ArrayList<NotificationPreferencesDto>();
        if (careTeamRoleId == null) {
            return result;
        }
        CareTeamMember careTeamMember = null;

        if (careTeamMemberId != null) {
            careTeamMember = careTeamMemberDao.get(careTeamMemberId);
        }
        for (EventTypeCareTeamRoleXref xref : eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)) {
            final NotificationPreferencesDto dto = searchPreference(xref.getEventType().getId(), result);
            boolean newDto = (dto.getEventType() == null);
            dto.setEventTypeId(xref.getEventType().getId());
            dto.setEventType(xref.getEventType().getDescription());
            dto.setResponsibility(xref.getResponsibility());
            dto.setCanChange(xref.getResponsibility().isChangeable());

            if (careTeamMember != null) {
                boolean preferenceExists = false;
                for (CareTeamMemberNotificationPreferences np : careTeamMember.getCareTeamMemberNotificationPreferencesList()) {
                    if (np.getEventType().equals(xref.getEventType())) {
                        preferenceExists = true;
                        dto.getNotificationTypeList().add(np.getNotificationType());
                        if (xref.getResponsibility().isChangeable() && np.getResponsibility().isAssignable() && careTeamRoleId.equals(careTeamMember.getCareTeamRole().getId())) {
                            dto.setResponsibility(np.getResponsibility());
                        } else {
                            dto.setResponsibility(xref.getResponsibility());
                        }
                        dto.setId(np.getId());
                    }
                }
                if (!preferenceExists) {
                    dto.setResponsibility(Responsibility.N);
                }
                dto.checkSetAllForLoad();
            } else {
                dto.getNotificationTypeList().add(NotificationType.EMAIL);
                dto.getNotificationTypeList().add(NotificationType.PUSH_NOTIFICATION);
            }
            if (newDto) {  //check if it is already added to result array
                addToGroup(result, dto, xref);
            }
//            preferencesDtoList.add(dto);
        }

        Collections.sort(result, new Comparator<NotificationPreferencesGroupDto>() {
            public int compare(NotificationPreferencesGroupDto n1, NotificationPreferencesGroupDto n2) {
                return n1.getPriority() - n2.getPriority();
            }
        });

        return result;
    }

    private void addToGroup(List<NotificationPreferencesGroupDto> result, NotificationPreferencesDto dto, EventTypeCareTeamRoleXref xref) {
        EventGroup eventGroup = xref.getEventType().getEventGroup();
        for (NotificationPreferencesGroupDto groupDto : result) {
            if (groupDto.getPriority().equals(eventGroup.getPriority())) {
                groupDto.getNotificationPreferences().add(dto);
                return;
            }
        }
        NotificationPreferencesGroupDto groupDto = new NotificationPreferencesGroupDto();
        groupDto.setName(eventGroup.getName());
        groupDto.setPriority(eventGroup.getPriority());
        groupDto.getNotificationPreferences().add(dto);
        result.add(groupDto);
    }


//    @Override
//    public List<NotificationPreferencesDto> getAvailableNotificationPreferences(final Long careTeamRoleId, final Long careTeamMemberId, final Long employeeId) {
//        final List<NotificationPreferencesDto> result = new ArrayList<NotificationPreferencesDto>();
//        if (careTeamRoleId == null ){
//            return result;
//        }
//        CareTeamMember careTeamMember = null;
//
//        if (careTeamMemberId != null) {
//            careTeamMember = careTeamMemberDao.get(careTeamMemberId);
//        }
//
//        List<EventTypeCareTeamRoleXref> eventTypeCareTeamRoleXrefList = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId);
//
//        if (careTeamMember != null) {
//        for (CareTeamMemberNotificationPreferences np : careTeamMember.getCareTeamMemberNotificationPreferencesList()) {
////            final NotificationPreferencesDto dto = searchPreference(np.getEventType().getId(),result);
//
//            boolean found = false;
//            NotificationPreferencesDto dto = null;
//            for (NotificationPreferencesDto preferencesDto:result){
//                if (preferencesDto.getEventTypeId() == np.getEventType().getId()) {
//                    dto = preferencesDto;
//                    found = true;
//                    break;
//                }
//            }
//            if (found) {
//                for (EventTypeCareTeamRoleXref xref : eventTypeCareTeamRoleXrefList) {
//                    if (np.getEventType().equals(xref.getEventType())) {
//                        dto.getNotificationTypeList().add(np.getNotificationType());
////                        dto.addNotificationType(np);
//                        break;
//                    }
//                }
//            }
//            else {
//                dto = new NotificationPreferencesDto();
//                dto.setEventTypeId(np.getEventType().getId());
//                dto.setEventType(np.getEventType().getDescription());
//                dto.setResponsibility(np.getResponsibility());
//                dto.setCanChange(np.getResponsibility().isChangeable());
//
//
//                    for (EventTypeCareTeamRoleXref xref : eventTypeCareTeamRoleXrefList) {
//                        if (np.getEventType().equals(xref.getEventType())) {
//                            dto.getNotificationTypeList().add(np.getNotificationType());
////                            dto.addNotificationType(np);
//                            dto.setCanChange(xref.getResponsibility().isChangeable());
//
//                            if (xref.getResponsibility().isChangeable() && np.getResponsibility().isAssignable() && careTeamRoleId.equals(careTeamMember.getCareTeamRole().getId())) {
//                                dto.setResponsibility(np.getResponsibility());
//                            } else {
//                                dto.setResponsibility(xref.getResponsibility());
//                            }
//                            dto.setId(np.getId());
//                            break;
//                        }
//                    }
//                result.add(dto);
//                }
//
//            }
//        }
//        else {
//            for (EventTypeCareTeamRoleXref xref : eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)) {  //TODO optimize
//                final NotificationPreferencesDto dto = new NotificationPreferencesDto();
//
//                dto.setEventTypeId(xref.getEventType().getId());
//                dto.setEventType(xref.getEventType().getDescription());
//                dto.setResponsibility(xref.getResponsibility());
//                dto.setCanChange(xref.getResponsibility().isChangeable());
//                result.add(dto);
//            }
//        }
//        return result;
//    }

    @Override
    public void deleteResidentCareTeamMember(final long careTeamMemberId) {
        Long residentId = residentCareTeamMemberDao.get(careTeamMemberId).getResidentId();

        residentCareTeamMemberDao.delete(careTeamMemberId);

        residentUpdateQueueProducer.putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
    }

    @Override
    public void createOrUpdateResidentCareTeamMember(final Long residentId, final CareTeamMemberDto patientCareTeamMemberDto) {
        ResidentCareTeamMember careTeamMember;
        boolean isNew = false;
        if (patientCareTeamMemberDto.getCareTeamMemberId() != null) {
            careTeamMember = residentCareTeamMemberDao.get(patientCareTeamMemberDto.getCareTeamMemberId());
        } else {
            isNew = true;
            careTeamMember = new ResidentCareTeamMember();
            careTeamMember.setEmployee(employeeDao.getEmployee(patientCareTeamMemberDto.getCareTeamEmployeeSelect()));
            careTeamMember.setResident(residentDao.get(residentId));
            careTeamMember.setResidentId(residentId);
            careTeamMember.setCareTeamRole(careTeamRoleDao.findOne(patientCareTeamMemberDto.getCareTeamRoleSelect()));
            careTeamMember.setAccessRights(accessRightsService.getDefaultAccessRights());
        }
        validateCareTeamMember(careTeamMember, patientCareTeamMemberDto);

        final List<ResidentCareTeamMember> existing = residentCareTeamMemberDao
                .getResidentCareTeamMembersByEmployeeAndRole(careTeamMember.getResidentId(),
                        careTeamMember.getEmployee().getId(),
                        patientCareTeamMemberDto.getCareTeamRoleSelect() != null ?
                                patientCareTeamMemberDto.getCareTeamRoleSelect() : careTeamMember.getCareTeamRole().getId());

        if (CollectionUtils.isNotEmpty(existing)) {
            for (ResidentCareTeamMember member : existing) {
                if (careTeamMember.getId() == null || (careTeamMember.getId() != null && !member.getId().equals(careTeamMember.getId()))) {
                    throw new BusinessException("Patient Care Team Member with selected role already exists. Please check entered data");
                }
            }
        }
        // TODO update permissions check : can user edit care team member's role?
        //final Set<Long> employeeIds = careCoordinationResidentService.getLoggedEmployeeIdsAvailableForPatient(residentId);
        //final boolean canEditCtmRole = canEditCtmRole(SecurityUtils.getAuthenticatedUser(), employeeIds, residentId, careTeamMember);
        if (patientCareTeamMemberDto.getCareTeamRoleSelect() != null &&
                (isNew || !careTeamMember.getCareTeamRole().getId().equals(patientCareTeamMemberDto.getCareTeamRoleSelect()))) {
            careTeamMember.setCareTeamRole(careTeamRoleDao.findOne(patientCareTeamMemberDto.getCareTeamRoleSelect()));
        }
        careTeamMember.setDescription(patientCareTeamMemberDto.getCareTeamDescription());
        careTeamMember.setIncludeInFaceSheet(patientCareTeamMemberDto.getIncludeInFaceSheet());
        final List<EventTypeCareTeamRoleXref> defaultSettings = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamMember.getCareTeamRole().getId());
        if (isNew) {
            careTeamMember = residentCareTeamMemberDao.create(careTeamMember);
            careTeamMember.setCreatedById(SecurityUtils.getAuthenticatedUser().getEmployeeId());

        } else {
            careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(careTeamMember.getId());
        }
        createCareTeamMemberNotificationPreferences(careTeamMember, patientCareTeamMemberDto, defaultSettings);
        residentCareTeamMemberDao.merge(careTeamMember);

        residentCareTeamMemberDao.flush();

        residentUpdateQueueProducer.putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
    }

    @Override
    public void createOrUpdateCommunityCareTeamMember(Long communityId, CareTeamMemberDto careTeamMemberDto, boolean canEditSelf, boolean createdAutomatically) {
        OrganizationCareTeamMember careTeamMember;
        boolean isNew = false;
        if (careTeamMemberDto.getCareTeamMemberId() != null) {
            careTeamMember = organizationCareTeamMemberDao.get(careTeamMemberDto.getCareTeamMemberId());
        } else {
            isNew = true;
            careTeamMember = new OrganizationCareTeamMember();
            careTeamMember.setEmployee(employeeDao.getEmployee(careTeamMemberDto.getCareTeamEmployeeSelect()));
            careTeamMember.setOrganization(organizationDao.get(communityId));
        }
        if (!canEditSelf && (isNew || careTeamMember.getCareTeamRole().getId() != careTeamMemberDto.getCareTeamRoleSelect())) {
            careTeamMember.setCareTeamRole(careTeamRoleDao.findOne(careTeamMemberDto.getCareTeamRoleSelect()));
        }

        validateCareTeamMember(careTeamMember, careTeamMemberDto);
        careTeamMember.setDescription(careTeamMemberDto.getCareTeamDescription());

        final List<OrganizationCareTeamMember> existing = organizationCareTeamMemberDao.getOrganizationCareTeamMembersByEmployeeAndRole(careTeamMember.getOrganization().getId(), careTeamMember.getEmployee().getId(), careTeamMemberDto.getCareTeamRoleSelect() != null ? careTeamMemberDto.getCareTeamRoleSelect() : careTeamMember.getCareTeamRole().getId());

        if (CollectionUtils.isNotEmpty(existing)) {
            for (OrganizationCareTeamMember member : existing) {
                if (careTeamMember.getId() == null || (careTeamMember.getId() != null && !member.getId().equals(careTeamMember.getId()))) {
                    throw new BusinessException("Community Care Team Member with selected role already exists. Please check entered data");
                }
            }
        }

        final List<EventTypeCareTeamRoleXref> defaultSettings = eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamMember.getCareTeamRole().getId());

        if (isNew) {
            careTeamMember = organizationCareTeamMemberDao.create(careTeamMember);
            if (!createdAutomatically) {
                careTeamMember.setCreatedById(SecurityUtils.getAuthenticatedUser().getEmployeeId());
            }
        } else {
            careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(careTeamMember.getId());
//            updateCareTeamMemberNotificationPreferences(careTeamMember, careTeamMemberDto, defaultSettings);
        }
        createCareTeamMemberNotificationPreferences(careTeamMember, careTeamMemberDto, defaultSettings);
        organizationCareTeamMemberDao.merge(careTeamMember);

        organizationCareTeamMemberDao.flush();
    }

    private void validateCareTeamMember(CareTeamMember careTeamMember, CareTeamMemberDto careTeamMemberDto) {
        for (NotificationPreferencesDto preferencesDto : careTeamMemberDto.getNotificationPreferences()) {
            for (NotificationType notificationType : preferencesDto.getNotificationTypeList()) {
                Person person = careTeamMember.getEmployee().getPerson();
                if (notificationType.equals(NotificationType.FAX) || notificationType.equals(NotificationType.ALL)) {
                    String fax = PersonService.getPersonTelecomValue(person, PersonTelecomCode.FAX);
                    if (StringUtils.isEmpty(fax)) {
                        throw new BusinessException("This user has no fax. Please select other notification type or add fax number for this user.");
                    }
                } else if (notificationType.equals(NotificationType.EMAIL) || notificationType.equals(NotificationType.ALL)) {
                    String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
                    if (StringUtils.isEmpty(email)) {
                        throw new BusinessException("This user has no email. Please select other notification type or add email for this user.");
                    }
                } else if (notificationType.equals(NotificationType.SMS) || notificationType.equals(NotificationType.ALL)) {
                    String phone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.WP);
                    if (StringUtils.isEmpty(phone)) {
                        throw new BusinessException("This user has no phone. Please select other notification type or add phone number for this user.");
                    }
                } else if (notificationType.equals(NotificationType.SECURITY_MESSAGE) || notificationType.equals(NotificationType.ALL)) {
                    if (StringUtils.isEmpty(careTeamMember.getEmployee().getSecureMessaging())) {
                        throw new BusinessException("This user has no secure email. Please select other notification type or add secure email for this user.");
                    }
                }
            }
        }
    }

    @Override
    public void deleteCommunityCareTeamMember(final long careTeamMemberId) {
        organizationCareTeamMemberDao.delete(careTeamMemberId);
    }

    @Override
    public Long getEmployeeIdForCareTeamMember(long careTeamMemberId) {
        return careTeamMemberDao.getEmployeeId(careTeamMemberId);
    }

    @Override
    public KeyValueDto getEmployeeForCareTeamMember(long careTeamMemberId) {
        Employee employee = careTeamMemberDao.getEmployee(careTeamMemberId);
        return new KeyValueDto(employee.getId(), employee.getFullName());
    }

//    @Override
//    public Long getEmployeeIdForPatientCareTeamMember(long careTeamMemberId) {
//        try {
//            return residentCareTeamMemberDao.get(careTeamMemberId).getEmployee().getId(); //TODO optimize
//        }catch(Exception e){
//            // TODO logger.
//            return null;
//        }
//    }
//
//    @Override
//    public KeyValueDto getEmployeeForPatientCareTeamMember(long careTeamMemberId) {
//        try {
//            Employee employee =  residentCareTeamMemberDao.get(careTeamMemberId).getEmployee(); //TODO optimize
//            return new KeyValueDto(employee.getId(),employee.getFullName());
//        }catch(Exception e){
//            // TODO logger.
//            return null;
//        }
//    }

    protected void createCareTeamMemberNotificationPreferences(final CareTeamMember careTeamMember, final CareTeamMemberDto patientCareTeamMemberDto, final List<EventTypeCareTeamRoleXref> defaultSettings) {
        careTeamMember.setCareTeamMemberNotificationPreferencesList(new ArrayList<CareTeamMemberNotificationPreferences>());
        for (NotificationPreferencesDto np : patientCareTeamMemberDto.getNotificationPreferences()) {
            if (np.getResponsibility() == null) {
                setDefaultResponsibilityToDto(np, defaultSettings);
            }
            np.checkSetAllForSave();
            for (NotificationType notificationType : np.getNotificationTypeList()) {
                createCareTeamMemberNotificationPreference(careTeamMember, np, defaultSettings, notificationType);
            }
        }

    }

    private void setDefaultResponsibilityToDto(NotificationPreferencesDto np, List<EventTypeCareTeamRoleXref> defaultSettings) {
        final EventTypeCareTeamRoleXref xref = getDefaultSettings(np.getEventTypeId(), defaultSettings);
        if (xref != null) {
            np.setResponsibility(xref.getResponsibility());
        }
    }

    private void createCareTeamMemberNotificationPreference(final CareTeamMember careTeamMember, NotificationPreferencesDto np, final List<EventTypeCareTeamRoleXref> defaultSettings, NotificationType notificationType) {
        final CareTeamMemberNotificationPreferences careTeamMemberNotificationPreferences = new CareTeamMemberNotificationPreferences();

        final EventTypeCareTeamRoleXref xref = getDefaultSettings(np.getEventTypeId(), defaultSettings);

        setDefaultResponsibility(np, careTeamMemberNotificationPreferences, xref);
        if (xref != null) {
            careTeamMemberNotificationPreferences.setEventType(xref.getEventType());
        } else {
            careTeamMemberNotificationPreferences.setEventType(eventTypeService.get(np.getEventTypeId()));
        }
        careTeamMemberNotificationPreferences.setNotificationType(notificationType);
        careTeamMemberNotificationPreferences.setCareTeamMember(careTeamMember);
        careTeamMember.getCareTeamMemberNotificationPreferencesList().add(careTeamMemberNotificationPreferences);
    }

//    protected void updateCareTeamMemberNotificationPreferences(final CareTeamMember careTeamMember, final CareTeamMemberDto patientCareTeamMemberDto, final List<EventTypeCareTeamRoleXref> defaultSettings) {
//        for (NotificationPreferencesDto np : patientCareTeamMemberDto.getNotificationPreferences()) {
//            if (np.getId()==null) {
//                createCareTeamMemberNotificationPreference(careTeamMember,np,defaultSettings);
//            }
//            else {
//                for (CareTeamMemberNotificationPreferences existing : careTeamMember.getCareTeamMemberNotificationPreferencesList()) {
//                    if (existing.getId().equals(np.getId())) {
//                        existing.setNotificationType(np.getNotificationType());
//                        final EventTypeCareTeamRoleXref xref = getDefaultSettings(np.getEventTypeId(), defaultSettings);
//                        setDefaultResponsibility(np, existing, xref);
//                        break;
//                    }
//                }
//            }
//        }
//    }

    private void setDefaultResponsibility(final NotificationPreferencesDto dto, final CareTeamMemberNotificationPreferences preferences, final EventTypeCareTeamRoleXref defaultPreferences) {
        if (!defaultPreferences.getResponsibility().isChangeable() || dto.getResponsibility() == null) {
            preferences.setResponsibility(defaultPreferences.getResponsibility());
        } else {
            preferences.setResponsibility(dto.getResponsibility());
        }
    }


    private static CareTeamMemberListItemDto toCareTeamMemberDto(final CareTeamMember careTeamMember) {
        final CareTeamMemberListItemDto result = new CareTeamMemberListItemDto();

        result.setId(careTeamMember.getId());
        result.setEmployee(new KeyValueDto(careTeamMember.getEmployee().getId(), careTeamMember.getEmployee().getFullName()));
        result.setRole(new KeyValueDto(careTeamMember.getCareTeamRole().getId(), careTeamMember.getCareTeamRole().getName()));
        result.setDescription(careTeamMember.getDescription());
        result.setCreatedAutomatically(careTeamMember.getEmployee().getCreatedAutomatically());
        result.setEmployeeDatabaseId(careTeamMember.getEmployee().getDatabaseId());
        result.setEmployeeDatabaseName(careTeamMember.getEmployee().getDatabase().getName());
        if (careTeamMember instanceof ResidentCareTeamMember) {
            result.setResidentDatabaseId(((ResidentCareTeamMember) careTeamMember).getResident().getDatabaseId());
            result.setResidentDatabaseName(((ResidentCareTeamMember) careTeamMember).getResident().getDatabase().getName());
            result.setResidentId(((ResidentCareTeamMember) careTeamMember).getResidentId());
        }

        return result;
    }

    private EventTypeCareTeamRoleXref getDefaultSettings(Long eventTypeId, List<EventTypeCareTeamRoleXref> defaultSettings) {
        for (EventTypeCareTeamRoleXref xref : defaultSettings) {
            if (xref.getEventType().getId().equals(eventTypeId)) {
                return xref;
            }
        }
        return null;
    }

    @Override
    public CareTeamMember getCareTeamMember(Long id) {
        return careTeamMemberDao.get(id);
    }

    @Override
    public boolean checkHasCareTeamMember(Set<Long> employeeIds, Long residentId, Long organizationId) {
        return residentCareTeamMemberDao.checkHasResidentCareTeamMember(employeeIds, residentId) ||
                organizationCareTeamMemberDao.checkHasOrganizationCareTeamMember(employeeIds, organizationId);
    }

    @Override
    public void deleteCareTeamMembersAssociatedWithDeletedAffiliatedRelation() {
        final List<Long> orgCtmIds = organizationCareTeamMemberDao.getOrganizationCareTeamMemberIdsFromDeletedAffiliatedRelation();
        final List<Long> residentCtmIds = residentCareTeamMemberDao.getResidentCareTeamMemberIdsFromDeletedAffiliatedRelation();
        if (CollectionUtils.isNotEmpty(orgCtmIds)) {
            careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(orgCtmIds);
            organizationCareTeamMemberDao.deleteByIdIn(orgCtmIds);
        }
        if (CollectionUtils.isNotEmpty(residentCtmIds)) {
            careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(residentCtmIds);
            residentCareTeamMemberDao.deleteByIdIn(residentCtmIds);
        }
    }
    
    @Override
    public Boolean getIncludedInFaceSheetForCareTeamMember (Long careTeamMemberId) {
        return residentCareTeamMemberDao.getIncludeInFaceSheetById(careTeamMemberId);
    }

}
