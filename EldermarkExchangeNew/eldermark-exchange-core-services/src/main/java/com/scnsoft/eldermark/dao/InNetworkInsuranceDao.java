package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InNetworkInsurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InNetworkInsuranceDao extends JpaRepository<InNetworkInsurance, Long> {

    Page<InNetworkInsurance> findByDisplayNameLike(String name, Pageable pageable);

    Optional<InNetworkInsurance> findFirstByKey(String key);

    Optional<InNetworkInsurance> findFirstByDisplayName(String name);
}
