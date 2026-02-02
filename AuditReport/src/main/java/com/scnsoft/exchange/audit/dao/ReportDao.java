package com.scnsoft.exchange.audit.dao;


import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;

import java.util.List;

public interface ReportDao<T extends ReportDto> {
    public List<T> findAll(ReportFilter filter);
    public List<T> findAll(int offset, int limit, ReportFilter filter);

    public int count(ReportFilter filter);
}
