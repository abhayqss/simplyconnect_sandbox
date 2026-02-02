package com.scnsoft.eldermark.dao.carecoordination.impl;

import com.scnsoft.eldermark.dao.carecoordination.AdtMessageCustomDao;
import com.scnsoft.eldermark.entity.AdtTypeEnum;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.EnumMap;
import java.util.Map;

@Repository
public class AdtMessageCustomDaoImpl implements AdtMessageCustomDao {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Map<AdtTypeEnum, String> TYPE_TO_QUERY_MAP = new EnumMap<>(AdtTypeEnum.class);

    {
        for (AdtTypeEnum adtType : AdtTypeEnum.values()) {
            TYPE_TO_QUERY_MAP.put(adtType, "Select a from " + adtType.getEntityClass().getSimpleName() + " a where a.id = :id");
        }
    }

    @Override
    public AdtMessage getMessageById(Long msgId, AdtTypeEnum adtType) {

        final String queryStr = TYPE_TO_QUERY_MAP.get(adtType);
        if (queryStr == null) {
            throw new RuntimeException("Not supported message type " + adtType);
        }

        final TypedQuery<AdtMessage> query = entityManager.createQuery(queryStr, AdtMessage.class);
        query.setParameter("id", msgId);
        return query.getSingleResult();
    }
}
