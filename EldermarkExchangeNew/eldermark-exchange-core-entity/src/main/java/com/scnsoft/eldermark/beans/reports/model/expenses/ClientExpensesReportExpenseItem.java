package com.scnsoft.eldermark.beans.reports.model.expenses;

import com.scnsoft.eldermark.entity.client.expense.ClientExpenseType;

import java.time.Instant;

public class ClientExpensesReportExpenseItem {

    private ClientExpenseType type;
    private Long cost;
    private Long cumulativeCost;
    private Instant date;
    private Instant reportedDate;
    private String author;
    private String comment;

    public ClientExpenseType getType() {
        return type;
    }

    public void setType(ClientExpenseType type) {
        this.type = type;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getCumulativeCost() {
        return cumulativeCost;
    }

    public void setCumulativeCost(Long cumulativeCost) {
        this.cumulativeCost = cumulativeCost;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
