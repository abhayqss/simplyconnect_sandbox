package com.scnsoft.eldermark.dao.phr;

import static org.springframework.data.domain.Sort.Direction.ASC;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * @author phomal Created on 8/4/2017.
 */
@Repository
public interface InNetworkInsuranceDao extends JpaRepository<InNetworkInsurance, Long>, JpaSpecificationExecutor<InNetworkInsurance> {

    Sort.Order ORDER_BY_IS_FIRST_GROUP_DESC = new Sort.Order(DESC, "isFirstGroup");
    Sort.Order ORDER_BY_IS_POPULAR_DESC = new Sort.Order(DESC, "isPopular");
    Sort.Order ORDER_BY_DISPLAY_NAME = new Sort.Order(ASC, "displayName");

    InNetworkInsurance findById(Long id);

    List<InNetworkInsurance> findInNetworkInsuranceByIdInOrderByDisplayNameAsc(List<Long> ids);

    Page<InNetworkInsurance> getAllByDisplayNameLikeOrderByDisplayNameAsc(String searchText, Pageable pageable);

    InNetworkInsurance getByDisplayName(String displayName);

    @Query("select i from InNetworkInsurance i where i.isFirstGroup = true")
    List<InNetworkInsurance> findItemsInGroupWithoutName(Sort orders);

    @Query("select i from InNetworkInsurance i where i.isPopular = true")
    List<InNetworkInsurance> findPopularItems(Sort orders);

    @Query("select i from InNetworkInsurance i where i.isFirstGroup = false or i.isFirstGroup is null")
    List<InNetworkInsurance> findItemsWithoutSection0(Sort orders);
}