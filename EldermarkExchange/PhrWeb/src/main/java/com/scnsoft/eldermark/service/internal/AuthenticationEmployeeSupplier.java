package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.UsernameBuilder;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.web.security.ExchangeAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Random;

/**
 * Search employee by company ID and login. Verify authentication by password.
 * <br/><br/>
 * A bean prototype that is instantiated and initialized by {@link EmployeeSupplierFactory#getAuthenticationEmployeeSupplier(String, String, char[])}.
 *
 * @author phomal
 * Created on 7/27/2017.
 */
class AuthenticationEmployeeSupplier extends MemoizingEmployeeSupplier {

    private final String companyId;
    private final String login;
    // String objects are immutable and therefore unsuitable for storing security sensitive information such as passwords.
    private final char[] password;

    @Autowired
    private ExchangeAuthenticationManager authenticationManager;

    public AuthenticationEmployeeSupplier(String companyId, String login, char[] password) {
        if (companyId == null || login == null || password == null) {
            throw new IllegalStateException("not initialized");
        }

        this.companyId = companyId;
        this.login = login;
        this.password = password;
    }

    @Override
    public Employee get() {
        final String username = UsernameBuilder.anUsername()
                .withCompanyId(companyId)
                .withLogin(login)
                .build();
        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(username, new String(password));
            Authentication authentication = authenticationManager.authenticate(authToken);
            return ((ExchangeUserDetails) authentication.getPrincipal()).getEmployee();
        } finally {
            reset();
        }
    }

    private void reset() {
        Random r = new Random();
        int start = ' ';
        int end = 'z' + 1;

        for (int i = 0; i < password.length; ++i) {
            password[i] = (char)(r.nextInt(end - start) + start);
        }
    }
}
