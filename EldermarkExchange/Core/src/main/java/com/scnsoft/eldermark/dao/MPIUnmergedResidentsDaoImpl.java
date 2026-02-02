package com.scnsoft.eldermark.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * @author phomal
 * Created on 4/13/2017.
 */
@Repository
public class MPIUnmergedResidentsDaoImpl implements MPIUnmergedResidentsDao {

    private final String TABLE_NAME = "MPI_unmerged_residents";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void insert(Long residentId, Long residentId2) {
        String queryStr = "INSERT INTO [MPI_unmerged_residents] (first_resident_id, second_resident_id) VALUES (?, ?)";
        entityManager.createQuery(queryStr)
                .setParameter(1, residentId)
                .setParameter(2, residentId2)
                .executeUpdate();
        entityManager.createQuery(queryStr)
                .setParameter(1, residentId2)
                .setParameter(2, residentId)
                .executeUpdate();
    }

    @Override
    public boolean exists(Long residentId, Long residentId2) {
        String queryStr = "SELECT 1 FROM [MPI_unmerged_residents] WHERE ([first_resident_id] = :id1 AND [second_resident_id] = :id2) OR ([second_resident_id] = :id2 AND [first_resident_id] = :id1)";

        try {
            entityManager.createQuery(queryStr)
                    .setParameter("id1", residentId)
                    .setParameter("id2", residentId2)
                    .getSingleResult();
            return true;
        } catch (NoResultException exc) {
            return false;
        }
    }

}
