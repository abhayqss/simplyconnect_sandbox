package com.scnsoft.eldermark.exchange.services.employees;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface PasswordProvider {
    String getPassword(DatabaseInfo database);
}
