package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ConcreteCcdCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
@Repository
public class ConcreteCcdCodeDaoImpl implements ConcreteCcdCodeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ConcreteCcdCode getCcdCode(String code, String codeSystem) {
        return getCcdCode(code, codeSystem, null);
    }

    @Override
    public ConcreteCcdCode getCcdCode(String code, String codeSystem, String valueSet) {
        final TypedQuery<ConcreteCcdCode> query = entityManager.createNamedQuery("concreteCcdCode.getCcdCode", ConcreteCcdCode.class);
        query.setParameter("code", code);
        query.setParameter("codeSystem", codeSystem);
        query.setParameter("valueSet", null);

        List<ConcreteCcdCode> results = query.getResultList();

        // if there're multiple results, try to refine search
        if (results.size() > 1 && StringUtils.isNotEmpty(valueSet)) {
            query.setParameter("valueSet", valueSet);
            results = query.getResultList();
            if (results.size() > 1) {
                throw new NonUniqueResultException();
            }
        }
        if (results.size() == 0) {
            return null;
        }
        return results.get(0);
    }

}
