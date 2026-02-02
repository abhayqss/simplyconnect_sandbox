package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Procedure;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProcedureDaoImpl extends ResidentAwareDaoImpl<Procedure> implements ProcedureDao {

    public ProcedureDaoImpl() {
        super(Procedure.class);
    }

    @Override
    public List<Procedure> listByResidentId(Long residentId) {
        TypedQuery<Procedure> query = entityManager.createNamedQuery("procedure.listByResidentId", Procedure.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Procedure procedure : this.listByResidentId(residentId)) {
            this.delete(procedure);
            ++count;
        }

        return count;
    }

}
