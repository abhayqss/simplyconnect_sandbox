package com.scnsoft.exchange.audit.service;


import com.scnsoft.exchange.audit.dao.ReportDao;
import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class BaseReportService<T extends ReportDto> implements ReportService<T> {
    private static final int PAGE_SIZE = 30;

    private ReportDao<T> dao;

    public BaseReportService(ReportDao<T> dao) {
        this.dao = dao;
    }

    public List<T> generate(ReportFilter filter) {
        return dao.findAll(filter);
    }

    public Page<T> generate(Integer pageNumber, ReportFilter filter) {
        int totalCount = dao.count(filter);
        PageRequest pageRequest = createPageRequest(pageNumber, totalCount);

        List<T> result = dao.findAll(pageRequest.getOffset(), pageRequest.getPageSize(), filter);

        return new PageImpl<T>(result, pageRequest, totalCount);
    }

    private PageRequest createPageRequest(int pageNumber, int totalCount) {
        if(pageNumber < 1) {
            pageNumber = 1;
        }
        if(PAGE_SIZE * pageNumber > totalCount) {
            pageNumber = (totalCount / PAGE_SIZE) + 1;
        }

        return new PageRequest(pageNumber - 1, PAGE_SIZE);
    }
}
