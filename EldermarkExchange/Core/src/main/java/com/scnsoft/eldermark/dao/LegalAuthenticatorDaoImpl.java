package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.LegalAuthenticator;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class LegalAuthenticatorDaoImpl extends ResidentAwareDaoImpl<LegalAuthenticator> implements LegalAuthenticatorDao {

    public LegalAuthenticatorDaoImpl() {
        super(LegalAuthenticator.class);
    }

    @Override
    public LegalAuthenticator getCcdLegalAuthenticator(Long residentId) {
        TypedQuery<LegalAuthenticator> query = entityManager.createQuery("select d from LegalAuthenticator d where d.resident.id = :residentId",
                LegalAuthenticator.class);
        query.setParameter("residentId", residentId);

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(String.format("Resident [id = %d] has several LegalAuthenticators", residentId), e);
        }
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        final LegalAuthenticator legalAuthenticator = getCcdLegalAuthenticator(residentId);
        if (legalAuthenticator == null) {
            return 0;
        } else {
            delete(legalAuthenticator);
            return 1;
        }
    }

}
