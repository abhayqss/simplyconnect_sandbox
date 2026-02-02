package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 *
 */
@Repository
public interface AdvanceDirectiveDao extends JpaRepository<AdvanceDirective, Long> {

    @Query(value = " SELECT ad " +
            "        FROM AdvanceDirective ad " +
            "        where ad.resident.id in " +
            "           (select min(ad2.resident.id) from AdvanceDirective ad2 " +
            "            where ad2.resident.id in :residentIds " +
            "            group by ad2.textType, ad2.textValue, ad2.timeLow, ad2.timeHigh )"
            )
    Page<AdvanceDirective> getDirectivesByResidentIds(@Param("residentIds") Collection<Long> residentIds,
                                           final Pageable pageable);

    @Query(value = " SELECT count(ad) " +
            "        FROM AdvanceDirective ad " +
            "        where ad.resident.id in " +
            "           (select min(ad2.resident.id) from AdvanceDirective ad2 " +
            "            where ad2.resident.id in :residentIds " +
            "            group by ad2.textType, ad2.textValue, ad2.timeLow, ad2.timeHigh )"
    )
    Long countResidentAdvanceDirectivesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
