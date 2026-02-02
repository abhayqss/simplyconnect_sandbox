package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientExpenseDao extends AppJpaRepository<ClientExpense, Long> {
}
