package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.phr.PrivilegesDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.phr.Privilege;
import com.scnsoft.eldermark.services.predicates.ConsanaCommunityIntegrationEnabledPredicate;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 4/14/2018.
 */
public class PrivilegesServiceTest extends BaseServiceTest {
    @Mock
    private PrivilegesDao privilegesDao;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate;

    @InjectMocks
    private PrivilegesService privilegesService;

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\n\n", userId);
    }

    @Test
    public void testCanReadOrganization() throws Exception {
        final Long databaseId = TestDataGenerator.randomId();
        final Long databaseId2 = TestDataGenerator.randomIdExceptOf(databaseId);

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, databaseId)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadOrganization(databaseId);
        final Boolean result2 = privilegesService.canReadOrganization(databaseId2);

        // Validation
        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.FALSE, result2);
    }

    @Test
    public void testCanReadCommunity_HasOrganizationOrCommunityAccess_ReturnsTrue() throws Exception {
        final Long organizationId = TestDataGenerator.randomId();

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId))
                .thenReturn(Boolean.TRUE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(organizationId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanReadCommunity_HasConsanaAccess_ReturnsTrue() throws Exception {
        final Long organizationId = TestDataGenerator.randomId();
        final Organization organization = new Organization();

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);
        when(organizationDao.get(organizationId)).thenReturn(organization);
        when(consanaCommunityIntegrationEnabledPredicate.apply(organization)).thenReturn(true);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(organizationId);

        // Validation
        assertEquals(Boolean.TRUE, result);
        verify(consanaCommunityIntegrationEnabledPredicate).apply(organization);
    }

    @Test
    public void testCanReadCommunity_HasAllAccess_ReturnsTrue() throws Exception {
        final Long organizationId = TestDataGenerator.randomId();
        final Organization organization = new Organization();

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId))
                .thenReturn(Boolean.TRUE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);
        when(organizationDao.get(organizationId)).thenReturn(organization);
        when(consanaCommunityIntegrationEnabledPredicate.apply(organization)).thenReturn(true);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(organizationId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanReadCommunity_NoAnyAccess_ReturnsFalse() throws Exception {
        final Long organizationId = TestDataGenerator.randomId();

        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(organizationId);

        // Validation
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanReadCommunity_NotConsanaIntegratedCommunity_ReturnsFalse() throws Exception {
        final Long organizationId = TestDataGenerator.randomId();
        final Organization organization = new Organization();

        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, organizationId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);
        when(organizationDao.get(organizationId)).thenReturn(organization);
        when(consanaCommunityIntegrationEnabledPredicate.apply(organization)).thenReturn(false);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(organizationId);

        // Validation
        assertEquals(Boolean.FALSE, result);
        verify(consanaCommunityIntegrationEnabledPredicate).apply(organization);
    }

    @Test
    public void testCanManageNucleusData() throws Exception {
        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.SPECIAL_NUCLEUS, (Long) null))
                .thenReturn(Boolean.TRUE)
                .thenReturn(Boolean.FALSE);

        // Execute the method being tested
        final Boolean result = privilegesService.canManageNucleusData();
        final Boolean result2 = privilegesService.canManageNucleusData();

        // Validation
        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.FALSE, result2);
    }

    @Test
    public void testListOrganizationsWithReadAccess() throws Exception {
        final List<Database> expected = Arrays.asList(new Database(), new Database());

        // Mockito expectations
        when(privilegesDao.listOrganizationsByPrivilege(userId, Privilege.Name.ORGANIZATION_READ)).thenReturn(expected);

        // Execute the method being tested
        final List<Database> result = privilegesService.listOrganizationsWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void testListOrganizationIdsWithReadAccess() throws Exception {
        final List<Long> expected = Arrays.asList(TestDataGenerator.randomId(), TestDataGenerator.randomId());

        // Mockito expectations
        when(privilegesDao.listOrganizationIdsByPrivilege(userId, Privilege.Name.ORGANIZATION_READ)).thenReturn(expected);

        // Execute the method being tested
        final List<Long> result = privilegesService.listOrganizationIdsWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void testListCommunitiesWithReadAccess() throws Exception {
        final List<Organization> expected = Arrays.asList(new Organization(), new Organization());

        // Mockito expectations
        when(privilegesDao.listCommunitiesByPrivilege(userId, Privilege.Name.COMMUNITY_READ)).thenReturn(expected);

        // Execute the method being tested
        final List<Organization> result = privilegesService.listCommunitiesWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void hasConsanaAccess_HasSpecialConsanaPrivilege_returnsTrue() {
        // Mockito expectations
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA))
                .thenReturn(Boolean.TRUE);

        // Execute the method being tested
        final Boolean result = privilegesService.hasConsanaAccess();

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testHasConsanaAccess_NoSpecialConsanaPrivilege_returnsFalse() {
        // Mockito expectations
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA))
                .thenReturn(Boolean.FALSE);

        // Execute the method being tested
        final Boolean result = privilegesService.hasConsanaAccess();

        // Validation
        assertEquals(Boolean.FALSE, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme