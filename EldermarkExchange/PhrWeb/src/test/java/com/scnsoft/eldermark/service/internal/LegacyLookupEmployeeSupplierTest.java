package com.scnsoft.eldermark.service.internal;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LegacyLookupEmployeeSupplierTest {

    // Shared test data
    final String phone = TestDataGenerator.randomPhone();
    final String email = TestDataGenerator.randomEmail();
    final String firstName = TestDataGenerator.randomName();
    final String lastName = TestDataGenerator.randomName();

    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private EmployeeSupplier legacyLookupEmployeeSupplier = new LegacyLookupEmployeeSupplier(email, phone, firstName, lastName);

    @Test
    public void testGetEmployee() {
        // Expected objects
        final String emailNormalized = Normalizer.normalizeEmail(email);
        final String phoneNormalized = Normalizer.normalizePhone(phone);
        final Employee existingEmployee = new Employee();
        existingEmployee.setId(TestDataGenerator.randomId());
        existingEmployee.setLoginName(email);
        existingEmployee.setStatus(EmployeeStatus.ACTIVE);

        // Mockito expectations
        when(employeeService.getEmployeesByData(emailNormalized, phoneNormalized, firstName, lastName))
                .thenReturn(Collections.singletonList(existingEmployee));
        when(employeeService.getEmployeesByData(null, emailNormalized, phoneNormalized, firstName, lastName, null))
                .thenReturn(Collections.singletonList(existingEmployee));

        // Execute the method being tested
        Employee result = legacyLookupEmployeeSupplier.getEmployee();
        Employee result2 = legacyLookupEmployeeSupplier.getEmployee();

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
        Employee result = legacyLookupEmployeeSupplier.getEmployee();

        // Validation
        assertNull(result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme