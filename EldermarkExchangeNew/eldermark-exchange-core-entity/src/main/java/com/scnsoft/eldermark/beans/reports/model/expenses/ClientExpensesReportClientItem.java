package com.scnsoft.eldermark.beans.reports.model.expenses;

import java.util.List;

public class ClientExpensesReportClientItem {

    private Long clientId;
    private Boolean isClientActive;
    private String clientName;
    private List<ClientExpensesReportExpenseItem> expenses;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getIsClientActive() {
        return isClientActive;
    }

    public void setIsClientActive(Boolean isClientActive) {
        this.isClientActive = isClientActive;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<ClientExpensesReportExpenseItem> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ClientExpensesReportExpenseItem> expenses) {
        this.expenses = expenses;
    }
}
