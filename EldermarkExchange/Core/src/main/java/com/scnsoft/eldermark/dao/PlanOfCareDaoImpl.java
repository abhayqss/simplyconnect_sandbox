package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.PlanOfCare;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class PlanOfCareDaoImpl extends ResidentAwareDaoImpl<PlanOfCare> implements PlanOfCareDao {

    public PlanOfCareDaoImpl() {
        super(PlanOfCare.class);
    }

    @Override
    public PlanOfCare getResidentPlanOfCare(Long residentId) {
        TypedQuery<PlanOfCare> query = entityManager.createQuery("select p from PlanOfCare p where p.resident.id = :residentId",
                PlanOfCare.class);
        query.setParameter("residentId", residentId);
        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(String.format("Resident [id = %d] has several PlanOfCare", residentId), e);
        }
    }


    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (PlanOfCare planOfCare : this.listByResidentId(residentId)) {
            this.delete(planOfCare);
            ++count;
        }

        return count;
    }

}
