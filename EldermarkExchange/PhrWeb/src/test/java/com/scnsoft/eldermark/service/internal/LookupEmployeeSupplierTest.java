package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LookupEmployeeSupplierTest {

    // Shared test data
    final String phone = TestDataGenerator.randomPhone();
    final String email = TestDataGenerator.randomEmail();
    final String firstName = TestDataGenerator.randomName();
    final String lastName = TestDataGenerator.randomName();
    final List<Long> ids = Collections.singletonList(TestDataGenerator.randomId());

    @Mock
    private EmployeeService employeeService;
    @Mock
    private OrganizationDao organizationDao;
    @InjectMocks
    private EmployeeSupplier lookupEmployeeSupplier = new LookupEmployeeSupplier(ids, email, phone, firstName, lastName);

    @Test
    public void testGet() {
        // Expected objects
        final String emailNormalized = Normalizer.normalizeEmail(email);
        final String phoneNormalized = Normalizer.normalizePhone(phone);
        final Employee existingEmployee = new Employee();
        existingEmployee.setId(TestDataGenerator.randomId());
        existingEmployee.setLoginName(email);
        existingEmployee.setStatus(EmployeeStatus.ACTIVE);

        List<Long> databaseIds = Collections.singletonList(TestDataGenerator.randomId());

        // Mockito expectations
        when(organizationDao.getDatabasesByOrganizationIds(ids)).thenReturn(databaseIds);
        when(employeeService.getEmployeesByData(databaseIds, emailNormalized, phoneNormalized, firstName, lastName, null))
                .thenReturn(Collections.singletonList(existingEmployee));

        // Execute the method being tested
        Employee result = lookupEmployeeSupplier.getEmployee();
        Employee result2 = lookupEmployeeSupplier.getEmployee();

        // Validation
        assertNotNull(result);
        assertEquals(existingEmployee, result);
        assertEquals(existingEmployee, result2);
    }

    @Test
    public void testGetEmployeeReturnsNull() {
        final String emailNormalized = Normalizer.normalizeEmail(email);
        final String phoneNormalized = Normalizer.normalizePhone(phone);

        // Mockito expectations
        when(employeeService.getEmployeesByData(emailNormalized, phoneNormalized, firstName, lastName)).thenReturn(Collections.<Employee>emptyList());

        // Execute the method being tested
        Employee result = lookupEmployeeSupplier.getEmployee();

        // Validation
        assertNull(result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme