package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.OrganizationCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.dao.phr.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.CareteamMemberBriefDto;
import com.scnsoft.eldermark.web.entity.CareteamMemberDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static com.scnsoft.eldermark.service.BaseServiceTest.createExpectedPerson;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 2/12/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class CareteamServiceTest {
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private ResidentsService residentsService;
    @Mock
    private PhysicianDao physicianDao;
    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;
    @Mock
    private OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @InjectMocks
    private CareteamService careteamService;

    protected final String email = TestDataGenerator.randomEmail();
    protected final String firstName = TestDataGenerator.randomName();
    protected final String lastName = TestDataGenerator.randomName();
    protected final Long ctmId = TestDataGenerator.randomId();
    protected final Long residentId = TestDataGenerator.randomId();


    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        careteamService.setDozer(dozer);
    }

    private List<ResidentCareTeamMember> setupMockitoExpectations(Long residentId, Pageable pageable) {
        final CareTeamRole ROLE_PARENT_GUARDIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        final CareTeamRole ROLE_PRIMARY_PHYSICIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PRIMARY_PHYSICIAN);
        final Employee employee = BaseServiceTest.createEmployee(email, firstName, lastName, null);
        final Long ctmId2 = TestDataGenerator.randomIdExceptOf(ctmId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(ctmId);
        rctm.setResidentId(residentId);
        rctm.setEmployee(employee);
        rctm.setCareTeamRole(ROLE_PARENT_GUARDIAN);
        final ResidentCareTeamMember rctm2 = new ResidentCareTeamMember();
        rctm2.setId(ctmId2);
        rctm2.setResidentId(residentId);
        rctm2.setEmployee(employee);
        rctm2.setCareTeamRole(ROLE_PRIMARY_PHYSICIAN);

        when(residentCareTeamMemberDao.findByResidentId(residentId, pageable)).thenReturn(new PageImpl<>(Arrays.asList(rctm, rctm2)));
        when(residentCareTeamMemberDao.findByResidentIdAndCareTeamRoleCode(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(rctm)));
        when(residentCareTeamMemberDao.findByResidentIdAndCareTeamRoleCodeNot(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(rctm2)));
        when(residentCareTeamMemberDao.getOne(ctmId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getOne(ctmId2)).thenReturn(rctm2);

        return Arrays.asList(rctm, rctm2);
    }

    @Test
    public void testListResidentCTMs() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        setupMockitoExpectations(residentId, pageable);

        Page<CareteamMemberBriefDto> result = careteamService.listResidentCTMs(residentId, null, pageable);

        assertThat(result.getContent(), hasSize(2));
        verify(residentCareTeamMemberDao).findByResidentId(residentId, pageable);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testListResidentCTMsFamily() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        setupMockitoExpectations(residentId, pageable);

        Page<CareteamMemberBriefDto> result = careteamService.listResidentCTMs(residentId, "FAMILY", pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(residentCareTeamMemberDao).findByResidentIdAndCareTeamRoleCode(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testListResidentCTMsCareProviders() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        setupMockitoExpectations(residentId, pageable);

        Page<CareteamMemberBriefDto> result = careteamService.listResidentCTMs(residentId, "CARE_PROVIDER", pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(residentCareTeamMemberDao).findByResidentIdAndCareTeamRoleCodeNot(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testGet() throws Exception {
        final List<ResidentCareTeamMember> rctms = setupMockitoExpectations(residentId, null);
        final ResidentCareTeamMember rctm = rctms.get(0);

        final CareteamMemberDto expected = new CareteamMemberDto();
        expected.setId(ctmId);
        expected.setEmployeeId(rctm.getEmployee().getId());
        expected.setEmergencyContact(Boolean.FALSE);
        expected.setCareTeamRole("Parent/Guardian");
        expected.setInvitationStatus(InvitationStatus.ACTIVE);
        expected.setContactEmail(email);
        expected.setPhysicianId(null);
        expected.setContactPhone(null);
        expected.setPerson(createExpectedPerson(rctm.getEmployee().getPerson()));

        when(residentCareTeamMemberDao.getOne(ctmId)).thenReturn(rctm);
        when(physicianDao.getIdByEmployeeId(anyLong())).thenReturn(null);

        CareteamMemberDto result = careteamService.get(residentId, ctmId);

        assertThat(result, sameBeanAs(expected));
        verify(residentCareTeamMemberDao).getOne(ctmId);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme