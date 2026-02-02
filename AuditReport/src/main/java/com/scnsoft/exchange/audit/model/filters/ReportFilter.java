package com.scnsoft.exchange.audit.model.filters;


public interface ReportFilter {
    public <V> V getCriteria(FilterBy filterBy, Class<V> valueClass);
}
