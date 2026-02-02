package com.scnsoft.eldermark.dto.client.expense;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense_;
import org.springframework.data.domain.Sort;

public class ClientExpenseListItemDto {

    private Long id;

    private String typeName;

    private String typeTitle;

    private Long cost;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(ClientExpense_.DATE)
    private Long date;

    @EntitySort(ClientExpense_.REPORTED_DATE)
    private Long reportedDate;

    @EntitySort(joined = {ClientExpense_.AUTHOR, Employee_.FIRST_NAME})
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
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
