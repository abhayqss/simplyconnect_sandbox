package com.scnsoft.eldermark.facade.client.expense;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseDto;
import com.scnsoft.eldermark.dto.client.expense.ClientExpenseListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientExpenseFacade {

    Long create(ClientExpenseDto expense);

    Page<ClientExpenseListItemDto> find(Long clientId, Pageable pageable);

    Long count(Long clientId);

    ClientExpenseDto findById(Long expenseId);

    Long getTotalCost(Long clientId);

    boolean canViewList(Long clientId);

    boolean canAdd(Long clientId);
}
