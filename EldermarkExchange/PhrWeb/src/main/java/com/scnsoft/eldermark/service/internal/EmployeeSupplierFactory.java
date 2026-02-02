package com.scnsoft.eldermark.service.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * A factory for simple switching between {@link EmployeeSupplier} implementations.
 *
 * @author phomal
 * Created on 7/27/2017.
 */
@Component
public class EmployeeSupplierFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    /**
     * @see AuthenticationEmployeeSupplier supplier description
     */
    public EmployeeSupplier getAuthenticationEmployeeSupplier(String companyId, String login, char[] password) {
        AuthenticationEmployeeSupplier employeeSupplier = new AuthenticationEmployeeSupplier(companyId, login, password);
        beanFactory.autowireBean(employeeSupplier);
        return employeeSupplier;
    }

    /**
     * @see LegacyLookupEmployeeSupplier supplier description
     */
    public EmployeeSupplier getEmployeeSupplier(String login, String phone, String firstName, String lastName) {
        LegacyLookupEmployeeSupplier employeeSupplier = new LegacyLookupEmployeeSupplier(login, phone, firstName, lastName);
        beanFactory.autowireBean(employeeSupplier);
        return employeeSupplier;
    }

    /**
     * @see LookupEmployeeSupplier supplier description
     */
    public EmployeeSupplier getEmployeeSupplier(Collection<Long> healthProviderIds, String login, String phone, String firstName, String lastName) {
        LookupEmployeeSupplier employeeSupplier = new LookupEmployeeSupplier(healthProviderIds, login, phone, firstName, lastName);
        beanFactory.autowireBean(employeeSupplier);
        return employeeSupplier;
    }

    /**
     * @see UnaffiliatedEmployeeSupplier supplier description
     */
    public EmployeeSupplier getUnaffiliatedEmployeeSupplier(String email, String phone, String firstName, String lastName) {
        UnaffiliatedEmployeeSupplier employeeSupplier = new UnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);
        beanFactory.autowireBean(employeeSupplier);
        return employeeSupplier;
    }

    /**
     * @see UnaffiliatedEmployeeSupplier supplier description
     */
    public EmployeeSupplier getUnaffiliatedEmployeeSupplier(String login) {
        UnaffiliatedEmployeeSupplier employeeSupplier = new UnaffiliatedEmployeeSupplier(login);
        beanFactory.autowireBean(employeeSupplier);
        return employeeSupplier;
    }

}
