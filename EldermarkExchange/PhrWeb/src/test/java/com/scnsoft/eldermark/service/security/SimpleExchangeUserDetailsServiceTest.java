package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.UsernameBuilder;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.BaseServiceTest;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.shared.service.security.SimpleExchangeUserDetailsService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
public class SimpleExchangeUserDetailsServiceTest extends BaseServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @InjectMocks
    private SimpleExchangeUserDetailsService simpleExchangeUserDetailsService;


    @Test
    public void testLoadUserByUsername() {
        // Expected objects
        final String companyId = "Test";
        final String email = TestDataGenerator.randomEmail();
        final Employee existingEmployee = createEmployee(email, null, null, null);
        existingEmployee.setCompany(companyId);
        existingEmployee.setPassword("password");

        final String username = UsernameBuilder.anUsername()
                .withCompanyId(companyId)
                .withLogin(email)
                .build();

        // Mockito expectations
        when(employeeService.getActiveEmployee(email, companyId)).thenReturn(existingEmployee);
        when(employeeService.getActiveEmployee(not(eq(email)), not(eq(companyId)))).thenThrow(UsernameNotFoundException.class);

        // Execute the method being tested
        UserDetails result = simpleExchangeUserDetailsService.loadUserByUsername(username);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(ExchangeUserDetails.class));
        assertEquals(existingEmployee, ((ExchangeUserDetails) result).getEmployee());

        verify(employeeService).getActiveEmployee(email, companyId);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameThrowsUsernameNotFound() {
        // Expected objects
        final String companyId = "Test";
        final String email = TestDataGenerator.randomEmail();
        final String username = UsernameBuilder.anUsername()
                .withCompanyId(companyId)
                .withLogin(email)
                .build();

        // Mockito expectations
        when(employeeService.getActiveEmployee(anyString(), anyString())).thenThrow(UsernameNotFoundException.class);

        // Execute the method being tested
        simpleExchangeUserDetailsService.loadUserByUsername(username);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme