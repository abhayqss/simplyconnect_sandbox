package com.scnsoft.eldermark.service.medispan.dto;

import java.util.Collection;

public class MediSpanCriteria {

    public static final String IS_EQUAL_TO_OPERATOR = "isEqualTo";
    public static final String CONTAINS_OPERATOR = "contains";

    private String field;
    private String operator;
    private String value;
    private Collection<String> values;

    public MediSpanCriteria(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public MediSpanCriteria(String field, String operator, Collection<String> values) {
        this.field = field;
        this.operator = operator;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }

    public static MediSpanCriteria isEqualTo(String field, String value) {
        return new MediSpanCriteria(field, IS_EQUAL_TO_OPERATOR, value);
    }

    public static MediSpanCriteria isEqualToAny(String field, Collection<String> values) {
        return new MediSpanCriteria(field, IS_EQUAL_TO_OPERATOR, values);
    }

    public static MediSpanCriteria contains(String field, String value) {
        return new MediSpanCriteria(field, CONTAINS_OPERATOR, value);
    }
}
