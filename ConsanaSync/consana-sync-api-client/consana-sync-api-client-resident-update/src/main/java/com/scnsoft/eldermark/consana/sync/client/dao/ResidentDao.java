package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResidentDao extends JpaRepository<Resident, Long>, JpaSpecificationExecutor<Resident>, MergedResidentsDaoFragment {

    default List<Resident> getMergedResidents(Long residentId) {
        return findAllById(getMergedResidentIds(residentId));
    }

    @EntityGraph(value = "databaseAndFacilityJoins" , type = EntityGraph.EntityGraphType.FETCH)
    List<Resident> findAllById(Iterable<Long> var1);

    @Modifying
    @Query("UPDATE Resident set consanaXrefId=:xrefId where id = :id")
    void updateXrefId(@Param("xrefId") String xrefId, @Param("id") Long id);
}
