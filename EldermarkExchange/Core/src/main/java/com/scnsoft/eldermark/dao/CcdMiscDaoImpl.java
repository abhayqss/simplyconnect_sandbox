package com.scnsoft.eldermark.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by knetkachou on 5/6/2017.
 */
@Repository
public class CcdMiscDaoImpl implements CcdMiscDao {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean isCcdExist(Long residentId) {
        final StringBuilder strQuery = new StringBuilder();
        strQuery.append("select top 1 id from ( ");
        strQuery.append("select top 1 id from Allergy where resident_id = :residentId union ");
        strQuery.append("select top 1 id from AdvanceDirective where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Medication where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Problem where resident_id = :residentId union ");
        strQuery.append("select top 1 id from ResidentProcedure where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Result where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Encounter where resident_id = :residentId union ");
        strQuery.append("select top 1 id from FamilyHistory where resident_id = :residentId union ");
        strQuery.append("select top 1 id from VitalSign where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Immunization where resident_id = :residentId union ");
        strQuery.append("select top 1 id from MedicalEquipment where resident_id = :residentId union ");
        strQuery.append("select top 1 id from Payer where resident_id = :residentId union ");
        strQuery.append("select top 1 id from PlanOfCare where resident_id = :residentId union ");
        strQuery.append("select top 1 id from SocialHistory where resident_id = :residentId ");
        strQuery.append(") sections");
        final Query query = entityManager.createNativeQuery(strQuery.toString(), Long.class);
        query.setParameter("residentId", residentId);
        return query.getResultList().size() > 0;
    }

}
