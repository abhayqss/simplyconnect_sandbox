package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.Custodian;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class CustodianDaoImpl extends ResidentAwareDaoImpl<Custodian> implements CustodianDao {

    public CustodianDaoImpl() {
        super(Custodian.class);
    }

    @Override
    public Custodian getCcdCustodian(Long residentId) {
        TypedQuery<Custodian> query = entityManager.createQuery("select c from Custodian c where c.resident.id = :residentId",
                                                                 Custodian.class);
        query.setParameter("residentId", residentId);

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(String.format("Resident [id = %d] has several Custodians", residentId), e);
        }
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        final Custodian custodian = getCcdCustodian(residentId);
        if (custodian != null) {
            this.delete(custodian);
            return 1;
        }
        return 0;
    }

}
