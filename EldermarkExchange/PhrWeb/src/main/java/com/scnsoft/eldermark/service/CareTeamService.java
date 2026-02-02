package com.scnsoft.eldermark.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.CareTeamRelationDao;
import com.scnsoft.eldermark.dao.phr.CareTeamRelationshipDao;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatUser;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.service.chat.PhrChatService;
import com.scnsoft.eldermark.service.internal.EmployeeSupplier;
import com.scnsoft.eldermark.service.internal.EmployeeSupplierFactory;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.web.entity.ActivityDto;
import com.scnsoft.eldermark.web.entity.CareteamMemberBriefDto;
import com.scnsoft.eldermark.web.entity.CareteamMemberDto;
import com.scnsoft.eldermark.web.entity.ContactStatus;
import com.scnsoft.eldermark.web.entity.PhrChatThreadDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 5/2/2017
 */
@Service
@Transactional
public class CareTeamService extends BasePhrService {

    private static final Logger logger = LoggerFactory.getLogger(CareTeamService.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ProfileService profileService;

    @Autowired
    AccessRightsService accessRightsService;

    @Autowired
    ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    CareTeamRoleDao careTeamRoleDao;

    @Autowired
    CareTeamRelationDao careTeamRelationDao;

    @Autowired
    CareTeamRelationshipDao careTeamRelationshipDao;

    @Autowired
    PhysiciansService physiciansService;

    @Autowired
    ActivityService activityService;

    @Autowired
    PhrResidentService phrResidentService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private DozerBeanMapper dozer;

    @Autowired
    ContactService contactService;

    @Autowired
    UserRegistrationApplicationService userRegistrationApplicationService;

    @Autowired
    NotificationPreferencesService notificationPreferencesService;

    @Autowired
    private NotificationsFacade notificationsFacade;

    @Autowired
    DatabasesService databasesService;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private EmployeeSupplierFactory employeeSupplierFactory;

    @Autowired
    private ResidentUpdateQueueProducer residentUpdateQueueProducer;

    @Autowired
    PhrChatService phrChatService;

    @Autowired
    PhrChatUserDao phrChatUserDao;

    private List<ResidentCareTeamMember> getCareTeamMembersForPatients(final Collection<Long> residentIds) {
        // `affiliated` is not applicable here so its value is `null`.
        // In mobile application context visible data is not restricted to any organization.
//        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE, Sort.Direction.ASC, "employee.label");
        return residentCareTeamMemberDao.getCareTeamMembers(residentIds, null, null);
    }

    /**
     * Toggle 'Emergency Contact' mark of a care team member.
     *
     * @param userId User ID
     * @param contactId Care team member ID
     * @param isEmergencyContact Mark if {@code true}, unmark if {@code false}
     *
     * @return TRUE
     * @throws PhrException of type {@link PhrExceptionType#ACCESS_FORBIDDEN} if the current user has no right
     *         to toggle 'Emergency Contact' mark of the selected care team member.
     */
    public Boolean setCareTeamMemberEmergency(Long userId, Long contactId, boolean isEmergencyContact) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        if (isCurrentUserInProviderMode(userId) && !privilegesService.canSetCareTeamMemberEmergency()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);

        ctm.setEmergencyContact(isEmergencyContact);
        return Boolean.TRUE;
    }

    @Transactional(readOnly = true)
    public List<CareteamMemberDto> getUserCareTeamMembers(Long userId) {
        return getUserCareTeamMembers(userId, null);
    }

    @Transactional(readOnly = true)
    public List<CareteamMemberDto> getUserCareTeamMembers(Long userId, String phrChatToken) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        Collection<Long> residentIds = getResidentIdsOrThrow(userId);
        Map<ResidentCareTeamMember, User> ctms = new HashMap<>();
        List<ResidentCareTeamMember> careTeamMembersForPatient = getCareTeamMembersForPatients(residentIds);
        for (ResidentCareTeamMember residentCareTeamMember : careTeamMembersForPatient) {
            User userCTM = userDao.getFirstByEmployee(residentCareTeamMember.getEmployee());
            ctms.put(residentCareTeamMember, userCTM);
        }
        String phrChatResponse = "";
        if (phrChatToken != null)
            phrChatResponse = phrChatService.getMessageThread(userId, phrChatToken);
        return transform(ctms, phrChatResponse);
    }

    @Transactional(readOnly = true)
    public List<CareteamMemberBriefDto> getUserCareTeamMembersBrief(Long userId) {
        return getUserCareTeamMembersBrief(userId, null);
    }

    @Transactional(readOnly = true)
    public List<CareteamMemberBriefDto> getUserCareTeamMembersBrief(Long userId, String phrChatToken) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        Collection<Long> residentIds = getResidentIdsOrThrow(userId);
        Map<ResidentCareTeamMember, User> ctms = new HashMap<>();

        List<ResidentCareTeamMember> careTeamMembersForPatient = getCareTeamMembersForPatients(residentIds);
        for (ResidentCareTeamMember residentCareTeamMember : careTeamMembersForPatient) {
            User userCTM = userDao.getFirstByEmployee(residentCareTeamMember.getEmployee());
            ctms.put(residentCareTeamMember, userCTM);
        }
        String phrChatResponse = "";
        if (phrChatToken != null) {
            phrChatResponse = phrChatService.getMessageThread(userId, phrChatToken);
        }
        return transformListItems(ctms, phrChatResponse);
    }

    @Transactional(readOnly = true)
    public CareteamMemberDto getUserCareTeamMember(Long userId, Long contactId, String phrChatToken) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        final ResidentCareTeamMember ctm = getResidentCareTeamMemberOrThrow(userId, contactId);
        User userCTM = userDao.getFirstByEmployee(ctm.getEmployee());

        String phrChatResponse = "";
        if (phrChatToken != null)
            phrChatResponse = phrChatService.getMessageThread(userId, phrChatToken);

        return transform(ctm, userCTM, null,phrChatResponse);

        //return transform(ctm, userCTM);
    }

    @Transactional(readOnly = true)
    public CareteamMemberDto getUserCareTeamMember(Long userId, Long contactId) {
       return getUserCareTeamMember(userId, contactId, null);
    }

    CareteamMemberDto getResidentCareTeamMember(Long residentId, Long employeeId) {
        ResidentCareTeamMember ctm = residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId);
        User userCTM = userDao.getFirstByEmployee(ctm.getEmployee());
        return transform(ctm, userCTM);
    }

    ResidentCareTeamMember getResidentCareTeamMemberOrThrow(Long userId, Long contactId) {
        final ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);
        return ctm;
    }

    public Boolean deleteUserCareTeamMember(Long userId, Long contactId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        Long residentId = ctm.getResidentId();

        validateAssociationAndEditable(userId, ctm);

        notificationPreferencesService.deleteByCtmId(contactId);
        residentCareTeamMemberDao.delete(contactId);

        residentUpdateQueueProducer.putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);

        return Boolean.TRUE;
    }

    private Collection<Long> validateAssociation(Long userId, ResidentCareTeamMember ctm) {
        Collection<Long> residentIds = getResidentIds(userId);
        if (ctm == null || !residentIds.contains(ctm.getResidentId())) {
            throw new PhrException(PhrExceptionType.CTM_NOT_ASSOCIATED);
        }
        return residentIds;
    }

    private Collection<Long> validateAssociationAndEditable(Long userId, ResidentCareTeamMember ctm) {
        Collection<Long> residentIds = validateAssociation(userId, ctm);
        if (!isEditable(ctm)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        return residentIds;
    }

    public CareteamMemberDto updateUserCareTeamMember(Long userId, Long contactId, CareteamMemberDto updatedCareTeamMember) {
        throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        /*
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociationAndEditable(userId, ctm);
        User userCTM = userDao.getUserByCTM(ctm);
        // update entity from dto
        if (StringUtils.isNotBlank(updatedCareTeamMember.getSsn())) {
            userCTM.setSsn(updatedCareTeamMember.getSsn());
        }
        List<AddressDto> dtoAddresses = updatedCareTeamMember.getPerson().getAddresses();
        if (CollectionUtils.isNotEmpty(dtoAddresses)) {
            AddressDto addressDto = dtoAddresses.get(0);
            Person person = userCTM.getEmployee().getPerson();
            PersonAddress personAddress = userCTM.getPrimaryAddress();
            if (personAddress == null) {
                personAddress = new PersonAddress();
                person.getAddresses().add(personAddress);
            }
            String country = personAddress.getCountry() == null ? "US" : personAddress.getCountry();
            personAddress.setCountry(country);
            personAddress.setPostalCode(addressDto.getPostalCode());
            personAddress.setState(addressDto.getState());
            personAddress.setCity(addressDto.getCity());
            personAddress.setStreetAddress(addressDto.getStreetAddress());
            userDao.save(userCTM);
        }
        List<TelecomDto> dtoTelecoms = updatedCareTeamMember.getPerson().getTelecoms();
        if (CollectionUtils.isNotEmpty(dtoTelecoms)) {
            TelecomDto dtoEmail = null;
            TelecomDto dtoPhone = null;
            for (TelecomDto dtoTelecom : dtoTelecoms) {
                if ("EMAIL".equals(dtoTelecom.getUseCode())) {
                    dtoEmail = dtoTelecom;
                }
                if ("WP".equals(dtoTelecom.getUseCode())) {
                    dtoPhone = dtoTelecom;
                }
            }
            if (dtoEmail != null) {
                // TODO: at which level should we store this secondary email?
                userCTM.getPersonalProfile().setSecondaryEmail(dtoEmail.getValue());
            }
            if (dtoPhone != null) {
                // TODO: at which level should we store this secondary phone?
                userCTM.getPersonalProfile().setSecondaryPhone(dtoPhone.getValue());
            }
        }
        if (updatedCareTeamMember.getRelation() != null && ctm.getCareTeamRelation() != null) {
            if (!updatedCareTeamMember.getRelation().equals(ctm.getCareTeamRelation().getCode())) {
                CareTeamRelation relation = careTeamRelationDao.getByCode(updatedCareTeamMember.getRelation());
                ctm.setCareTeamRelation(relation);
            }
        }
        return transform(ctm, userCTM);
        */
    }

    /**
     * Invite a friend or family member to user's Care Team.
     *
     * @param ssn Social security number. May be null.
     * @param phone Phone number.
     * @param relation The nature of the relationship between a patient and a contact person for that patient
     */
    public CareteamMemberDto inviteFriend(Long userId, String ssn, String email, String phone, String firstName, String lastName,
                                          CareTeamRelation.Relation relation) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        if (isCurrentUserInProviderMode(userId) && !privilegesService.canInviteFriendToCareTeam()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        User userPatient = userDao.getOne(userId);
        if (!profileService.isPatient(userPatient) && !Boolean.TRUE.equals(userPatient.getAutocreated())) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO_DURING_INVITATION);
        }

        final CareTeamRole careTeamRole = careTeamRoleDao.getByCode(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        Employee userFriendEmployee = getOrCreateEmployee(userPatient, email, phone, firstName, lastName, careTeamRole);
        User userFriend = userDao.getFirstByEmployee(userFriendEmployee);

        if (userFriend == null) {
            //validateEmailNotExistsOrThrow(email, PhrExceptionType.INVITEE_EMAIL_CONFLICT);

            // if invited user is not registered neither as patient, nor as guardian -> create new invite
            User inviter = careTeamSecurityUtils.getCurrentUser();
            RegistrationApplication invite = userRegistrationApplicationService.createRegistrationApplicationForInvitee(
                    inviter, userFriendEmployee, ssn, phone, email, firstName, lastName);

            userRegistrationApplicationService.save(invite);
        } else {
            if (userFriend.getId().equals(userId)) {
                throw new PhrException(PhrExceptionType.SELF_INVITE_TO_CT);
            }

            if (!profileService.isGuardian(userFriend)) {
                // if invited user is already registered as patient
                profileService.addGuardianAccountType(userFriend, false);
            }
        }

        ResidentCareTeamMember residentCareTeamMember = createResidentCareTeamMember(userPatient, userFriendEmployee, careTeamRole, relation,
                CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ContactStatus status = notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, userFriend, userFriendEmployee, residentCareTeamMember);

        return transform(residentCareTeamMember, userFriend, status);
    }

    private ResidentCareTeamMember createResidentCareTeamMemberMedicalStaff(User userPatient, User userPhysician, CareTeamRole careTeamRole) {
        return createResidentCareTeamMember(userPatient, userPhysician.getEmployee(), careTeamRole, null, CareTeamRelationship.Relationship.MEDICAL_STAFF);
    }

    private ResidentCareTeamMember createResidentCareTeamMember(User userPatient, Employee userCtmEmployee, CareTeamRole careTeamRole,
                                                                CareTeamRelation.Relation relation, CareTeamRelationship.Relationship relationship) {
        User currentUser = careTeamSecurityUtils.getCurrentUser();
        Resident resident = getOrCreateResidentInUnaffiliatedOrg(userPatient);
        validateNoDuplicatedCareTeamMember(resident, userCtmEmployee, careTeamRole.getCode());

        ResidentCareTeamMember residentCareTeamMember = new ResidentCareTeamMember();
        residentCareTeamMember.setEmergencyContact(false);
        residentCareTeamMember.setCareTeamRole(careTeamRole);
        residentCareTeamMember.setEmployee(userCtmEmployee);
        residentCareTeamMember.setResident(resident);
        residentCareTeamMember.setResidentId(resident.getId());
        // TODO change createdByResidentId and createdByEmployeeId to createdByUserId since CTM can invite another CTM to patient's CT ? But what if this information will be required later in Web app?
        residentCareTeamMember.setCreatedByResidentId(currentUser.getResidentId());
        residentCareTeamMember.setCreatedById(currentUser.getEmployeeId());

        CareTeamRelationship careTeamRelationship = careTeamRelationshipDao.getByCode(relationship);
        residentCareTeamMember.setCareTeamRelationship(careTeamRelationship);

        if (relation != null) {
            CareTeamRelation careTeamRelation = careTeamRelationDao.getByCode(relation);
            residentCareTeamMember.setCareTeamRelation(careTeamRelation);
        }

        residentCareTeamMember.setAccessRights(accessRightsService.getDefaultAccessRights());

        residentCareTeamMember = residentCareTeamMemberDao.create(residentCareTeamMember);

        notificationPreferencesService.createDefaultCareTeamMemberNotificationPreferences(residentCareTeamMember);
        residentCareTeamMember = residentCareTeamMemberDao.merge(residentCareTeamMember);

        residentUpdateQueueProducer.putToResidentUpdateQueue(resident.getId(), ResidentUpdateType.CARE_TEAM);

        return residentCareTeamMember;
    }

    private Employee getOrCreateEmployee(User userPatient, String email, String phone, String firstName, String lastName, CareTeamRole careTeamRole) {
        // TODO choose between two different employee lookup strategies
        // 1:
        //final Collection<Long> providerIds = getHealthProviderProviderIds(userPatient.getId());
        //final EmployeeSupplier employeeSupplier = employeeSupplierFactory.getEmployeeSupplier(providerIds, email, phone, firstName, lastName);
        // 2:
        final EmployeeSupplier employeeSupplier = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);

        Employee userFriendEmployee = employeeSupplier.getEmployee();
        if (userFriendEmployee == null) {
            // create a new employee and assign to the same community as the patient
            userFriendEmployee = contactService.createEmployeeForInvitedFriend(userPatient.getResident(), email, phone, firstName, lastName, careTeamRole);
        }

        return userFriendEmployee;
    }

    private Resident getOrCreateResidentInUnaffiliatedOrg(User userPatient) {
        // SCPAPP-376 : Automatically create a record in the Unaffiliated organization if user-consumer has no such record during invitation.
        Resident resident = phrResidentService.findAssociatedResidentInUnaffiliated(userPatient.getId());
        if (resident == null) {
            resident = phrResidentService.createAssociatedResidentFromUserData(userPatient);
        }
        return resident;
    }

    public CareteamMemberDto invitePhysician(Long userId, Long physicianId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        User userPatient = userDao.getOne(userId);
        if (!profileService.isPatient(userPatient) && !Boolean.TRUE.equals(userPatient.getAutocreated())) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO_DURING_INVITATION);
        }
        Physician physician = physiciansService.getPhysicianOrThrow(physicianId);
        User userPhysician = physician.getUserMobile();
        if (userId.equals(userPhysician.getId())) {
            throw new PhrException(PhrExceptionType.SELF_INVITE_TO_CT_PHYSICIAN);
        }

        final CareTeamRole careTeamRole = userPhysician.getEmployee().getCareTeamRole();

        ResidentCareTeamMember residentCareTeamMember = createResidentCareTeamMemberMedicalStaff(userPatient, userPhysician, careTeamRole);

        notificationsFacade.notifyMedicalStaffAboutInvitationToCareTeam(userPatient, userPhysician, residentCareTeamMember);

        return transform(residentCareTeamMember, userPhysician, ContactStatus.EXISTING_ACTIVE);
    }

    private void validateNoDuplicatedCareTeamMember(Resident resident, Employee employeeCtm, CareTeamRoleCode careTeamRoleCode) {
        final CareTeamRole role = careTeamRoleDao.getByCode(careTeamRoleCode);
        final List<ResidentCareTeamMember> existing = residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeAndRole(
                resident.getId(), employeeCtm.getId(), role.getId());
        if (CollectionUtils.isNotEmpty(existing)) {
            throw new PhrException(PhrExceptionType.CTM_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getRecentActivity(Long userId, Long contactId, Pageable pageable) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);

        List<Activity> activities = activityService.getRecentActivity(userId, ctm.getEmployee(), pageable);

        if (isCurrentUserInProviderMode(userId)) {
            // inverse call direction when CTM is viewing recent activity
            for (Activity activity : activities) {
                if (activity instanceof CallActivity) {
                    final Boolean isIncoming = ((CallActivity) activity).getIncoming();
                    ((CallActivity) activity).setIncoming(!isIncoming);
                } else if (activity instanceof VideoActivity) {
                    final Boolean isIncoming = ((VideoActivity) activity).getIncoming();
                    ((VideoActivity) activity).setIncoming(!isIncoming);
                }
            }
        }

        return ActivityService.transform(activities);
    }

    @Transactional(readOnly = true)
    public Long getRecentActivityCount(Long userId, Long contactId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);

        return activityService.countRecentActivity(userId, ctm.getEmployee());
    }

    public <T extends ActivityDto> void logActivity(Long userId, Long contactId, T activityDto) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        User userPatient = userDao.getOne(userId);
        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);

        activityService.logCallActivity(userPatient.getId(), ctm.getEmployee(), activityDto);
    }

    /**
     * @return Can the current User manage access rights for the specified Care Team Member?
     */
    private boolean canManageAccessRights(ResidentCareTeamMember ctm) {
        final Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        // is the specified Care Team Member invited by this user ?
        Collection<Long> residentIds = getResidentIds(currentUserId);
        if (residentIds.contains(ctm.getCreatedByResidentId())) {
            return true;
        }

        final User currentUser = userDao.getOne(currentUserId);
        /*
        final Long employeeId = currentUser.getEmployeeId();
        // is the specified Care Team Member added by Employee associated with this user ?
        if (employeeId != null && ObjectUtils.equals(employeeId, ctm.getCreatedById())) {
            return true;
        }*/

        // has the current user a consumer mode ?
        // is the current user associated with the specified Care Team Member ?
        // is the specified Care Team Member associated with “Unaffiliated” organization ?
        return profileService.isPatient(currentUser) &&
                residentIds.contains(ctm.getResidentId()) &&
                ctm.getEmployee() != null && databasesService.isUnaffiliated(ctm.getEmployee().getDatabase());

    }

    /**
     * @return Can the current User manage access rights for the specified Care Team Member?
     */
    public boolean canManageAccessRights(Long contactId) {
        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        return canManageAccessRights(ctm);
    }

    public Map<AccessRight.Code, Boolean> getAccessRights(Long userId, Long contactId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);

        return AccessRightsService.getAccessRights(ctm);
    }

    public void updateAccessRights(Long userId, Long contactId, Map<AccessRight.Code, Boolean> accessRightsMap) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        ResidentCareTeamMember ctm = residentCareTeamMemberDao.get(contactId);
        validateAssociation(userId, ctm);
        if (!canManageAccessRights(ctm)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        accessRightsService.updateAccessRights(ctm, accessRightsMap);
        residentCareTeamMemberDao.merge(ctm);
    }

    // ================================================================================================================

    private List<CareteamMemberBriefDto> transformListItems(Map<ResidentCareTeamMember, User> ctms) {
        return transformListItems(ctms, null);
    }

    private List<CareteamMemberBriefDto> transformListItems(Map<ResidentCareTeamMember, User> ctms,
            String phrChatResponse) {
        List<CareteamMemberBriefDto> dtos = new ArrayList<>(ctms.size());
        for (Map.Entry<ResidentCareTeamMember, User> ctm : ctms.entrySet()) {
            dtos.add(transformListItem(ctm.getKey(), ctm.getValue(), phrChatResponse));
        }
        Collections.sort(dtos);
        return dtos;
    }

    private List<CareteamMemberDto> transform(Map<ResidentCareTeamMember, User> ctms, String phrChatResponse) {
        List<CareteamMemberDto> dtos = new ArrayList<>(ctms.size());
        for (Map.Entry<ResidentCareTeamMember, User> ctm : ctms.entrySet()) {
            dtos.add(transform(ctm.getKey(), ctm.getValue(), null, phrChatResponse));
        }
        Collections.sort(dtos);
        return dtos;
    }

    private CareteamMemberBriefDto transformListItem(ResidentCareTeamMember ctm, User userCTM) {
        return transformListItem(ctm, userCTM, null);
    }

    private CareteamMemberBriefDto transformListItem(ResidentCareTeamMember ctm, User userCTM, String phrChatResponse) {
        Validate.notNull(ctm);
        if (userCTM == null) {
            // prevent NPE for CTMs without mobile account
            userCTM = new User();
        }

        CareteamMemberBriefDto dto = new CareteamMemberBriefDto();
        dto.setId(ctm.getId());
        dto.setUserId(userCTM.getId());

        if (ctm.getEmployee().getStatus().equals(EmployeeStatus.PENDING)) {  //TODO!!!
            dto.setInvitationStatus(InvitationStatus.PENDING);
        } else {
            dto.setInvitationStatus(InvitationStatus.ACTIVE);
        }

        dto.setFullName(ctm.getEmployee().getFullName());
        dto.setEmergencyContact(Boolean.TRUE.equals(ctm.getEmergencyContact()));
        dto.setCareTeamRole(ctm.getCareTeamRole().getName());
        dto.setContactPhone(getAnyPhoneOf(ctm.getEmployee().getPerson()));

        dto.setDataSource(DataSourceService.transform(ctm.getEmployee().getDatabase(), ctm.getResidentId()));

        if (userCTM.getId() != null && StringUtils.isNotEmpty(phrChatResponse)) {
            PhrChatUser phrChatUser= phrChatUserDao.findByNotifyUserId(userCTM.getId());
            if (phrChatUser != null) {
                dto.setChatUserId(phrChatUser.getId());
                dto.setChatThread(parseChatResponseData(userCTM.getId(), phrChatResponse));
            }
        }
        return dto;
    }

    private CareteamMemberDto transform(ResidentCareTeamMember ctm, User userCTM) {
        return transform(ctm, userCTM, null, null);
    }

    private CareteamMemberDto transform(ResidentCareTeamMember ctm, User userCTM, ContactStatus status) {
        return transform(ctm, userCTM, status, null);
    }

    private CareteamMemberDto transform(ResidentCareTeamMember ctm, User userCTM, ContactStatus status,
            String phrChatResponse) {
        Validate.notNull(ctm);
        if (userCTM == null) {
            userCTM = new User();    // prevent NPE
        }

        CareteamMemberDto dto = new CareteamMemberDto();
        dto.setId(ctm.getId());
        dto.setUserId(userCTM.getId());

        Physician physician = physiciansService.getPhysicianByUserId(userCTM.getId());
        if (physician != null) {
            dto.setPhysicianInfo(physiciansService.transformListItem(physician));

            if (Boolean.TRUE.equals(physician.getVerified())) {
                dto.setInvitationStatus(InvitationStatus.ACTIVE);
            } else {
                dto.setInvitationStatus(InvitationStatus.DECLINED);     // impossible?
            }
            dto.setPhotoUrl(dto.getPhysicianInfo().getPhotoUrl());
        } else {
            if (ctm.getEmployee().getStatus().equals(EmployeeStatus.PENDING)) {
                dto.setInvitationStatus(InvitationStatus.PENDING);
            } else {
                dto.setInvitationStatus(InvitationStatus.ACTIVE);
            }
            dto.setPhotoUrl(avatarService.getPhotoUrl(userCTM.getId()));
        }
        dto.setContactStatus(status);

        dto.setSsnLastFourDigits(userCTM.getSsnLastFourDigits());

        PersonDto personDto = dozer.map(ctm.getEmployee().getPerson(), PersonDto.class);

        if (userCTM.getPrimaryAddress() != null) {
            AddressDto addressDto = dozer.map(userCTM.getPrimaryAddress(), AddressDto.class);
            personDto.setAddresses(Collections.singletonList(addressDto));  // probably rewriting addresses
        }

        String personPhone = getAnyPhoneOf(ctm.getEmployee().getPerson());
        String personEmail = PersonService.getPersonTelecomValue(ctm.getEmployee().getPerson(), PersonTelecomCode.EMAIL);
        if (StringUtils.isNotBlank(personPhone)) {
            dto.setContactPhone(personPhone);
        }
        if (StringUtils.isNotBlank(personEmail)) {
            dto.setContactEmail(personEmail);
        }
        if (CollectionUtils.isEmpty(personDto.getNames())) {
            final NameDto name = transformName(ctm.getEmployee());
            if (name != null) {
                personDto.getNames().add(name);
            }
        }
        dto.setPerson(personDto);

        dto.setEditable(isEditable(ctm));
        dto.setEmergencyContact(Boolean.TRUE.equals(ctm.getEmergencyContact()));

        if (ctm.getCareTeamRelation() != null) {
            dto.setRelation(ctm.getCareTeamRelation().getCode());
        }
        if (ctm.getCareTeamRelationship() != null) {
            dto.setRelationship(ctm.getCareTeamRelationship().getCode());
        }
        dto.setCareTeamRole(ctm.getCareTeamRole().getName());

        dto.setDataSource(DataSourceService.transform(ctm.getEmployee().getDatabase(), ctm.getResidentId()));
        if (userCTM.getId() != null && StringUtils.isNotEmpty(phrChatResponse)) {
            PhrChatUser phrChatUser= phrChatUserDao.findByNotifyUserId(userCTM.getId());
            if (phrChatUser != null) {
                dto.setChatUserId(phrChatUser.getId());
                dto.setChatThread(parseChatResponseData(userCTM.getId(), phrChatResponse));
            }
        }
        dto.setIncludedInFaceSheet(ctm.getIncludeInFaceSheet());
        return dto;
    }
    private static String parseChatResponseData(Long userId, String phrChatResponse) {
        try {
            if (StringUtils.isNotEmpty(phrChatResponse)) {
                try {
                    String responseData = mapper
                            .writeValueAsString(mapper.readValue(phrChatResponse, HashMap.class));
                    String threadsData = mapper
                            .writeValueAsString(mapper.readValue(responseData, HashMap.class).get("threads"));

                    List<PhrChatThreadDto> listOfThreads = mapper.readValue(threadsData,
                            new TypeReference<List<PhrChatThreadDto>>() {
                            });
                    for (PhrChatThreadDto item : listOfThreads) {
                        if (userId.equals(item.getNotifyUserId())) {
                            String response = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(item);
                            return response;
                            // return item.toString();
                        }
                    }
                } catch (Exception e) {
                    logger.error("Parsing Error of PhrCharResponse", e);
                }
            }
        } catch (Exception e) {
            logger.error("Parsing Error of PhrCharResponse", e);
        }
        return null;
    }

    private static String getAnyPhoneOf(Person person) {
        String personPhone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.MC);
        String personPhone2 = PersonService.getPersonTelecomValue(person, PersonTelecomCode.WP);
        String personPhone3 = PersonService.getPersonTelecomValue(person, PersonTelecomCode.HP);

        if (StringUtils.isNotBlank(personPhone)) {
            return personPhone;
        } else if (StringUtils.isNotBlank(personPhone2)) {
            return personPhone2;
        } else if (StringUtils.isNotBlank(personPhone3)) {
            return personPhone3;
        } else {
            return null;
        }
    }

    private NameDto transformName(Employee employee) {
        NameDto name = new NameDto();
        name.setFullName(employee.getFullName());
        name.setUseCode("L");

        return StringUtils.isNotBlank(name.getFullName()) ? name : null;
    }

    private static boolean isCurrentUserInProviderMode(Long userId) {
        return !PhrSecurityUtils.checkAccessToUserInfo(userId);
    }

    /**
     * @param ctm Care Team Member of a Resident
     * @param residentIds IDs of active Residents of current User. Active Residents are selected from active Health Providers.
     * @return Is editable?
     */
    private boolean isEditable(ResidentCareTeamMember ctm, User currentUser, Collection<Long> residentIds) {
        final Long employeeId = currentUser.getEmployeeId();
        // Consumer should be able to delete the care team members invited by him/herself
        if (residentIds.contains(ctm.getCreatedByResidentId())) {
            return true;
        }
        // Provider should be able to delete the care team members invited or added by him/herself.
        // After SCPAPP-383 (permissions matrix for CTM removal) this case may be applicable only for
        // Providers (logged in users) with roles "Primary physician" and "Behavioral Health".
        if (employeeId != null && ObjectUtils.equals(employeeId, ctm.getCreatedById()) &&
                CareTeamRoleCode.ROLE_PARENT_GUARDIAN.equals(ctm.getCareTeamRole().getCode())) {
            return true;
        }
        // Consumer should be able to delete his own CTMs associated with “Unaffiliated” organization in regardless of their role.
        if (profileService.isPatient(currentUser) && residentIds.contains(ctm.getResidentId()) &&
                databasesService.isUnaffiliated(ctm.getEmployee().getDatabase())) {
            return true;
        }
        // If "Person Receiving Services" CTM was added by a contact (via Web app), and this CTM links a resident and a contact
        // which both are associated with the same mobile user, other users SHOULD NOT be able to remove this care team member.
        // (in this case a resident wants to receive alerts about his events)
        if (CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES.equals(ctm.getCareTeamRole().getCode())) {
            final User ctmUser = userDao.getFirstByEmployee(ctm.getEmployee());
            if (ctmUser != null) {
                final Collection<Long> ctmResidentIds = getResidentIds(ctmUser.getId());
                if (ctmResidentIds.contains(ctm.getResidentId())) {
                    return false;
                }
            }
        }
        // May a care team member associated with “Unaffiliated” organization delete themself from a care team of Consumer ?
        // By default, each care team member is able to do this.
        if (employeeId != null && employeeId.equals(ctm.getEmployee().getId()) && databasesService.isUnaffiliated(ctm.getEmployee().getDatabase()) &&
                privilegesService.canDeleteThemselfFromCareTeam()) {
            return true;
        }
        // Check permissions matrix (applicable only if logged in User's organization matches CTM's organization)
        if (employeeId != null) {
            final long databaseId = currentUser.getEmployee().getDatabaseId();
            final long ctmDatabaseId = ctm.getEmployee().getDatabaseId();
            return ObjectUtils.equals(databaseId, ctmDatabaseId) && privilegesService.canDeleteFromCareTeam(ctm.getCareTeamRole());
        }
        return false;
    }

    /**
     * @param ctm Care Team Member of a Resident
     * @return Is editable by current user?
     */
    private boolean isEditable(ResidentCareTeamMember ctm) {
        final User currentUser = careTeamSecurityUtils.getCurrentUser();
        Collection<Long> residentIds = getResidentIds(currentUser.getId());
        return isEditable(ctm, currentUser, residentIds);
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }
    
    @Transactional
    public Boolean deleteContactFromFacesheet(Long userId, Long contactId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        ResidentCareTeamMember residentCareTeamMember = residentCareTeamMemberDao.get(contactId);
        validateAssociationAndEditable(userId, residentCareTeamMember);
        residentCareTeamMember.setIncludeInFaceSheet(false);
        residentCareTeamMember = residentCareTeamMemberDao.merge(residentCareTeamMember);
        return true;
    }
    
    @Transactional
    public Boolean addContactIntoFacesheet(Long userId, Long contactId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        ResidentCareTeamMember residentCareTeamMember = residentCareTeamMemberDao.get(contactId);
        validateAssociationAndEditable(userId, residentCareTeamMember);
        residentCareTeamMember.setIncludeInFaceSheet(true);
        residentCareTeamMember = residentCareTeamMemberDao.merge(residentCareTeamMember);
        return true;
    }

}
