package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.CareTeamRelationship;
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
public interface CareTeamRelationshipDao extends JpaRepository<CareTeamRelationship, Long>, JpaSpecificationExecutor<CareTeamRelationship> {

    @Query("Select o from CareTeamRelationship o WHERE code = :code")
    CareTeamRelationship getByCode(@Param("code") CareTeamRelationship.Relationship code);

    @Query("Select o from CareTeamRelationship o WHERE name = :name")
    CareTeamRelationship getByName(@Param("name") String name);

}
