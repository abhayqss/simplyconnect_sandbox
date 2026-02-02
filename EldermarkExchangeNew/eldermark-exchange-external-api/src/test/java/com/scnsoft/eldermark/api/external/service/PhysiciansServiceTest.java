package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PhysicianDao;
import com.scnsoft.eldermark.api.external.entity.Physician;
import com.scnsoft.eldermark.api.external.web.dto.PhysicianExtendedDto;
import com.scnsoft.eldermark.api.external.web.dto.ProfessionalProfileDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PhysiciansServiceTest {

    @Mock
    private PhysicianDao physicianDao;

    @InjectMocks
    private PhysiciansServiceImpl physiciansService;

    // Shared test data
    protected final Long userId = TestDataGenerator.randomId();
    protected final Long physicianId = TestDataGenerator.randomId();

    @BeforeEach
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nPhysician ID: %d\n\n", userId, physicianId);
    }

    @BeforeEach
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        physiciansService.setDozer(dozer);
    }

    @Test
    public void testGet() throws Exception {
        // Expected objects
        var user = new MobileUser();
        user.setId(userId);

        final Long employeeId = TestDataGenerator.randomId();
        final CareTeamRole role = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH);
        final Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName(TestDataGenerator.randomName());
        employee.setLastName(TestDataGenerator.randomName());
        employee.setCareTeamRole(role);

        final String fax = TestDataGenerator.randomPhone();
        final ProfessionalProfileDto professionalInfo = new ProfessionalProfileDto();
        professionalInfo.setFax(fax);
        professionalInfo.setSpecialities(Collections.<String>emptyList());
        professionalInfo.setInNetworkInsurances(Collections.<String>emptyList());
        final PhysicianExtendedDto expectedPhysician = new PhysicianExtendedDto();
        expectedPhysician.setId(physicianId);
        expectedPhysician.setFullName(employee.getFullName());
        expectedPhysician.setSpeciality(role.getName());
        expectedPhysician.setProfessionalInfo(professionalInfo);

        final Physician physician = new Physician();
        physician.setId(physicianId);
        physician.setEmployee(employee);
        physician.setUserMobile(user);
        physician.setVerified(Boolean.TRUE);
        physician.setDiscoverable(Boolean.TRUE);
        physician.setFax(fax);

        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(Optional.of(physician));

        // Execute the method being tested
        PhysicianExtendedDto result = physiciansService.get(physicianId);

        // Validation
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedPhysician);
    }

    @Test
    public void testGetPhysicianOrThrow() throws Exception {
        // Expected objects
        final Physician physician = new Physician();
        physician.setId(physicianId);

        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(Optional.of(physician));

        // Execute the method being tested
        Physician result = physiciansService.getPhysicianOrThrow(physicianId);

        // Validation
        assertThat(result).usingRecursiveComparison().isEqualTo(physician);
    }

    @Test
    public void testGetPhysicianOrThrowThrowsPhysicianNotFound() throws Exception {
        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(Optional.empty());

        // Execute the method being tested
        assertThrows(PhrException.class, () -> physiciansService.getPhysicianOrThrow(physicianId));
    }

}
