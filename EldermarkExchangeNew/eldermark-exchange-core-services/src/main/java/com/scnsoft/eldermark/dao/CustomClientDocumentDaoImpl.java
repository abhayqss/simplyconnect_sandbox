package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.DocumentCount;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.ClientDocument_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class CustomClientDocumentDaoImpl implements CustomClientDocumentDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomClientDocumentDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Instant> findMinDate(Specification<ClientDocument> specification) {
        return findDate(specification, CriteriaBuilder::least);
    }

    @Override
    public List<DocumentCount> countGroupedBySignatureStatus(Specification<ClientDocument> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(DocumentCount.class);
        var root = crq.from(ClientDocument.class);

        var signatureRequest = JpaUtils.getOrCreateJoin(root, ClientDocument_.signatureRequest, JoinType.LEFT);
        var signatureStatus = signatureRequest.get(DocumentSignatureRequest_.status);

        crq.multiselect(signatureStatus, cb.count(root.get(ClientDocument_.id)));
        crq.where(specification.toPredicate(root, crq, cb));
        crq.groupBy(signatureStatus);

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }

    private Optional<Instant> findDate(Specification<ClientDocument> specification, BiFunction<CriteriaBuilder, Expression<Instant>, Expression<Instant>> selector) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Object.class);
        var root = crq.from(ClientDocument.class);

        crq.multiselect(selector.apply(cb, root.get(ClientDocument_.creationTime)));
        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);
        return Optional.ofNullable((Instant)typed.getSingleResult());
    }
}
