package com.scnsoft.exchange.audit.service;


import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService<T extends ReportDto> {
    public List<T> generate(ReportFilter filter);
    public Page<T> generate(Integer pageNumber, ReportFilter filter);
}
