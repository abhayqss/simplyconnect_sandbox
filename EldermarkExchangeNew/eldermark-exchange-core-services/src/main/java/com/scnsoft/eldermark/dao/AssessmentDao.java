package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentDao extends AppJpaRepository<Assessment, Long> {

    @Query("Select distinct asm from Assessment asm left join asm.organizations orgs " +
            "where orgs.id is null or orgs.id=:clientOrganizationId ")
    List<Assessment> findAvailableAssessments(@Param("clientOrganizationId") Long clientOrganizationId);

    @Query("Select distinct asm from Assessment asm left join asm.organizations orgs " +
            "where (orgs.id is null or orgs.id=:clientOrganizationId) and (asm.code in (:types)) ")
    List<Assessment> findAvailableAssessmentsByTypes(@Param("clientOrganizationId") Long clientOrganizationId, @Param("types") List<String> types);

    Assessment findByShortName(String shortName);
}
