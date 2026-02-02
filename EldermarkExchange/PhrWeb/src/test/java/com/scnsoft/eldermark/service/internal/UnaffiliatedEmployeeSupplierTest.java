package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnaffiliatedEmployeeSupplierTest {

    // Shared test data
    final String phone = TestDataGenerator.randomPhone();
    final String email = TestDataGenerator.randomEmail();
    final String firstName = TestDataGenerator.randomName();
    final String lastName = TestDataGenerator.randomName();

    @Mock
    private EmployeeService employeeService;
    @Mock
    private DatabasesService databasesService;

    @InjectMocks
    private EmployeeSupplier unaffiliatedEmployeeSupplier = new UnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);

    @Test
    public void testGetEmployee() {
        // Expected objects
        final String emailNormalized = Normalizer.normalizeEmail(email);
        final String phoneNormalized = Normalizer.normalizePhone(phone);
        final Employee existingEmployee = new Employee();
        existingEmployee.setId(TestDataGenerator.randomId());
        existingEmployee.setLoginName(email);
        existingEmployee.setStatus(EmployeeStatus.ACTIVE);

        final Long databaseId = TestDataGenerator.randomId();
        final Database unaffiliated = new Database();
        unaffiliated.setId(databaseId);

        final Pageable pageable = new PageRequest(0, 1);

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(unaffiliated);
        when(employeeService.getEmployeesByData(Collections.singletonList(databaseId), emailNormalized, phoneNormalized, firstName, lastName, pageable))
                .thenReturn(Collections.singletonList(existingEmployee));

        // Execute the method being tested
        Employee result = unaffiliatedEmployeeSupplier.getEmployee();
        Employee result2 = unaffiliatedEmployeeSupplier.getEmployee();

        // Validation
        assertNotNull(result);
        assertEquals(existingEmployee, result);
        assertEquals(existingEmployee, result2);
    }

    @Test
    public void testGetEmployeeReturnsNull() {
        final String emailNormalized = Normalizer.normalizeEmail(email);
        final String phoneNormalized = Normalizer.normalizePhone(phone);

        final Long databaseId = TestDataGenerator.randomId();
        final Database unaffiliated = new Database();
        unaffiliated.setId(databaseId);

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(unaffiliated);
        when(employeeService.getEmployeesByData(Collections.singletonList(databaseId), emailNormalized, phoneNormalized, firstName, lastName, null))
                .thenReturn(Collections.<Employee>emptyList());

        // Execute the method being tested
        Employee result = unaffiliatedEmployeeSupplier.getEmployee();

        // Validation
        assertNull(result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme