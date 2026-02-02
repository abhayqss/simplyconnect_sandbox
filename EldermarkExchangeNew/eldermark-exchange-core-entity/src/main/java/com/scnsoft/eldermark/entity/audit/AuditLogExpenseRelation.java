package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.client.expense.ClientExpense;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Expense")
public class AuditLogExpenseRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "expense_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClientExpense clientExpense;

    @Column(name = "expense_id", nullable = false)
    private Long expenseId;

    public ClientExpense getClientExpense() {
        return clientExpense;
    }

    public void setClientExpense(ClientExpense clientExpense) {
        this.clientExpense = clientExpense;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(expenseId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.EXPENSE;
    }
}
