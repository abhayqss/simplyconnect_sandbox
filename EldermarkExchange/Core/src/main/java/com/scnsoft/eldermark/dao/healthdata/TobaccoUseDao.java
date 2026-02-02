package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.TobaccoUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TobaccoUseDao extends JpaRepository<TobaccoUse, Long> {
    @Query("SELECT tu FROM TobaccoUse tu WHERE tu.id IN " +
            "(SELECT MIN(tuu.id) FROM TobaccoUse tuu " +
            "WHERE tuu.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY tuu.effectiveTimeLow, tuu.value.id)")
    Page<TobaccoUse> listResidentsTobaccoUseWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(tu) FROM TobaccoUse tu WHERE tu.id IN " +
            "(SELECT MIN(tuu.id) FROM TobaccoUse tuu " +
            "WHERE tuu.socialHistory.resident.id IN (:residentIds) " +
            "GROUP BY tuu.effectiveTimeLow, tuu.value)")
    Long countResidentsTobaccoUseWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
