package com.scnsoft.eldermark.beans.reports.model.expenses;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class ClientExpensesReport extends Report {

    private List<ClientExpensesReportItem> items;

    public List<ClientExpensesReportItem> getItems() {
        return items;
    }

    public void setItems(List<ClientExpensesReportItem> items) {
        this.items = items;
    }
}
