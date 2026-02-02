package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ResidentLightDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.HealthProviderDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/30/2017.
 */
public class HealthProviderServiceTest extends BaseServiceTest {

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;
    @Mock
    private ResidentService residentService;
    @Mock
    private ResidentLightDao residentLightDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;

    @Captor
    private ArgumentCaptor<List<UserResidentRecord>> captor;

    @InjectMocks
    private HealthProviderService healthProviderService;

    // Shared test data
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nResident ID (2): %d\nResident ID (3): %d\nSSN: %s\nPhone: %s\nEmail: %s\n\n",
                userId, residentId, residentId2, residentId3, ssn, phone, email);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId, ssn, phone, email, null, null);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);
        if (consumerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }

        return user;
    }

    @Test
    public void testGetHealthProviders() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders();
        final UserResidentRecord[] records = prepareUserResidentRecords(userId);
        final List<UserResidentRecord> expectedNewcomers = Collections.emptyList();

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]));
        when(residentService.getDirectMergedResidentIds(user.getResident()))
                .thenReturn(new HashSet<>(Arrays.asList(residentId2, residentId3)));

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.getHealthProviders(userId);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));
        assertThat(result.get(2), sameBeanAs(expectedHealthProviders[2]));
        verify(userResidentRecordsDao, never()).deleteUnusedRecords(eq(userId), anyCollectionOf(Long.class));
        if (!expectedNewcomers.isEmpty()) {
            verify(userResidentRecordsDao).save(expectedNewcomers);
        }
    }

    @Test
    public void testGetHealthProvidersNoPatientInfo() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);
        user.setResident(null);
        user.setResidentId(null);

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Collections.<UserResidentRecord>emptyList());
        when(residentService.getDirectMergedResidentIds(user.getResident()))
                .thenThrow(NullPointerException.class);

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.getHealthProviders(userId);

        // Validation
        assertThat(result, hasSize(0));
    }

    @Test
    public void testGetHealthProvidersWithDelete() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders();
        final UserResidentRecord[] records = prepareUserResidentRecords(userId);
        final Set<Long> expectedVictims = new HashSet<>(Collections.singleton(residentId3));
        final List<UserResidentRecord> expectedNewcomers = Collections.emptyList();

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]))
                .thenReturn(Arrays.asList(records[0], records[1]));
        when(residentService.getDirectMergedResidentIds(user.getResident()))
                .thenReturn(new HashSet<>(Arrays.asList(residentId2)));

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.getHealthProviders(userId);

        // Validation
        assertThat(result, hasSize(2));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));

        verify(userResidentRecordsDao).deleteUnusedRecords(userId, expectedVictims);
        if (!expectedNewcomers.isEmpty()) {
            verify(userResidentRecordsDao).save(expectedNewcomers);
        }
    }

    @Test
    public void testGetHealthProvidersWithDeleteAll() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final UserResidentRecord[] records = prepareUserResidentRecords(userId);
        final Set<Long> expectedVictims = new HashSet<>(Arrays.asList(residentId2, residentId3));
        final List<UserResidentRecord> expectedNewcomers = Collections.emptyList();

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]))
                .thenReturn(Arrays.asList(records[0]));
        when(residentService.getDirectMergedResidentIds(user.getResident()))
                .thenReturn(new HashSet<Long>());

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.getHealthProviders(userId);

        // Validation
        assertThat(result, hasSize(1));

        verify(userResidentRecordsDao).deleteUnusedRecords(userId, expectedVictims);
        if (!expectedNewcomers.isEmpty()) {
            verify(userResidentRecordsDao).save(expectedNewcomers);
        }
        verify(userDao, never()).save(user);
    }

    @Test
    public void testGetHealthProvidersWithUpdate() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders();
        final UserResidentRecord[] records = prepareUserResidentRecords(userId);
        final List<UserResidentRecord> expectedNewcomers = Arrays.asList(records[2]);
        final ResidentLight resident = createResident(residentId);
        final ResidentLight resident2 = createResident(residentId2);
        final ResidentLight resident3 = createResident(residentId3);
        final HashSet<Long> updatedResidentIds = new HashSet<>(Arrays.asList(residentId, residentId2, residentId3));

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1]))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]));
        when(residentService.getDirectMergedResidentIds(user.getResident()))
                .thenReturn(new HashSet<>(Arrays.asList(residentId2, residentId3)));
        when(residentLightDao.findAllByIdIn(updatedResidentIds))
                .thenReturn(new HashSet<>(Arrays.asList(resident, resident2, resident3)));

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.getHealthProviders(userId);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));
        assertThat(result.get(2), sameBeanAs(expectedHealthProviders[2]));

        verify(userResidentRecordsDao, never()).deleteUnusedRecords(eq(userId), anyCollectionOf(Long.class));
        if (!expectedNewcomers.isEmpty()) {
            verify(userResidentRecordsDao).save(expectedNewcomers);
        }
    }

    @Test
    public void testSetCurrentHealthProvider() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final UserResidentRecord[] records = prepareUserResidentRecords(userId, false, 1);
        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders(false, 1);

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]));
        when(userResidentRecordsDao.countByUserIdAndCurrentIsTrue(userId))
                .thenReturn(1L);
        when(userResidentRecordsDao.setCurrentRecord(eq(userId), or(eq(residentId), or(eq(residentId2), eq(residentId3)))))
                .thenReturn(1);

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.setCurrentHealthProvider(userId, residentId2);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));
        assertThat(result.get(2), sameBeanAs(expectedHealthProviders[2]));

        verify(userResidentRecordsDao).dropCurrentRecordForUser(userId);
        verify(userResidentRecordsDao).setCurrentRecord(userId, residentId2);
        verify(userDao, never()).save(user);
    }

    @Test(expected = PhrException.class)
    public void testSetCurrentHealthProviderThrowsNotAssociated() {
        // Expected objects
        setUpMockitoExpectations(userId);

        final Long wrongId = TestDataGenerator.randomIdExceptOf(residentId, residentId2, residentId3);

        // Mockito expectations
        when(userResidentRecordsDao.setCurrentRecord(eq(userId), or(eq(residentId), or(eq(residentId2), eq(residentId3)))))
                .thenReturn(1);

        // Execute the method being tested
        healthProviderService.setCurrentHealthProvider(userId, wrongId);
    }

    @Test
    public void testSetCurrentHealthProviderMerged() {
        // Expected objects
        setUpMockitoExpectations(userId);

        final UserResidentRecord[] records = prepareUserResidentRecords(userId, true, 0);
        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders(true, 0);

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]));
        when(userResidentRecordsDao.countByUserIdAndCurrentIsTrue(userId))
                .thenReturn(1L);
        when(userResidentRecordsDao.setCurrentRecordsAll(userId))
                .thenReturn(3);

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.setCurrentHealthProvider(userId, 0L);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));
        assertThat(result.get(2), sameBeanAs(expectedHealthProviders[2]));

        verify(userResidentRecordsDao).setCurrentRecordsAll(userId);
    }

    @Test(expected = PhrException.class)
    public void testSetCurrentHealthProviderMergedThrowsNotFoundPatientInfo() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);
        user.setResident(null);
        user.setResidentId(null);

        // Mockito expectations
        when(userResidentRecordsDao.setCurrentRecordsAll(userId)).thenReturn(0);

        // Execute the method being tested
        healthProviderService.setCurrentHealthProvider(userId, 0L);
    }

    @Ignore("OUTDATED. Delete?")
    @Test
    public void testSetCurrentHealthProviderNoChanges() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final UserResidentRecord[] records = prepareUserResidentRecords(userId, false, user.getResidentId().intValue());
        final HealthProviderDto[] expectedHealthProviders = prepareHealthProviders(false, user.getResidentId().intValue());

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Arrays.asList(records[0], records[1], records[2]));
        when(userResidentRecordsDao.countByUserIdAndCurrentIsTrue(userId))
                .thenReturn(1L);
        when(userResidentRecordsDao.setCurrentRecord(eq(userId), or(eq(residentId), or(eq(residentId2), eq(residentId3)))))
                .thenReturn(1);

        // Execute the method being tested
        List<HealthProviderDto> result = healthProviderService.setCurrentHealthProvider(userId, user.getResidentId());

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedHealthProviders[0]));
        assertThat(result.get(1), sameBeanAs(expectedHealthProviders[1]));
        assertThat(result.get(2), sameBeanAs(expectedHealthProviders[2]));

        verify(userResidentRecordsDao, never()).dropCurrentRecordForUser(userId);
        verify(userResidentRecordsDao, never()).setCurrentRecord(userId, residentId2);
        verify(userDao, never()).save(user);
    }

    @Test
    public void testUpdateUserResidentRecords() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);
        user.setResident(null);
        user.setResidentId(null);

        final ResidentLight resident = createResident(residentId);
        final ResidentLight resident2 = createResident(residentId2);
        final ResidentLight resident3 = createResident(residentId3);
        final HashSet<Long> updatedResidentIds = new HashSet<>(Arrays.asList(residentId, residentId2, residentId3));

        final UserResidentRecord[] expectedRecords = prepareUserResidentRecords(userId, true, 0);

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId))
                .thenReturn(Collections.<UserResidentRecord>emptyList());
        when(residentLightDao.findAllByIdIn(updatedResidentIds))
                .thenReturn(new HashSet<>(Arrays.asList(resident, resident2, resident3)));

        // Execute the method being tested
        healthProviderService.updateUserResidentRecords(user, updatedResidentIds, true);

        verify(userDao).updateMainResident(eq(userId), anyLong());
        verify(userResidentRecordsDao).save(captor.capture());
        final List<UserResidentRecord> records = captor.getValue();
        assertThat(records, containsInAnyOrder(expectedRecords));
    }

    // ==== Utility methods ====

    private ResidentLight createResident(Long residentId) {
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);

        final Organization facility = new Organization();
        facility.setDatabase(person.getDatabase());
        facility.setName("test org");
        final ResidentLight resident = new ResidentLight();
        resident.setId(residentId);
        resident.setFacility(facility);
        resident.setDatabase(person.getDatabase());
        resident.setDatabaseId(person.getDatabaseId());

        return resident;
    }

    private UserResidentRecord[] prepareUserResidentRecords(Long consumerUserId) {
        return prepareUserResidentRecords(consumerUserId, false, 0);
    }

    private UserResidentRecord[] prepareUserResidentRecords(Long consumerUserId, boolean isMerged, int current) {
        final String databaseName = createDatabase().getName();

        final UserResidentRecord record = new UserResidentRecord();
        record.setUserId(consumerUserId);
        record.setResidentId(residentId);
        record.setProviderId(resident.getFacility().getId());
        record.setProviderName(databaseName + " : test org");
        record.setCurrent(isMerged || current == 0);
        record.setFoundByMatching(Boolean.TRUE);
        final UserResidentRecord record2 = new UserResidentRecord();
        record2.setUserId(consumerUserId);
        record2.setResidentId(residentId2);
        record2.setProviderId(resident.getFacility().getId());
        record2.setProviderName(databaseName + " : test org");
        record2.setCurrent(isMerged || current == 1);
        record2.setFoundByMatching(Boolean.TRUE);
        final UserResidentRecord record3 = new UserResidentRecord();
        record3.setUserId(consumerUserId);
        record3.setResidentId(residentId3);
        record3.setProviderId(resident.getFacility().getId());
        record3.setProviderName(databaseName + " : test org");
        record3.setCurrent(isMerged || current == 2);
        record3.setFoundByMatching(Boolean.TRUE);

        return new UserResidentRecord[] {record, record2, record3};
    }

    private HealthProviderDto[] prepareHealthProviders() {
        return prepareHealthProviders(false, 0);
    }

    private HealthProviderDto[] prepareHealthProviders(boolean isMerged, int current) {
        final String databaseName = createDatabase().getName();

        final HealthProviderDto healthProviderDto = new HealthProviderDto();
        healthProviderDto.setCurrent(isMerged || current == 0);
        healthProviderDto.setProviderId(resident.getFacility().getId());
        healthProviderDto.setProviderName(databaseName + " : test org");
        healthProviderDto.setResidentId(residentId);
        final HealthProviderDto healthProviderDto2 = new HealthProviderDto();
        healthProviderDto2.setCurrent(isMerged || current == 1);
        healthProviderDto2.setProviderId(resident.getFacility().getId());
        healthProviderDto2.setProviderName(databaseName + " : test org");
        healthProviderDto2.setResidentId(residentId2);
        final HealthProviderDto healthProviderDto3 = new HealthProviderDto();
        healthProviderDto3.setCurrent(isMerged || current == 2);
        healthProviderDto3.setProviderId(resident.getFacility().getId());
        healthProviderDto3.setProviderName(databaseName + " : test org");
        healthProviderDto3.setResidentId(residentId3);

        return new HealthProviderDto[] {healthProviderDto, healthProviderDto2, healthProviderDto3};
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme