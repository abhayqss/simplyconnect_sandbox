package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MpiMergedResidents;
import com.scnsoft.eldermark.entity.ProbablyMatchedResidentIdDto;
import com.scnsoft.eldermark.entity.Resident;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 4/7/2017.
 */
@Repository
public class MPIMergedResidentsDaoImpl extends BaseDaoImpl<MpiMergedResidents> implements MPIMergedResidentsDao {

    @PersistenceContext
    protected EntityManager em;

    public MPIMergedResidentsDaoImpl() {
        super(MpiMergedResidents.class);
    }

    @Override
    public List<Long> listProbablyMatchedResidents(Long residentId) {
        TypedQuery<ProbablyMatchedResidentIdDto> query = em.createNamedQuery("exec__find_probably_matched_patients", ProbablyMatchedResidentIdDto.class);
        query.setParameter("residentId", residentId);

        List<Long> matchedResidentIds = new ArrayList<Long>();
        for (ProbablyMatchedResidentIdDto e : query.getResultList()) {
            if (e.longValue() != residentId)    // exclude itself
                matchedResidentIds.add(e.longValue());
        }

        return matchedResidentIds;
    }

    // TODO use in ResidentMerger module
    @Override
    public MpiMergedResidents insertAutomerged(Long survivingResidentId, Long mergedResidentId, boolean probablyMatched, Double confidence) {
        Resident survivingResident = entityManager.getReference(Resident.class, survivingResidentId);
        Resident mergedResident = entityManager.getReference(Resident.class, mergedResidentId);

        MpiMergedResidents mpiMergedResidents = new MpiMergedResidents();
        mpiMergedResidents.setSurvivingResident(survivingResident);
        mpiMergedResidents.setMergedResident(mergedResident);
        mpiMergedResidents.setProbablyMatched(probablyMatched);
        mpiMergedResidents.setMergedAutomatically(true);
        mpiMergedResidents.setMergedManually(false);
        mpiMergedResidents.setMerged(!probablyMatched);
        mpiMergedResidents.setDukeConfidence(confidence);

        return create(mpiMergedResidents);
    }

    @Override
    public void deleteMergesBetween(Long residentId1, Long residentId2) {
        String queryStr = "DELETE FROM [MPI_merged_residents] WHERE ([surviving_resident_id] = :residentId1 AND [merged_resident_id] = :residentId2) OR ([surviving_resident_id] = :residentId2 AND [merged_resident_id] = :residentId1)";
        Query query = entityManager.createQuery(queryStr)
                .setParameter("residentId1", residentId1)
                .setParameter("residentId2", residentId2);
        query.executeUpdate();
    }

}
