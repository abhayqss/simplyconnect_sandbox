package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.PolicyActivity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * @author phomal
 * Created on 11/4/2017.
 */
@Repository
public interface PolicyActivityDao extends JpaRepository<PolicyActivity, Long> {

    Sort.Order ORDER_BY_COVERAGE_PERIOD_START_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "prt.timeLow");
    Sort.Order ORDER_BY_ORG_NAME = new Sort.Order(Sort.Direction.ASC, "org.name");

    PolicyActivity findByPayerResidentId(Long residentId);

    Long countByPayerResidentIdIn(Collection<Long> residentIds);

    @Query(value = "SELECT pa FROM PolicyActivity pa " +
            "   INNER JOIN FETCH pa.payer pr " +
            "   LEFT JOIN FETCH pa.payerOrganization org " +
            "   LEFT JOIN FETCH pa.participant prt " +
            "WHERE pa.id IN (" +
            "SELECT min(pa.id) FROM PolicyActivity pa " +
            "   INNER JOIN pa.payer pr " +
            "   LEFT JOIN pa.participant prt " +
            "WHERE pr.resident.id IN :residentIds " +
            "GROUP BY pa.payerOrganization, pa.participantMemberId, prt.timeLow, prt.timeHigh)")
    List<PolicyActivity> listResidentPolicyActivitiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds,
                                                                       final Sort sort);

    @Query("SELECT count(pa) FROM PolicyActivity pa " +
            "WHERE pa.id IN (" +
            "SELECT min(pa.id) FROM PolicyActivity pa " +
            "   INNER JOIN pa.payer pr " +
            "   LEFT JOIN pa.participant prt " +
            "WHERE pr.resident.id IN :residentIds " +
            "GROUP BY pa.payerOrganization, pa.participantMemberId, prt.timeLow, prt.timeHigh)")
    Long countResidentPolicyActivitiesWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);

}
