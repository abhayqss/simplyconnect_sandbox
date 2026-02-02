package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseType;
import com.scnsoft.eldermark.entity.client.expense.ExpenseTypeAware;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import com.scnsoft.eldermark.service.client.expense.ClientExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogExpenseConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> expenseActivitiesWithNote = List.of(
            AuditLogActivity.EXPENSE_VIEW,
            AuditLogActivity.EXPENSE_CREATE
    );

    @Autowired
    private ClientExpenseService clientExpenseService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (expenseActivitiesWithNote.contains(activity)) {
                return List.of("Expense: " + clientExpenseService.findById(relatedId, ExpenseTypeAware.class)
                        .map(ExpenseTypeAware::getType)
                        .map(ClientExpenseType::getDisplayName)
                        .map(Collections::singletonList)
                        .orElseGet(List::of));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.EXPENSE;
    }
}
