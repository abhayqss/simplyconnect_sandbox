package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PrivilegesDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.phr.Privilege;
import com.scnsoft.eldermark.entity.phr.User;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 11/20/2017.
 */
public class PrivilegesServiceTest extends BaseServiceTest {

    @Mock
    private PrivilegesDao privilegesDao;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private PrivilegesService privilegesService;

    @Test
    public void testCanInviteFriendToCareTeam() {
        // Expected objects
        final User provider = createProvider(userId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_LIST_INVITE_FRIEND)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canInviteFriendToCareTeam();

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanInviteFriendToCareTeam2() {
        // Expected objects
        final User provider = createProvider(userId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_LIST_INVITE_FRIEND)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        Boolean result = privilegesService.canInviteFriendToCareTeam();

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanDeleteFromCareTeam() {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_DELETE, role)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canDeleteFromCareTeam(role);

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanDeleteFromCareTeam2() {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_DELETE, role)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        Boolean result = privilegesService.canDeleteFromCareTeam(role);

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanDeleteThemselfFromCareTeam() {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_DELETE_SELF)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canDeleteThemselfFromCareTeam();

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanDeleteThemselfFromCareTeam2() {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.CARE_TEAM_DELETE_SELF)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        Boolean result = privilegesService.canDeleteThemselfFromCareTeam();

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanAddNote() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.ADD_NOTE)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canAddNote();

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanAddNote2() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.ADD_NOTE)).thenReturn(Boolean.FALSE);


        // Execute the method being tested
        Boolean result = privilegesService.canAddNote();

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanViewNote() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.VIEW_NOTE)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canViewNote();

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanViewNote2() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.VIEW_NOTE)).thenReturn(Boolean.FALSE);


        // Execute the method being tested
        Boolean result = privilegesService.canViewNote();

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanEditNote() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.EDIT_NOTE)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        Boolean result = privilegesService.canEditNote();

        // Validation
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanEditNote2() throws Exception {
        // Expected objects
        final User provider = createProvider(userId);
        final CareTeamRole role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(provider);
        when(privilegesDao.hasRight(provider.getEmployeeId(), Privilege.Name.EDIT_NOTE)).thenReturn(Boolean.FALSE);


        // Execute the method being tested
        Boolean result = privilegesService.canEditNote();

        // Validation
        Assert.assertEquals(Boolean.FALSE, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme