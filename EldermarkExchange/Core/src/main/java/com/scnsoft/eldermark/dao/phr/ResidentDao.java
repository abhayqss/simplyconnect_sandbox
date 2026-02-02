package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author phomal Created on 1/30/2018.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ResidentDao extends JpaRepository<Resident, Long> {

    String RESIDENT_IS_VISIBLE = "(f.isInactive <> 1 AND r.isOptOut <> 1 AND r.active = 1)";

    @Query("SELECT facility.id FROM Resident WHERE id = :id")
    Long getFacilityIdById(@Param("id") long id);

    @Query("SELECT CASE WHEN count(r.id)>0 THEN true ELSE false END FROM Resident r INNER JOIN r.facility f "
            + "WHERE r.id = :id AND " + RESIDENT_IS_VISIBLE)
    boolean isVisible(@Param("id") Long id);

    @Query("SELECT r FROM Resident r INNER JOIN r.facility f " + "WHERE r.databaseId = :databaseId AND "
            + RESIDENT_IS_VISIBLE)
    Page<Resident> findVisibleByDatabaseId(@Param("databaseId") long databaseId, Pageable pageable);

    @Query("SELECT r FROM Resident r INNER JOIN r.facility f " + "WHERE f.id = :organizationId AND "
            + RESIDENT_IS_VISIBLE)
    Page<Resident> findVisibleByFacilityId(@Param("organizationId") long organizationId, Pageable pageable);

    @Query("SELECT r FROM Resident r INNER JOIN r.facility f " + "WHERE r.databaseId IN (:databaseIds) AND "
            + RESIDENT_IS_VISIBLE)
    Page<Resident> findVisibleByDatabaseIdIn(@Param("databaseIds") Collection<Long> ids, Pageable pageable);

}
