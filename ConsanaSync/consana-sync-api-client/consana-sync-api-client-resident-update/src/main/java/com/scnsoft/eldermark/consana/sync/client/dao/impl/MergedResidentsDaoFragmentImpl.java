package com.scnsoft.eldermark.consana.sync.client.dao.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.MergedResidentsDaoFragment;
import com.scnsoft.eldermark.consana.sync.client.model.entities.MergedClientView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class MergedResidentsDaoFragmentImpl implements MergedResidentsDaoFragment {

    private final EntityManager entityManager;

    @Autowired
    public MergedResidentsDaoFragmentImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Long> getMergedResidentIds(Long residentId) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(MergedClientView.class);

        query.where(cb.equal(root.get(MergedClientView.CLIENT_ID), residentId));
        query.select(root.get(MergedClientView.MERGED_CLIENT_ID));

        return entityManager.createQuery(query).getResultList();
    }
}
