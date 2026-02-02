package com.scnsoft.eldermark.dump.dao;


import com.scnsoft.eldermark.dump.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDao extends JpaSpecificationExecutor<Client>, JpaRepository<Client, Long> {

    @Query("Select id from Client where organizationId = :organizationId")
    List<Long> idsInOrganization(@Param("organizationId") Long organizationId);

    @Query(value = "select client1_.id as id, count(clientasse0_.id) as count " +
            "from ResidentAssessmentResult clientasse0_ " +
            "   inner join resident client1_ on clientasse0_.resident_id=client1_.id " +
            "   inner join Assessment assessment2_ on clientasse0_.assessment_id=assessment2_.id " +
            "       where client1_.database_id = :organizationId and assessment2_.short_name='Comprehensive Assessment' " +
            "           and clientasse0_.archived=0 and clientasse0_.assessment_status='COMPLETED'" +
            "   GROUP BY client1_.id", nativeQuery = true)
    List<AssessmentCount> comprehensiveCompletedCount(@Param("organizationId") Long organizationId);

    interface AssessmentCount {
        Long getId();

        Long getCount();
    }
}
