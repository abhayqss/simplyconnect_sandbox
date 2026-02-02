package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.MergedResidentIdDto;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MPIDaoImpl extends BaseDaoImpl<MPI> implements MPIDao {

    @PersistenceContext
    protected EntityManager em;

    public MPIDaoImpl() {
        super(MPI.class);
    }

    @Override
    public List<Long> listMergedResidents(Long residentId) {
        TypedQuery<MergedResidentIdDto> query = em.createNamedQuery("exec__find_merged_patients", MergedResidentIdDto.class);
        query.setParameter("residentId", residentId);

        List<Long> mergedResidentIds = new ArrayList<Long>();
        for (MergedResidentIdDto e : query.getResultList()) {
            if (e.longValue() != residentId) // exclude itself
                mergedResidentIds.add(e.longValue());
        }

        return mergedResidentIds;
    }

    public List<MPI> getByResidentId(Long residentId) {
        TypedQuery<MPI> query = em.createQuery("SELECT m FROM MPI m where m.residentId=:residentId order by m.id", MPI.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    public String getAaUniversalByResidentId(Long residentId) {
        TypedQuery<String> query = em.createQuery("SELECT m.assigningAuthorityUniversal FROM MPI m where m.residentId=:residentId order by m.id", String.class);
        query.setParameter("residentId", residentId);
        List<String> result = query.getResultList();
        if (result.size()>0) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public List<Long> listResidentsAndMergedResidents(Long databaseId) {
        TypedQuery<MergedResidentIdDto> query = em.createNamedQuery("exec__find_patients_and_merged_patients", MergedResidentIdDto.class);
        query.setParameter("databaseId", databaseId);
        List<Long> residentIds = new ArrayList<Long>();
        for (MergedResidentIdDto e : query.getResultList()) {
            residentIds.add(e.longValue());
        }
        return residentIds;
    }

}
