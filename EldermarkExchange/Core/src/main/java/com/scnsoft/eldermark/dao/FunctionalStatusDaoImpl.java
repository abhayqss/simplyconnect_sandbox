package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class FunctionalStatusDaoImpl extends ResidentAwareDaoImpl<FunctionalStatus> implements FunctionalStatusDao {

    public FunctionalStatusDaoImpl() {
        super(FunctionalStatus.class);
    }

    @Override
    public FunctionalStatus getResidentFunctionalStatus(Long residentId) {
        TypedQuery<FunctionalStatus> query = entityManager.createQuery("select fs from FunctionalStatus fs where fs.resident.id = :residentId",
                FunctionalStatus.class);
        query.setParameter("residentId", residentId);
        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(String.format("Resident [id = %d] has several FunctionalStatus", residentId), e);
        }
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (FunctionalStatus functionalStatus : this.listByResidentId(residentId)) {
            this.delete(functionalStatus);
            ++count;
        }

        return count;
    }
}
