package com.scnsoft.eldermark.service.client.expense;

import com.scnsoft.eldermark.dao.ClientExpenseDao;
import com.scnsoft.eldermark.dao.specification.ClientExpenseSpecificationGenerator;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import com.scnsoft.eldermark.entity.client.expense.CostAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientExpenseServiceImpl implements ClientExpenseService {

    @Autowired
    private ClientExpenseDao clientExpenseDao;

    @Autowired
    private ClientExpenseSpecificationGenerator clientExpenseSpecificationGenerator;

    @Override
    public ClientExpense save(ClientExpense entity) {
        return clientExpenseDao.save(entity);
    }

    @Override
    public Optional<ClientExpense> findById(Long expenseId) {
        return clientExpenseDao.findById(expenseId);
    }

    @Override
    public <P> Optional<P> findById(Long expenseId, Class<P> projectionClass) {
        return clientExpenseDao.findById(expenseId, projectionClass);
    }

    @Override
    public <P> Page<P> find(Long clientId, Pageable pageable, Class<P> projectionClass) {
        return clientExpenseDao.findAll(
                clientExpenseSpecificationGenerator.byClientId(clientId),
                projectionClass,
                pageable
        );
    }

    @Override
    public Long count(Long clientId) {
        return clientExpenseDao.count(clientExpenseSpecificationGenerator.byClientId(clientId));
    }

    @Override
    public Long getTotalCost(Long clientId) {
        return clientExpenseDao.findAll(
                        clientExpenseSpecificationGenerator.byClientId(clientId),
                        CostAware.class
                ).stream()
                .mapToLong(CostAware::getCost)
                .sum();
    }
}
