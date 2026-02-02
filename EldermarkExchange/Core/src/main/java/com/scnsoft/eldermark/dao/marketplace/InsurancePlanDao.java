package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * @author phomal
 */
@Repository
public interface InsurancePlanDao extends JpaRepository<InsurancePlan, Long> {

    Sort.Order ORDER_BY_DISPLAY_NAME = new Sort.Order(ASC, "displayName");

    Page<InsurancePlan> getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(Long inNetworkInsuranceId, Pageable pageable);

    Page<InsurancePlan> getAllByInNetworkInsuranceIdAndDisplayNameLikeOrderByDisplayNameAsc(Long inNetworkInsuranceId,
            String displayNameSearch, Pageable pageable);

    List<InsurancePlan> getAllByInNetworkInsuranceIdOrderByDisplayNameAsc(Long inNetworkInsuranceId);

    Page<InsurancePlan> getAllByDisplayNameLikeOrderByDisplayNameAsc(String displayNameSearch, Pageable pageable);

    @Query("Select plan.inNetworkInsuranceId from InsurancePlan plan where plan.id = :insurancePlanId")
    Long getInNetworkInsuranceIdById(@Param("insurancePlanId") Long insurancePlanId);

    InsurancePlan getByInNetworkInsuranceIdAndDisplayName(Long inNetworkInsuranceId, String displayName);

    @Query("Select plan from InsurancePlan plan where plan.isPopular = true")
    List<InsurancePlan> findPopularItems(Sort orders);
}
