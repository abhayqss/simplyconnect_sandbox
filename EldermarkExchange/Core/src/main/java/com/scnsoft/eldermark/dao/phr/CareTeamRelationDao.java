package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.CareTeamRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 05/04/2017.
 */
@Repository
public interface CareTeamRelationDao extends JpaRepository<CareTeamRelation, Long>, JpaSpecificationExecutor<CareTeamRelation> {

    @Query("Select o from CareTeamRelation o WHERE code = :code")
    CareTeamRelation getByCode(@Param("code") CareTeamRelation.Relation code);

    @Query("Select o from CareTeamRelation o WHERE name = :name")
    CareTeamRelation getByName(@Param("name") String name);

}
