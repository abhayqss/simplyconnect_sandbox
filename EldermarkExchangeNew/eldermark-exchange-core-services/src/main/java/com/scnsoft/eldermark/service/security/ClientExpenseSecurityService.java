package com.scnsoft.eldermark.service.security;

public interface ClientExpenseSecurityService {

    boolean canAdd(Long clientId);

    boolean canViewList(Long clientId);

    boolean canView(Long expenseId);
}
