package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.PhysicianExtendedDto;
import com.scnsoft.eldermark.web.entity.ProfessionalProfileDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 2/15/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhysiciansServiceTest {

    @Mock
    private PhysicianDao physicianDao;

    @InjectMocks
    private PhysiciansService physiciansService;

    // Shared test data
    protected final Long userId = TestDataGenerator.randomId();
    protected final Long physicianId = TestDataGenerator.randomId();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nPhysician ID: %d\n\n", userId, physicianId);
    }

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        physiciansService.setDozer(dozer);
    }

    @Test
    public void testGet() throws Exception {
        // Expected objects
        final User user = User.Builder.anUser()
                .withId(userId)
                .build();
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
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(physician);

        // Execute the method being tested
        PhysicianExtendedDto result = physiciansService.get(physicianId);

        // Validation
        assertThat(result, sameBeanAs(expectedPhysician));
    }

    @Test
    public void testGetPhysicianOrThrow() throws Exception {
        // Expected objects
        final Physician physician = new Physician();
        physician.setId(physicianId);

        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(physician);

        // Execute the method being tested
        Physician result = physiciansService.getPhysicianOrThrow(physicianId);

        // Validation
        assertThat(result, sameBeanAs(physician));
    }

    @Test(expected = PhrException.class)
    public void testGetPhysicianOrThrowThrowsPhysicianNotFound() throws Exception {
        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(null);

        // Execute the method being tested
        physiciansService.getPhysicianOrThrow(physicianId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme