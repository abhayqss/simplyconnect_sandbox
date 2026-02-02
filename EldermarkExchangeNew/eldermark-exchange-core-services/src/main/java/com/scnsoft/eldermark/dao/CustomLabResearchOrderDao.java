package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderListItem;
import com.scnsoft.eldermark.entity.lab.report.LabResearchResultWithOrder;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderDocumentWithOrderListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomLabResearchOrderDao {
    Page<LabResearchOrderListItem> findLabOrders(Specification<LabResearchOrder> specification, Pageable pageable);
    List<LabResearchOrderDocumentWithOrderListItem> findLabOrdersWithDocuments(Specification<LabResearchOrder> specification, Sort sort);
    List<LabResearchResultWithOrder> findResultsWithOrders(Specification<LabResearchOrder> specification, Sort sort);
}
