package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PrivilegesDao;
import com.scnsoft.eldermark.api.external.entity.Privilege;
import com.scnsoft.eldermark.api.external.specification.CommunityExtApiSpecifications;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

public class PrivilegesServiceTest extends BaseServiceTest {
    @Mock
    private PrivilegesDao privilegesDao;
    @Mock
    private CommunityDao communityDao;
    @Mock
    private CommunityExtApiSpecifications communityExtApiSpecifications;
    @Mock
    private CommunitySpecificationGenerator communitySpecifications;

    @InjectMocks
    private PrivilegesServiceImpl privilegesService;

    @BeforeEach
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\n\n", userId);
    }

    @Test
    public void testCanReadOrganization() {
        final Long organizationId = TestDataGenerator.randomId();
        final Long organizationId2 = TestDataGenerator.randomIdExceptOf(organizationId);

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, organizationId)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadOrganization(organizationId);
        final Boolean result2 = privilegesService.canReadOrganization(organizationId2);

        // Validation
        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.FALSE, result2);
    }

    @Test
    public void testCanReadCommunity_HasOrganizationOrCommunityAccess_ReturnsTrue() {
        final Long communityId = TestDataGenerator.randomId();

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId))
                .thenReturn(Boolean.TRUE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(communityId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanReadCommunity_HasConsanaAccess_ReturnsTrue() {
        final Long communityId = TestDataGenerator.randomId();
        var community = new Community();
        community.setId(communityId);

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);

        var spec = setUpConsanaEnabledSpecification(communityId);
        when(communityDao.exists(spec)).thenReturn(true);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(communityId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanReadCommunity_HasAllAccess_ReturnsTrue() {
        final Long communityId = TestDataGenerator.randomId();
        var community = new Community();
        community.setId(communityId);

        // Mockito expectations
        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId))
                .thenReturn(Boolean.TRUE);
        lenient().when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);

        var spec = setUpConsanaEnabledSpecification(communityId);
        lenient().when(communityDao.exists(spec)).thenReturn(true);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(communityId);

        // Validation
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testCanReadCommunity_NoAnyAccess_ReturnsFalse() {
        final Long communityId = TestDataGenerator.randomId();

        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(communityId);

        // Validation
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testCanReadCommunity_NotConsanaIntegratedCommunity_ReturnsFalse() {
        final Long communityId = TestDataGenerator.randomId();
        var community = new Community();
        community.setId(communityId);

        when(privilegesDao.hasRight(userId, Privilege.Name.ORGANIZATION_READ, Privilege.Name.COMMUNITY_READ, communityId))
                .thenReturn(Boolean.FALSE);
        when(privilegesDao.hasUserRight(userId, Privilege.Name.SPECIAL_CONSANA)).thenReturn(Boolean.TRUE);

        var spec = setUpConsanaEnabledSpecification(communityId);
        when(communityDao.exists(spec)).thenReturn(false);

        // Execute the method being tested
        final Boolean result = privilegesService.canReadCommunity(communityId);

        // Validation
        assertEquals(Boolean.FALSE, result);
    }

    private Specification<Community> setUpConsanaEnabledSpecification(Long communityId) {
        Specification<Community> byId = Mockito.mock(Specification.class);
        Specification<Community> integrationEnabled = Mockito.mock(Specification.class);
        Specification<Community> spec = Mockito.mock(Specification.class);

        lenient().when(communitySpecifications.byCommunityIdsEligibleForDiscovery(ArgumentMatchers.argThat(longs -> longs != null &&
                longs.size() == 1 && longs.iterator().next().equals(communityId))
        )).thenReturn(byId);

        lenient().when(communityExtApiSpecifications.consanaSyncEnabled(true)).thenReturn(integrationEnabled);
        lenient().when(byId.and(integrationEnabled)).thenReturn(spec);

        return spec;
    }

    @Test
    public void testListOrganizationsWithReadAccess() {
        var expected = Arrays.asList(new Organization(), new Organization());

        // Mockito expectations
        when(privilegesDao.listOrganizationsByPrivilege(userId, Privilege.Name.ORGANIZATION_READ)).thenReturn(expected);

        // Execute the method being tested
        var result = privilegesService.listOrganizationsWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void testListOrganizationIdsWithReadAccess() {
        final List<Long> expected = Arrays.asList(TestDataGenerator.randomId(), TestDataGenerator.randomId());

        // Mockito expectations
        when(privilegesDao.listOrganizationIdsByPrivilege(userId, Privilege.Name.ORGANIZATION_READ)).thenReturn(expected);

        // Execute the method being tested
        final List<Long> result = privilegesService.listOrganizationIdsWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

    @Test
    public void testListCommunitiesWithReadAccess() {
        var expected = Arrays.asList(new Community(), new Community());

        // Mockito expectations
        when(privilegesDao.listCommunitiesByPrivilege(userId, Privilege.Name.COMMUNITY_READ)).thenReturn(expected);

        // Execute the method being tested
        var result = privilegesService.listCommunitiesWithReadAccess();

        // Validation
        assertEquals(expected, result);
    }

}
