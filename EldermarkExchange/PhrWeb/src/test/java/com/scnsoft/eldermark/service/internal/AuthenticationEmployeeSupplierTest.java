package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.BaseServiceTest;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.security.ExchangeAuthenticationManager;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
public class AuthenticationEmployeeSupplierTest extends BaseServiceTest {

    // Shared test data
    final String companyId = "Test";
    final String email = TestDataGenerator.randomEmail();
    final String password = "password";

    @Mock
    private ExchangeAuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

    @InjectMocks
    private AuthenticationEmployeeSupplier authenticationEmployeeSupplier = new AuthenticationEmployeeSupplier(companyId, email, password.toCharArray());

    @Test
    public void testGet() {
        // Expected objects
        final Employee existingEmployee = createEmployee(email, null, null, null);
        existingEmployee.setCompany(companyId);
        existingEmployee.setPassword(password);

        final ExchangeUserDetails expectedExchangeUserDetails = new ExchangeUserDetails(existingEmployee, Collections.<SimpleGrantedAuthority>emptyList(),
                null, null, null, null, true);

        // Mockito expectations
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(expectedExchangeUserDetails);

        // Execute the method being tested
        final Employee result = authenticationEmployeeSupplier.getEmployee();
        final Employee result2 = authenticationEmployeeSupplier.getEmployee();

        // Validation
        assertEquals(existingEmployee, result);
        assertEquals(existingEmployee, result2);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme