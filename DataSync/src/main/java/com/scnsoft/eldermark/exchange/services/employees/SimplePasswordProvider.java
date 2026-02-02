package com.scnsoft.eldermark.exchange.services.employees;

import com.scnsoft.eldermark.framework.DatabaseInfo;
import org.springframework.stereotype.Component;

@Component
public class SimplePasswordProvider implements PasswordProvider {
    @Override
    public String getPassword(DatabaseInfo database) {
        return "Show1me2the3user4password5";
    }
}
