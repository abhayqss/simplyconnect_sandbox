package com.scnsoft.eldermark.dto.client.expense;

import com.scnsoft.eldermark.beans.projection.ClientExpenseSecurityAwareEnity;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClientExpenseDto implements ClientExpenseSecurityAwareEnity {

    private Long id;

    private Long clientId;

    @NotNull
    private ClientExpenseType typeName;

    private String typeTitle;

    @NotNull
    private Long cost;

    private Long cumulativeCost;

    @Size(max = 256)
    private String comment;

    @NotNull
    private Long date;

    private Long reportedDate;

    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ClientExpenseType getTypeName() {
        return typeName;
    }

    public void setTypeName(ClientExpenseType typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Long reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
