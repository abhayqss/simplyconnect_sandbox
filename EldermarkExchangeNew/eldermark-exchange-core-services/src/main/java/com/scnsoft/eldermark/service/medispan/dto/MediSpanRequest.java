package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MediSpanRequest {

    public static final List<String> ALL_FIELDS = List.of("all");

    private String customerTransactionId;
    private String count;
    private String startIndex;
    private String removeHistorical;
    private List<MediSpanCriteria> criteria;
    private List<String> fields;

    public String getCustomerTransactionId() {
        return customerTransactionId;
    }

    public void setCustomerTransactionId(String customerTransactionId) {
        this.customerTransactionId = customerTransactionId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public String getRemoveHistorical() {
        return removeHistorical;
    }

    public void setRemoveHistorical(String removeHistorical) {
        this.removeHistorical = removeHistorical;
    }

    public List<MediSpanCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<MediSpanCriteria> criteria) {
        this.criteria = criteria;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
