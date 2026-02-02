package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.UnknownCcdCode;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
@Repository
public class UnknownCcdCodeDaoImpl extends BaseDaoImpl<UnknownCcdCode> implements UnknownCcdCodeDao {

    public UnknownCcdCodeDaoImpl() {
        super(UnknownCcdCode.class);
    }

    @Override
    public List<UnknownCcdCode> getCcdCodes(String code, String codeSystem) {
        final TypedQuery<UnknownCcdCode> query = entityManager.createNamedQuery("unknownCcdCode.find", UnknownCcdCode.class);
        query.setParameter("code", code);
        query.setParameter("codeSystem", codeSystem);

        return query.getResultList();
    }

}
