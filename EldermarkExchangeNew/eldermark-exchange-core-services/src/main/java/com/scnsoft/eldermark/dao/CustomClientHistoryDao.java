package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import com.scnsoft.eldermark.entity.history.ClientHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomClientHistoryDao {
    List<ClientIntakesReportItem> findClientIntakesReportItems(Specification<ClientHistory> specification, Sort sort);
}
