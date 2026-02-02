package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.PhysicianCategoryDao;
import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.PhysicianCategory;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.PhysicianDto;
import com.scnsoft.eldermark.web.entity.PhysicianExtendedDto;
import com.scnsoft.eldermark.web.entity.ProfessionalProfileDto;
import com.scnsoft.eldermark.web.entity.SpecialityDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/16/2017.
 */
public class PhysiciansServiceTest extends BaseServiceTest {

    @Mock
    private PhysicianDao physicianDao;
    @Mock
    private AvatarService avatarService;
    @Mock
    private PhysicianCategoryDao physicianCategoryDao;

    @InjectMocks
    private PhysiciansService physiciansService;

    // Shared test data
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
    public void testGetPhysician() {
        // Expected objects
        final User user = super.createProvider(userId);

        final String fax = TestDataGenerator.randomPhone();
        final ProfessionalProfileDto professionalInfo = new ProfessionalProfileDto();
        professionalInfo.setFax(fax);
        professionalInfo.setSpecialities(Collections.<String>emptyList());
        professionalInfo.setInNetworkInsurances(Collections.<String>emptyList());
        final PhysicianExtendedDto expectedPhysician = new PhysicianExtendedDto();
        expectedPhysician.setId(physicianId);
        expectedPhysician.setUserId(userId);
        expectedPhysician.setPhotoUrl("url" + userId);
        expectedPhysician.setFullName(user.getFullName());
        expectedPhysician.setSpeciality(BEHAVIORAL_HEALTH);
        expectedPhysician.setProfessionalInfo(professionalInfo);

        final Physician physician = new Physician();
        physician.setId(physicianId);
        physician.setUserMobile(user);
        physician.setVerified(Boolean.TRUE);
        physician.setDiscoverable(Boolean.TRUE);
        physician.setFax(fax);

        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(physician);
        when(avatarService.getPhotoUrl(userId)).thenReturn("url" + userId);

        // Execute the method being tested
        PhysicianExtendedDto result = physiciansService.getPhysician(physicianId);

        // Validation
        assertThat(result, sameBeanAs(expectedPhysician));
    }

    @Test
    public void testListPhysicians() {
        // Expected objects
        final User user = super.createProvider(userId);

        final String fax = TestDataGenerator.randomPhone();
        final ProfessionalProfileDto professionalInfo = new ProfessionalProfileDto();
        professionalInfo.setFax(fax);
        professionalInfo.setSpecialities(Collections.<String>emptyList());
        final PhysicianDto expectedPhysician = new PhysicianDto();
        expectedPhysician.setId(physicianId);
        expectedPhysician.setUserId(userId);
        expectedPhysician.setPhotoUrl("url" + userId);
        expectedPhysician.setFullName(user.getFullName());
        expectedPhysician.setSpeciality(BEHAVIORAL_HEALTH);

        final Physician physician = new Physician();
        physician.setId(physicianId);
        physician.setUserMobile(user);
        physician.setEmployee(user.getEmployee());
        physician.setVerified(Boolean.TRUE);
        physician.setDiscoverable(Boolean.TRUE);
        physician.setFax(fax);

        // Mockito expectations
        when(physicianDao.findAllByDiscoverableTrueAndVerifiedTrue()).thenReturn(Collections.singletonList(physician));
        when(avatarService.getPhotoUrl(userId)).thenReturn("url" + userId);

        // Execute the method being tested
        List<PhysicianDto> result = physiciansService.listPhysicians();

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedPhysician));
    }

    @Test
    public void testGetPhysicianOrThrow() {
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
    public void testGetPhysicianOrThrowThrowsPhysicianNotFound() {
        // Mockito expectations
        when(physicianDao.findByIdAndDiscoverableTrueAndVerifiedTrue(physicianId)).thenReturn(null);

        // Execute the method being tested
        physiciansService.getPhysicianOrThrow(physicianId);
    }

    @Test
    public void testListPhysicianSpecialities() {
        // Expected objects
        final Long categoryId = TestDataGenerator.randomId();
        final Long categoryId2 = TestDataGenerator.randomIdExceptOf(categoryId);
        final PhysicianCategory physicianCategory = new PhysicianCategory();
        physicianCategory.setId(categoryId);
        physicianCategory.setDisplayName("name 1");
        final PhysicianCategory physicianCategory2 = new PhysicianCategory();
        physicianCategory2.setId(categoryId2);
        physicianCategory2.setDisplayName("name 2");

        final SpecialityDto expectedSpeciality = new SpecialityDto();
        expectedSpeciality.setId(categoryId);
        expectedSpeciality.setDisplayName("name 1");
        final SpecialityDto expectedSpeciality2 = new SpecialityDto();
        expectedSpeciality2.setId(categoryId2);
        expectedSpeciality2.setDisplayName("name 2");

        // Mockito expectations
        when(physicianCategoryDao.findAll()).thenReturn(Arrays.asList(physicianCategory, physicianCategory2));

        // Execute the method being tested
        List<SpecialityDto> result = physiciansService.listPhysicianSpecialities();

        assertThat(result, containsInAnyOrder(expectedSpeciality, expectedSpeciality2));
    }

}
