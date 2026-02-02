package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.Token;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/14/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CareTeamSecurityUtilsTest {

    @Mock
    private UserDao userDao;

    @Mock
    UserResidentRecordsDao userResidentRecordsDao;

    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Mock
    private AccessRightsService accessRightsService;

    @Mock
    private Authentication authentication;

    @Mock
    private MPIService mpiService;

    @InjectMocks
    private CareTeamSecurityUtils careTeamSecurityUtils;

    // Shared test data
    private final Long userId = TestDataGenerator.randomId();
    private final Token token = Token.generateToken(userId);
    private final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
    private final Long residentId = TestDataGenerator.randomId();
    private final List<Long> residentIds = Collections.singletonList(residentId);
    private final Set<Long> residentIdsSet = new HashSet<>(residentIds);
    private final Resident resident = new Resident(residentId);
    private final User userConsumer = User.Builder.anUser()
            .withId(consumerId)
            .withResident(resident)
            .build();
    private final Long careReceiverId = TestDataGenerator.randomId();

    @Before
    public void mockTokenAuth() {
        when(authentication.getDetails()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userDao.findOne(consumerId)).thenReturn(userConsumer);
    }

    @Test
    public void testCheckAccessToUserInfo() {
        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(userId, AccessRight.Code.MY_PHR);

        // Validation
        assertTrue(result);
    }

    @Test
    public void testCheckAccessToUserInfoRestricted() {
        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);

        // Validation
        assertFalse(result);
    }

    @Test
    public void testCheckAccessToUserInfoOrThrow() {
        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testCheckAccessToUserInfoOrThrow2() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setEmployee(employee);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(residentIds);
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId)).thenThrow(NoResultException.class);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds))
                .thenReturn(Collections.singletonList(rctm));

        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(consumerId);

        // Validation
        verify(residentCareTeamMemberDao).getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds);
    }

    @Test(expected = PhrException.class)
    public void testCheckAccessToUserInfoOrThrowThrowsAccessForbidden() {
        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    @Test(expected = PhrException.class)
    public void testCheckAccessToUserInfoOrThrowThrowsAccessForbidden2() {
        // Expected objects
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(null)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(consumerId);
    }

    @Test(expected = PhrException.class)
    public void testCheckAccessToUserInfoOrThrowThrowsAccessForbidden3() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(residentIds);
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId))
                .thenThrow(NoResultException.class);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds))
                .thenReturn(Collections.<ResidentCareTeamMember>emptyList());

        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(consumerId);

        // Validation
        verify(residentCareTeamMemberDao).getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds);
    }

    @Test
    public void testCheckAccessToUserInfoAsProvider() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setEmployee(employee);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(residentIds);
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds))
                .thenReturn(Collections.singletonList(rctm));
        when(accessRightsService.checkHasAccessRight(rctm, AccessRight.Code.MY_PHR)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);

        // Validation
        assertTrue(result);
        verify(residentCareTeamMemberDao).getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds);
        verify(accessRightsService).checkHasAccessRight(rctm, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testCheckAccessToUserInfoAsProviderRestricted() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setEmployee(employee);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(residentIds);
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds))
                .thenReturn(Collections.singletonList(rctm));
        when(accessRightsService.checkHasAccessRight(rctm, AccessRight.Code.MY_PHR)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);

        // Validation
        assertFalse(result);
        verify(residentCareTeamMemberDao).getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds);
        verify(accessRightsService).checkHasAccessRight(rctm, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testCheckAccessToUserInfoAsProviderRestricted2() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(residentIds);
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId)).thenThrow(NoResultException.class);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds))
                .thenReturn(Collections.<ResidentCareTeamMember>emptyList());


        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);

        // Validation
        assertFalse(result);
        verify(residentCareTeamMemberDao).getResidentCareTeamMembersByEmployeeIdAndResidentIds(employeeId, residentIds);
    }

    @Test(expected = PhrException.class)
    public void testCheckAccessToUserInfoAsProviderThrowsUserNotFound() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerId)).thenReturn(Collections.<Long>emptyList());

        // Execute the method being tested
        careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testCheckAccessToUserInfoAsProviderRestricted3() {
        // Expected objects
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(null)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        boolean result = careTeamSecurityUtils.checkAccessToUserInfo(consumerId, AccessRight.Code.MY_PHR);

        // Validation
        assertFalse(result);
    }

    @Test
    public void testIsAssociatedWhenUserAssociatedWithResidentId() {
        // Mockito expectations
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(residentIds);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(new HashSet<>(residentIds));


        // Execute the method being tested
        assertTrue(careTeamSecurityUtils.isAssociated(userId, residentId));
    }

    @Test
    public void testIsAssociatedWhenUserIsEmployeeFromPatientCareTeam() {
        // Expected objects

        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user is an employee from the patient's care team.
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(Collections.singleton(employeeId)))
                .thenReturn(residentIds);

        // Execute the method being tested
        assertTrue(careTeamSecurityUtils.isAssociated(userId, residentId));
    }

    @Test
    public void testIsAssociatedWhenUserIsEmployeeFromPatientCareTeamWithAccessRight() {
        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user is an employee from the patient's care team with specific access right.
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);

        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(accessRight);

        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeId), accessRight))
                .thenReturn(residentIds);

        // Execute the method being tested
        assertTrue(careTeamSecurityUtils.isAssociated(userId, residentId, AccessRight.Code.EVENT_NOTIFICATIONS));
    }

    @Test
    public void testIsAssociatedWhenUserIsEmployeeFromCareTeamOfPatientMergedRecord() {
        // Expected objects

        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user not an employee from the patient's care team
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(accessRight);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeId), accessRight))
                .thenReturn(empty);

        //step 3. current is an employee from a care team of patient's merged record
        when(mpiService.listMergedResidents(empty)).thenReturn(residentIds);

        // Execute the method being tested
        assertTrue(careTeamSecurityUtils.isAssociated(userId, residentId));
    }

    @Test
    public void testIsAssociatedWhenUserIsEmployeeFromCareTeamOfPatientMergedRecordWithAccessRight() {
        // Expected objects

        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user not an employee from the patient's care team
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(Collections.singleton(employeeId)))
                .thenReturn(empty);



        //step 3. current is an employee from a care team of patient's merged record
        when(mpiService.listMergedResidents(empty)).thenReturn(residentIds);

        // Execute the method being tested
        assertTrue(careTeamSecurityUtils.isAssociated(userId, residentId));
    }

    @Test
    public void testIsNotAssociated() {
        // Expected objects

        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user not an employee from the patient's care team
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(Collections.singleton(employeeId)))
                .thenReturn(empty);

        //step 3. current user not an employee from a care team of patient's merged record
        when(mpiService.listMergedResidents(empty)).thenReturn(empty);

        // Execute the method being tested
        assertFalse(careTeamSecurityUtils.isAssociated(userId, residentId));
    }

    @Test(expected = PhrException.class)
    public void testNotFoundEmployee() {
        // Mockito expectations
        final List<Long> empty = Collections.emptyList();

        //step 1. not associated with current patient record
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(empty);
        when(userResidentRecordsDao.getAllProviderIdsByUserId(userId)).thenReturn(Collections.<Long>emptySet());

        //step 2. current user not an employee from the patient's care team
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);

        //step 3. current user not an employee from a care team of patient's merged record
        when(mpiService.listMergedResidents(empty)).thenReturn(empty);

        // Execute the method being tested
        careTeamSecurityUtils.isAssociated(userId, residentId);
    }

    @Test
    public void testCheckAccessToCareTeamMemberId() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);


        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(residentIds);

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        careTeamMember.setEmployee(employee);

        when(residentCareTeamMemberDao.get(careReceiverId)).thenReturn(careTeamMember);

        assertTrue(careTeamSecurityUtils.checkAccessToCareTeamMember(careReceiverId, AccessRight.Code.MY_PHR));
    }

    @Test
    public void testCheckAccessToCareTeamMemberIdNoAccess() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);


        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(Collections.<Long>emptyList());

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        careTeamMember.setEmployee(employee);

        when(residentCareTeamMemberDao.get(careReceiverId)).thenReturn(careTeamMember);

        assertFalse(careTeamSecurityUtils.checkAccessToCareTeamMember(careReceiverId, AccessRight.Code.MY_PHR));
    }

    @Test
    public void testCheckAccessToCareTeamMemberIdNoAccessWrongEmployee() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);


        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(residentIds);

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        final Employee employee2 = new Employee();
        employee.setId(TestDataGenerator.randomIdExceptOf(employeeId));
        careTeamMember.setEmployee(employee2);

        when(residentCareTeamMemberDao.get(careReceiverId)).thenReturn(careTeamMember);

        assertFalse(careTeamSecurityUtils.checkAccessToCareTeamMember(careReceiverId, AccessRight.Code.MY_PHR));
    }

    @Test
    public void testCheckAccessToCareTeamMember() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);

        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(residentIds);

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        careTeamMember.setEmployee(employee);

        assertTrue(careTeamSecurityUtils.checkAccessToCareTeamMember(careTeamMember, AccessRight.Code.MY_PHR));
    }

    @Test
    public void testCheckAccessToCareTeamMemberNoAccess() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);

        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(Collections.<Long>emptyList());

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        careTeamMember.setEmployee(employee);

        assertFalse(careTeamSecurityUtils.checkAccessToCareTeamMember(careTeamMember, AccessRight.Code.MY_PHR));
    }

    @Test
    public void testCheckAccessToCareTeamMemberNoAccessWrongEmployee() {
        final AccessRight accessRight = new AccessRight();
        when(accessRightsService.getAccessRight(AccessRight.Code.MY_PHR)).thenReturn(accessRight);

        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();
        when(userDao.findOne(userId)).thenReturn(user);
        when(userDao.getOne(userId)).thenReturn(user);


        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(
                Collections.singleton(employeeId), accessRight)).thenReturn(residentIds);

        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        final Employee employee2 = new Employee();
        employee.setId(TestDataGenerator.randomIdExceptOf(employeeId));
        careTeamMember.setEmployee(employee2);

        assertFalse(careTeamSecurityUtils.checkAccessToCareTeamMember(careTeamMember, AccessRight.Code.MY_PHR));
    }

}
