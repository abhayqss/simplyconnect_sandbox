package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
@Repository
public class InterpretiveCcdCodeDaoImpl extends BaseDaoImpl<InterpretiveCcdCode> implements InterpretiveCcdCodeDao {

    public InterpretiveCcdCodeDaoImpl() {
        super(InterpretiveCcdCode.class);
    }

    @Override
    public InterpretiveCcdCode getCcdCode(ConcreteCcdCode originalCode, String displayName) {
        final TypedQuery<InterpretiveCcdCode> query = entityManager.createNamedQuery("interpretiveCcdCode.getCcdCode", InterpretiveCcdCode.class);
        query.setParameter("originalCode", originalCode);
        query.setParameter("displayName", displayName);
        List<InterpretiveCcdCode> results = query.getResultList();

        if (results.size() == 0) {
            return null;
        }
        return results.get(0);
    }

}
