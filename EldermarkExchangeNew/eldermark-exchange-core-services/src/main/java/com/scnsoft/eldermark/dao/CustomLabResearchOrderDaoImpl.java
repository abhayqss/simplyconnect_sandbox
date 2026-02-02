package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Avatar_;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.Document_;
import com.scnsoft.eldermark.entity.lab.*;
import com.scnsoft.eldermark.entity.lab.report.LabResearchResultWithOrder;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderDocumentWithOrderListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import java.util.List;

public class CustomLabResearchOrderDaoImpl implements CustomLabResearchOrderDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomLabResearchOrderDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<LabResearchOrderListItem> findLabOrders(Specification<LabResearchOrder> specification, Pageable pageable) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(LabResearchOrderListItem.class);
        var root = crq.from(LabResearchOrder.class);

        var createdByJoin = root.join(LabResearchOrder_.createdBy);
        var clientJoin = root.join(LabResearchOrder_.client);
        var communityJoin = clientJoin.join(Client_.community);
        var avatarJoin = clientJoin.join(Client_.avatar, JoinType.LEFT);

        crq.multiselect(root.get(LabResearchOrder_.id), createdByJoin.get(Employee_.firstName), createdByJoin.get(Employee_.lastName),
                clientJoin.get(Client_.id), clientJoin.get(Client_.firstName), clientJoin.get(Client_.lastName),
                communityJoin.get(Community_.name), avatarJoin.get(Avatar_.id) ,root.get(LabResearchOrder_.status),
                root.get(LabResearchOrder_.reason), root.get(LabResearchOrder_.requisitionNumber), root.get(LabResearchOrder_.createdDate));
        crq.where(specification.toPredicate(root, crq, cb));

        if (pageable != null) {
            crq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        }

        var typed = entityManager.createQuery(crq);
        if (pageable != null) {
            typed.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typed.setMaxResults(pageable.getPageSize());
        }
        return new PageImpl<>(typed.getResultList(), pageable == null ? Pageable.unpaged() : pageable, count(specification));
    }

    @Override
    public List<LabResearchOrderDocumentWithOrderListItem> findLabOrdersWithDocuments(Specification<LabResearchOrder> specification, Sort sort) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(LabResearchOrderDocumentWithOrderListItem.class);
        var root = crq.from(LabResearchOrder.class);

        var clientJoin = root.join(LabResearchOrder_.client);
        var documentJoin = root.join(LabResearchOrder_.documents, JoinType.LEFT);

        crq.multiselect(root.get(LabResearchOrder_.id), clientJoin.get(Client_.id), clientJoin.get(Client_.firstName),
                clientJoin.get(Client_.lastName), root.get(LabResearchOrder_.orderDate), documentJoin.get(Document_.id),
                documentJoin.get(Document_.documentTitle), documentJoin.get(Document_.originalFileName), documentJoin.get(Document_.mimeType));
        crq.where(specification.toPredicate(root, crq, cb));

        if (sort != null) {
            crq.orderBy(QueryUtils.toOrders(sort, root, cb));
        }

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }

    @Override
    public List<LabResearchResultWithOrder> findResultsWithOrders(Specification<LabResearchOrder> specification, Sort sort) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(LabResearchResultWithOrder.class);
        var root = crq.from(LabResearchOrder.class);

        var clientJoin = root.join(LabResearchOrder_.client);
        var orderOruJoin = root.join(LabResearchOrder_.orderORU);
        var resultsJoin = root.join((LabResearchOrder_.observationResults), JoinType.LEFT);
        var communityJoin = clientJoin.join(Client_.community);

        crq.multiselect(root.get(LabResearchOrder_.id), root.get(LabResearchOrder_.reason), root.get(LabResearchOrder_.specimenDate), clientJoin.get(Client_.id), clientJoin.get(Client_.firstName),
                clientJoin.get(Client_.lastName), communityJoin.get(Community_.name), orderOruJoin.get(LabResearchOrderORU_.receivedDatetime),
                resultsJoin.get(LabResearchOrderObservationResult_.code), resultsJoin.get(LabResearchOrderObservationResult_.value));
        crq.where(specification.toPredicate(root, crq, cb));

        if (sort != null) {
            crq.orderBy(QueryUtils.toOrders(sort, root, cb));
        }

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }

    private Long count(Specification<LabResearchOrder> specification) {
        var cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        var countRoot = cq.from(LabResearchOrder.class);
        cq.select(cb.count(countRoot.get(LabResearchOrder_.id)));
        cq.where(specification.toPredicate(countRoot, cq, cb));
        var countQuery = entityManager.createQuery(cq);
        return countQuery.getSingleResult();
    }

}
