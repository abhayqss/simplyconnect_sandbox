package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PhysicianDao;
import com.scnsoft.eldermark.api.external.specification.ClientCareTeamExtApiSpecifications;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberDto;
import com.scnsoft.eldermark.api.shared.entity.InvitationStatus;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.scnsoft.eldermark.api.external.service.BaseServiceTest.createExpectedPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareteamServiceTest {
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private ResidentsService residentsService;
    @Mock
    private PhysicianDao physicianDao;
    @Mock
    private ClientCareTeamMemberDao residentCareTeamMemberDao;
    @Mock
    private ClientCareTeamExtApiSpecifications specifications;

    @InjectMocks
    private CareteamServiceImpl careteamService;

    protected final String email = TestDataGenerator.randomEmail();
    protected final String firstName = TestDataGenerator.randomName();
    protected final String lastName = TestDataGenerator.randomName();
    protected final Long ctmId = TestDataGenerator.randomId();
    protected final Long residentId = TestDataGenerator.randomId();


    @BeforeEach
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        careteamService.setDozer(dozer);
    }

    private List<ClientCareTeamMember> setupListExpectations(Long residentId, String directory, Pageable pageable) {
        final CareTeamRole ROLE_PARENT_GUARDIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        final CareTeamRole ROLE_PRIMARY_PHYSICIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PRIMARY_PHYSICIAN);
        final Employee employee = prepareEmployee();
        final Long ctmId2 = TestDataGenerator.randomIdExceptOf(ctmId);

        final ClientCareTeamMember rctm = prepareClientCTM(ctmId, residentId, employee, ROLE_PARENT_GUARDIAN);
        final ClientCareTeamMember rctm2 = prepareClientCTM(ctmId2, residentId, employee, ROLE_PRIMARY_PHYSICIAN);

        //todo lenient?
        Specification<ClientCareTeamMember> spec = Mockito.mock(Specification.class);
        if (directory == null) {
            when(specifications.clientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Arrays.asList(rctm, rctm2)));
        } else if ("FAMILY".equalsIgnoreCase(directory)) {
            when(specifications.familyClientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(rctm)));
        } else if ("CARE_PROVIDER".equalsIgnoreCase(directory)) {
            when(specifications.careProviderClientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(rctm2)));
        }

        return Arrays.asList(rctm, rctm2);
    }

    private ClientCareTeamMember prepareClientCTM(Long id, Long residentId, Employee employee, CareTeamRole role) {
        var ctm = new ClientCareTeamMember();
        ctm.setId(id);
        ctm.setClientId(residentId);
        ctm.setEmployee(employee);
        ctm.setCareTeamRole(role);
        return ctm;
    }

    private Employee prepareEmployee() {
        return BaseServiceTest.createEmployee(email, firstName, lastName, null);
    }

    @Test
    public void testListResidentCTMsNoDir() throws Exception {
        testListResidentCTMs(null);
    }

    @Test
    public void testListResidentCTMsFamily() throws Exception {
        testListResidentCTMs("FAMILY");
    }

    @Test
    public void testListResidentCTMsCareProviders() throws Exception {
        testListResidentCTMs("CARE_PROVIDER");
    }

    void testListResidentCTMs(String directory) throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final CareTeamRole ROLE_PARENT_GUARDIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        final CareTeamRole ROLE_PRIMARY_PHYSICIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PRIMARY_PHYSICIAN);
        final Employee employee = prepareEmployee();
        final Long ctmId2 = TestDataGenerator.randomIdExceptOf(ctmId);

        final ClientCareTeamMember rctm = prepareClientCTM(ctmId, residentId, employee, ROLE_PARENT_GUARDIAN);
        final ClientCareTeamMember rctm2 = prepareClientCTM(ctmId2, residentId, employee, ROLE_PRIMARY_PHYSICIAN);

        Specification<ClientCareTeamMember> spec = Mockito.mock(Specification.class);
        int expectedSize = 0;
        if (directory == null) {
            when(specifications.clientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Arrays.asList(rctm, rctm2)));
            expectedSize = 2;
        } else if ("FAMILY".equalsIgnoreCase(directory)) {
            when(specifications.familyClientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(rctm)));
            expectedSize = 1;
        } else if ("CARE_PROVIDER".equalsIgnoreCase(directory)) {
            when(specifications.careProviderClientCtm(residentId)).thenReturn(spec);
            when(residentCareTeamMemberDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(rctm2)));
            expectedSize = 1;
        }

        var result = careteamService.listResidentCTMs(residentId, directory, pageable);

        assertThat(result).hasSize(expectedSize);
        verify(residentCareTeamMemberDao).findAll(spec, pageable);
        verify(residentsService).checkAccessOrThrow(residentId);
    }


    @Test
    public void testGet() {
        final CareTeamRole ROLE_PARENT_GUARDIAN = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_PARENT_GUARDIAN);
        final Employee employee = prepareEmployee();
        final ClientCareTeamMember rctm = prepareClientCTM(ctmId, residentId, employee, ROLE_PARENT_GUARDIAN);

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

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(residentCareTeamMemberDao).getOne(ctmId);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme