package com.scnsoft.eldermark.facade.client.expense;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseDto;
import com.scnsoft.eldermark.dto.client.expense.ClientExpenseListItemDto;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseListItemDetailsAware;
import com.scnsoft.eldermark.service.client.expense.ClientExpenseService;
import com.scnsoft.eldermark.service.security.ClientExpenseSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ClientExpenseFacadeImpl implements ClientExpenseFacade {

    @Autowired
    private ClientExpenseService clientExpenseService;

    @Autowired
    private Converter<ClientExpenseDto, ClientExpense> expenseEntityConverter;

    @Autowired
    private Converter<ClientExpense, ClientExpenseDto> expenseDtoConverter;

    @Autowired
    private Converter<ClientExpenseListItemDetailsAware, ClientExpenseListItemDto> expenseListItemDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientExpenseSecurityService clientExpenseSecurityService;

    @Override
    @Transactional
    @PreAuthorize("@clientExpenseSecurityService.canAdd(#expense.clientId)")
    public Long create(@Valid ClientExpenseDto expense) {
        var entity = Objects.requireNonNull(expenseEntityConverter.convert(expense));

        var currentUser = loggedUserService.getCurrentEmployee();
        entity.setAuthorId(currentUser.getId());
        entity.setAuthor(currentUser);
        entity.setReportedDate(Instant.now());

        return clientExpenseService.save(entity).getId();
    }

    @Override
    @PreAuthorize("@clientExpenseSecurityService.canViewList(#clientId)")
    public Page<ClientExpenseListItemDto> find(Long clientId, Pageable pageable) {
        return clientExpenseService.find(
                        clientId,
                        PaginationUtils.applyEntitySort(pageable, ClientExpenseListItemDto.class),
                        ClientExpenseListItemDetailsAware.class
                )
                .map(expenseListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@clientExpenseSecurityService.canViewList(#clientId)")
    public Long count(Long clientId) {
        return clientExpenseService.count(clientId);
    }

    @Override
    @PreAuthorize("@clientExpenseSecurityService.canView(#expenseId)")
    public ClientExpenseDto findById(Long expenseId) {
        return clientExpenseService.findById(expenseId)
                .map(expenseDtoConverter::convert)
                .orElseThrow();
    }

    @Override
    @PreAuthorize("@clientExpenseSecurityService.canViewList(#clientId)")
    public Long getTotalCost(Long clientId) {
        return clientExpenseService.getTotalCost(clientId);
    }

    @Override
    public boolean canViewList(Long clientId) {
        return clientExpenseSecurityService.canViewList(clientId);
    }

    @Override
    public boolean canAdd(Long clientId) {
        return clientExpenseSecurityService.canAdd(clientId);
    }
}
