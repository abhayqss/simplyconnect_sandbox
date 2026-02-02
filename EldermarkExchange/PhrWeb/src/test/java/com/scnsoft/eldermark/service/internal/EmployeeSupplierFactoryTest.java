package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author phomal
 * Created on 7/31/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployeeSupplierFactoryTest {

    @Mock
    private AutowireCapableBeanFactory beanFactory;

    @InjectMocks
    private EmployeeSupplierFactory employeeSupplierFactory;

    @Test
    public void testGetAuthenticationEmployeeSupplier() {
        final String email = TestDataGenerator.randomEmail();

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getAuthenticationEmployeeSupplier("companyId", email, "password".toCharArray());

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(AuthenticationEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAuthenticationEmployeeSupplierThrowsIllegalStateException() {
        // Execute the method being tested
        employeeSupplierFactory.getAuthenticationEmployeeSupplier(null, null, null);
    }

    @Test
    public void testGetEmployeeSupplier() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getEmployeeSupplier(email, phone, firstName, lastName);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(LegacyLookupEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test
    public void testGetEmployeeSupplierAnonymous() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getEmployeeSupplier(email, phone, null, null);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(LegacyLookupEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetEmployeeSupplierThrowsIllegalStateException() {
        // Execute the method being tested
        employeeSupplierFactory.getEmployeeSupplier(null, null, null, null);
    }

    @Test
    public void testGetEmployeeSupplier2() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final List<Long> ids = Collections.singletonList(TestDataGenerator.randomId());

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getEmployeeSupplier(ids, email, phone, firstName, lastName);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(LookupEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test
    public void testGetEmployeeSupplier2Anonymous() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();
        final List<Long> ids = Collections.singletonList(TestDataGenerator.randomId());

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getEmployeeSupplier(ids, email, phone, null, null);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(LookupEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetEmployeeSupplier2ThrowsIllegalStateException() {
        // Execute the method being tested
        employeeSupplierFactory.getEmployeeSupplier(null, null, null, null, null);
    }

    @Test
    public void testGetUnaffiliatedEmployeeSupplier() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(UnaffiliatedEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test
    public void testGetUnaffiliatedEmployeeSupplierAnonymous() {
        final String phone = TestDataGenerator.randomPhone();
        final String email = TestDataGenerator.randomEmail();

        // Execute the method being tested
        EmployeeSupplier result = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email, phone, null, null);

        // Validation
        assertNotNull(result);
        assertThat(result, instanceOf(UnaffiliatedEmployeeSupplier.class));
        verify(beanFactory).autowireBean(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetUnaffiliatedEmployeeSupplierThrowsIllegalStateException() {
        // Execute the method being tested
        employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(null, null, null, null);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme