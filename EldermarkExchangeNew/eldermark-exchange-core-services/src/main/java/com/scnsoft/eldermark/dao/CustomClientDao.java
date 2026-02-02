package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomClientDao {
    //todo rewrite using evaluated properties com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty
    List<ClientIntakesReportItem> findClientIntakesReportItems(Specification<Client> specification, Sort sort);

    List<IdCommunityIdAssociatedEmployeeIdsAware> findAllIdCommunityIdAssociatedEmployeeIdsAware(Specification<Client> spec);
}
