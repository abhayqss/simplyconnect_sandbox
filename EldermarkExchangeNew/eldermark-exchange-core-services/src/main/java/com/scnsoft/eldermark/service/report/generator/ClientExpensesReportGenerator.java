package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.expenses.ClientExpensesReport;
import com.scnsoft.eldermark.beans.reports.model.expenses.ClientExpensesReportClientItem;
import com.scnsoft.eldermark.beans.reports.model.expenses.ClientExpensesReportExpenseItem;
import com.scnsoft.eldermark.beans.reports.model.expenses.ClientExpensesReportItem;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientExpenseDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientExpenseSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseReportDetailsAware;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense_;
import com.scnsoft.eldermark.entity.client.history.ClientHistoryStatusAware;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientExpensesReportGenerator extends DefaultReportGenerator<ClientExpensesReport> {

    private final static Sort CLIENT_EXPENSE_SORT = Sort.by(
            Sort.Order.asc(String.join(".", ClientExpense_.CLIENT, Client_.COMMUNITY, Community_.NAME)),
            Sort.Order.asc(String.join(".", ClientExpense_.CLIENT, Client_.COMMUNITY, Community_.ID)),
            Sort.Order.asc(String.join(".", ClientExpense_.CLIENT, Client_.FIRST_NAME)),
            Sort.Order.asc(String.join(".", ClientExpense_.CLIENT, Client_.LAST_NAME)),
            Sort.Order.desc(ClientExpense_.DATE)
    );

    @Autowired
    private ClientExpenseDao clientExpenseDao;

    @Autowired
    private ClientHistoryDao clientHistoryDao;

    @Autowired
    private ClientExpenseSpecificationGenerator clientExpenseSpecificationGenerator;

    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @Transactional(readOnly = true)
    public ClientExpensesReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ClientExpensesReport();
        populateReportingCriteriaFields(filter, report);
        populateClientExpenses(filter, report);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.CLIENT_EXPENSES;
    }

    private void populateClientExpenses(InternalReportFilter filter, ClientExpensesReport report) {
        var expenses = clientExpenseDao.findAll(
                clientExpenseSpecificationGenerator.hasAccess(permissionFilterService.createPermissionFilterForCurrentUser())
                        .and(clientExpenseSpecificationGenerator.byClientCommunities(filter.getAccessibleCommunityIdsAndNames()))
                        .and(clientExpenseSpecificationGenerator.isClientActiveInPeriod(filter.getInstantFrom(), filter.getInstantTo())),
                ClientExpenseReportDetailsAware.class,
                CLIENT_EXPENSE_SORT
        );

        report.setItems(createReportItems(filter, expenses));
    }

    private List<ClientExpensesReportItem> createReportItems(InternalReportFilter filter,
                                                             List<ClientExpenseReportDetailsAware> expenses) {
        var items = new LinkedList<ClientExpensesReportItem>();

        var clientIds = expenses.stream()
                .map(ClientIdAware::getClientId)
                .collect(Collectors.toSet());

        var clientStatuses = clientHistoryDao.findAll(
                        clientHistorySpecificationGenerator.byClientIdIn(clientIds)
                                .and(clientHistorySpecificationGenerator.firstByUpdatedDateTimeAfter(filter.getInstantTo())),
                        ClientHistoryStatusAware.class
                ).stream()
                .collect(Collectors.toMap(ClientIdAware::getClientId, ClientHistoryStatusAware::getActive));

        expenses.forEach(expense -> {
            var item = items.isEmpty() ? null : items.getLast();
            if (item == null || !Objects.equals(item.getCommunityName(), expense.getClientCommunityName())) {
                item = new ClientExpensesReportItem();
                item.setCommunityName(expense.getClientCommunityName());
                item.setClients(new LinkedList<>());
                items.add(item);
            }

            var clientItem = item.getClients().isEmpty() ? null : item.getClients().getLast();
            if (clientItem == null || !Objects.equals(clientItem.getClientId(), expense.getClientId())) {
                clientItem = new ClientExpensesReportClientItem();
                clientItem.setClientId(expense.getClientId());
                clientItem.setIsClientActive(
                        Optional.ofNullable(clientStatuses.get(expense.getClientId()))
                                .orElse(expense.getClientActive())
                );
                clientItem.setClientName(expense.getClientFullName());
                clientItem.setExpenses(new LinkedList<>());
                item.getClients().add(clientItem);
            }

            var expenseItem = new ClientExpensesReportExpenseItem();
            expenseItem.setAuthor(expense.getAuthorFullName());
            expenseItem.setComment(expense.getComment());
            expenseItem.setCost(expense.getCost());
            expenseItem.setCumulativeCost(expense.getCumulativeCost());
            expenseItem.setDate(expense.getDate());
            expenseItem.setReportedDate(expense.getReportedDate());
            expenseItem.setType(expense.getType());

            clientItem.getExpenses().add(expenseItem);
        });

        return items;
    }

}
