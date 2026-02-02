package com.scnsoft.eldermark.test.integration.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.MpiMergedResidents;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.scnsoft.eldermark.test.TestApplicationH2Config;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * NOTE: Changes made by {@link DatabaseSetup} annotation are not rolled back after test. Even in {@link Transactional} test cases.
 * @author phomal
 * Created on 4/26/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationH2Config.class })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TransactionConfiguration(defaultRollback = true)
public class ResidentDaoIT {

    @Autowired
    private ResidentDao residentDao;

    @Test
    @DatabaseSetup("/datasets/import.xml")
    public void aaInitializeDB() {
        // this test should be executed once before other tests
    }

    @Test
    @DatabaseSetup({"/datasets/charles-xavier.xml"})
    public void getResidentsByGender() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.MALE);
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue(resident.getGender() == null || isMale(resident.getGender()));
        }
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsByGender2() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.FEMALE);
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        assertFalse(CollectionUtils.isEmpty(residents2));
        for (Resident resident : residents) {
            assertTrue(resident.getGender() == null || isFemale(resident.getGender()));
        }
        for (Resident resident : residents2) {
            assertTrue(resident.getGender() == null || isFemale(resident.getGender()));
        }
    }

    private static boolean isMale(CcdCode gender) {
        return "2.16.840.1.113883.5.1".equals(gender.getCodeSystem()) && "M".equals(gender.getCode());
    }

    private static boolean isFemale(CcdCode gender) {
        return "2.16.840.1.113883.5.1".equals(gender.getCodeSystem()) && "F".equals(gender.getCode());
    }

    @Test
    @DatabaseSetup({"/datasets/charles-xavier.xml"})
    public void getResidentsByName() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setFirstName("Charles");
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue("Charles".equals(resident.getFirstName()));
        }
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsByName2() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setFirstName("Charles");
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue("Charles".equals(resident.getFirstName()));
        }
        assertEquals(1, residents.size());
        assertFalse(CollectionUtils.isEmpty(residents2));
        for (Resident resident : residents2) {
            assertTrue("Charles".equals(resident.getFirstName()));
        }
        assertEquals(1, residents2.size());
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void testNoResults() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setFirstName("The one whose name shall not be spoken");
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter);

        assertTrue(CollectionUtils.isEmpty(residents));
        assertTrue(CollectionUtils.isEmpty(residents2));
    }

    @Test
    @DatabaseSetup({"/datasets/charles-xavier.xml"})
    public void getResidentsBySsn() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        String correctSsn = "123456710";
        filter.setSsn(correctSsn);
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue(correctSsn.equals(resident.getSocialSecurity()));
        }
        assertFalse(CollectionUtils.isEmpty(residents2));
        for (Resident resident : residents2) {
            assertTrue(correctSsn.equals(resident.getSocialSecurity()));
        }
    }

    @Test
    @DatabaseSetup({"/datasets/charles-xavier.xml"})
    public void getResidentsByIncorrectSsn() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setSsn("1234567");
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter);
        filter.setSsn("1234567l0");
        List<Resident> residents3 = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ALL);
        List<Resident> residents4 = residentDao.getResidents(filter);

        assertTrue(CollectionUtils.isEmpty(residents));
        assertTrue(CollectionUtils.isEmpty(residents2));
        assertTrue(CollectionUtils.isEmpty(residents3));
        assertTrue(CollectionUtils.isEmpty(residents4));
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsBySsnLast4Digits() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setSsn("6379");
        filter.setLastFourDigitsOfSsn("6379");
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        assertTrue("637916379".equals(residents.get(0).getSocialSecurity()));
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsFirstPage() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.FEMALE);
        Pageable pageRequest = new PageRequest(0, 10);

        List<Resident> residents = residentDao.getResidents(filter, pageRequest);

        assertEquals(10, residents.size());
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsSorted() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMode(SearchMode.MATCH_ALL);
        Sort.Direction sortDirection = Sort.Direction.DESC;

        Pageable pageRequest = new PageRequest(0, 40, sortDirection, "lastName");
        List<Resident> residents = residentDao.getResidents(filter, pageRequest);
        filter.setMode(SearchMode.MATCH_ANY);
        List<Resident> residents2 = residentDao.getResidents(filter, pageRequest);

        List<Resident> expected = sort(residents, "lastName", sortDirection);
        assertEquals(25, residents.size());
        assertEquals(expected, residents);
        assertEquals(25, residents2.size());
        assertEquals(expected, residents2);
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedByLastName() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "lastName";
        String propertyName = "lastName";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedByFirstName() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "firstName";
        String propertyName = "firstName";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedBySSN() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "ssn";
        String propertyName = "socialSecurity";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedByDateOfBirth() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "dateOfBirth";
        String propertyName = "birthDate";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    @Test
    @Ignore
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedByOrganization() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "organizationName";
        String propertyName = "facility";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    @Test
    @Ignore
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsPagedAndSortedByDatabase() throws Exception {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String columnName = "databaseName";
        String propertyName = "database";

        loadDataAndAssertOrder(sortDirection, columnName, propertyName);
    }

    private void loadDataAndAssertOrder(Sort.Direction sortDirection, String columnName, String propertyName) {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMode(SearchMode.MATCH_ALL);
        Pageable pageRequest = new PageRequest(0, 10, sortDirection, columnName);
        List<Resident> residents = residentDao.getResidents(filter, pageRequest);

        assertEquals(10, residents.size());
        List<Resident> expected = sort(residents, propertyName, sortDirection);
        assertEquals(expected, residents);

        pageRequest = new PageRequest(1, 10, sortDirection, columnName);
        residents = residentDao.getResidents(filter, pageRequest);

        assertEquals(10, residents.size());
        expected = sort(residents, propertyName, sortDirection);
        assertEquals(expected, residents);

        pageRequest = new PageRequest(2, 10, sortDirection, columnName);
        residents = residentDao.getResidents(filter, pageRequest);

        assertEquals(5, residents.size());
        expected = sort(residents, propertyName, sortDirection);
        assertEquals(expected, residents);
    }

    private static List<Resident> sort(List<Resident> residents, String propertyName, final Sort.Direction direction) {
        List<Resident> sorted = new LinkedList<Resident>(residents);
        if (Sort.Direction.ASC.equals(direction)) {
            Collections.sort(sorted, new BeanComparator(propertyName));
        } else {
            Collections.sort(sorted, new BeanComparator(propertyName, new ReverseComparator(new ComparableComparator())));
        }

        return sorted;
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentCount() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.FEMALE);
        filter.setMode(SearchMode.MATCH_ALL);

        long residentCount = residentDao.getResidentCount(filter);

        assertEquals(23, residentCount);
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentCount2() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.MALE);
        filter.setMode(SearchMode.MATCH_ALL);

        long residentCount = residentDao.getResidentCount(filter);

        assertEquals(4, residentCount);
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void compareResidentCountWithSize() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.FEMALE);
        filter.setMode(SearchMode.MATCH_ALL);

        List<Resident> residents = residentDao.getResidents(filter);
        long residentCount = residentDao.getResidentCount(filter);

        assertEquals(residentCount, (long) residents.size());
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void compareResidentCountWithSizeSorted() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setGender(Gender.FEMALE);
        filter.setMode(SearchMode.MATCH_ALL);
        // request more residents that we have in test dataset
        Pageable pageRequest = new PageRequest(0, 40, Sort.Direction.DESC, "firstName");

        List<Resident> residents = residentDao.getResidents(filter, pageRequest);
        long residentCount = residentDao.getResidentCount(filter);

        assertEquals(residentCount, (long) residents.size());
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void testSearchMaybeMatched() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMatchStatus(MatchStatus.MAYBE_MATCHED);
        filter.setMergeStatus(MergeStatus.NOT_MERGED);
        filter.setMode(SearchMode.MATCH_ANY_LIKE);

        Pageable pageRequest = new PageRequest(0, 10, Sort.Direction.ASC, "firstName");
        List<Resident> residents = residentDao.getResidents(filter, pageRequest);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue(hasMaybeMatchedResidents(resident));
        }
        assertEquals(2, residents.size());
        List<Resident> expected = sort(residents, "firstName", Sort.Direction.ASC);
        assertEquals(expected, residents);
    }

    @Test
    @Transactional
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void testSearchMaybeMatchedByQuery() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMatchStatus(MatchStatus.MAYBE_MATCHED);
        filter.setMergeStatus(MergeStatus.NOT_MERGED);
        filter.setMode(SearchMode.MATCH_ANY_LIKE);
        String query = "Smi";
        filter.setFirstName(query);
        filter.setLastName(query);
        filter.setSsn(query);
        filter.setCommunity(query);
        filter.setProviderOrganization(query);

        Pageable pageRequest = new PageRequest(0, 10, Sort.Direction.ASC, "firstName");
        List<Resident> residents = residentDao.getResidents(filter, pageRequest);

        assertEquals(1, residents.size());
        Resident resident = residents.get(0);
        assertTrue(hasMaybeMatchedResidents(resident));
        assertEquals("Anna", resident.getFirstName());
        assertEquals("Smith", resident.getLastName());
        assertEquals(377L, (long)resident.getId());
    }

    private boolean hasMaybeMatchedResidents(Resident resident) {
        if (!CollectionUtils.isEmpty(resident.getSecondaryResidents())) {
            for (MpiMergedResidents mpiMergedResidents : resident.getSecondaryResidents()) {
                if (mpiMergedResidents.isProbablyMatched()) {
                    return true;
                }
            }
        }
        if (!CollectionUtils.isEmpty(resident.getMainResidents())) {
            for (MpiMergedResidents mpiMergedResidents : resident.getMainResidents()) {
                if (mpiMergedResidents.isProbablyMatched()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void testPhrLocalSearch() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMode(SearchMode.MATCH_ALL);
        filter.setFirstName("Craig");
        filter.setLastName("Patnode");
        filter.setLastFourDigitsOfSsn("6379");
        filter.setSsn("6379");
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        Date dateOfBirth = dateFormat.parse("12/19/1969");
        filter.setDateOfBirth(dateOfBirth);
        filter.setGender(Gender.MALE);

        Pageable pageRequest = new PageRequest(0, 10);
        List<Resident> residents = residentDao.getResidents(filter, pageRequest);

        assertFalse(CollectionUtils.isEmpty(residents));
        for (Resident resident : residents) {
            assertTrue("Craig".equals(resident.getFirstName()));
            assertTrue("Patnode".equals(resident.getLastName()));
            assertTrue("6379".equals(resident.getSsnLastFourDigits()));
            assertTrue("637916379".equals(resident.getSocialSecurity()));
            assertTrue(dateOfBirth.equals(resident.getBirthDate()));
            assertTrue(isMale(resident.getGender()));
        }
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void getResidentsByDateOfBirth() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMode(SearchMode.MATCH_ANY);
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        Date dateOfBirth = dateFormat.parse("10/10/1990");
        filter.setDateOfBirth(dateOfBirth);

        List<Resident> residents = residentDao.getResidents(filter);
        filter.setMode(SearchMode.MATCH_ALL);
        List<Resident> residents2 = residentDao.getResidents(filter);

        assertFalse(CollectionUtils.isEmpty(residents));
        assertFalse(CollectionUtils.isEmpty(residents2));
        Resident resident = residents.get(0);
        Resident resident2 = residents2.get(0);
        //assertEquals(resident, resident2);
        assertIsMaryLee(dateOfBirth, resident);
        assertIsMaryLee(dateOfBirth, resident2);
    }

    private void assertIsMaryLee(Date dateOfBirth, Resident resident) {
        assertEquals(dateOfBirth, resident.getBirthDate());
        assertEquals("Mary", resident.getFirstName());
        assertEquals("Lee", resident.getLastName());
        assertEquals(297L, (long)resident.getId());
    }

    @Test
    @DatabaseSetup({"/datasets/residents-with-matches.xml"})
    public void testSearchMatchAnyOfTheFollowing() throws Exception {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        filter.setMode(SearchMode.MATCH_ANY);
        filter.setFirstName("Smuth");   // No residents with such first name
        filter.setLastName("Smuth");    // Last name of Anna Smuth
        filter.setSsn("637916379");     // SSN of Craig Patnode
        filter.setCity("North");        // Jon Snow from North is not accessible for discovery. Is it a bug?
        filter.setPostalCode("55343");  // Zip code of Samuel Clausson, Craig Patnode, and Charles Xavier

        List<Resident> residents = residentDao.getResidents(filter);

        assertEquals(4, residents.size());
    }

}