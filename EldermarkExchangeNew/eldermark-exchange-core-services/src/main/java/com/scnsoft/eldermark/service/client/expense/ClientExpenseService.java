package com.scnsoft.eldermark.service.client.expense;

import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientExpenseService {

    ClientExpense save(ClientExpense entity);

    Optional<ClientExpense> findById(Long expenseId);

    <P> Optional<P> findById(Long expenseId, Class<P> projectionClass);

    <P> Page<P> find(Long clientId, Pageable pageable, Class<P> projectionClass);

    Long count(Long clientId);

    Long getTotalCost(Long clientId);
}
