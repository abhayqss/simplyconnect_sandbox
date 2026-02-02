package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InsuranceNetworkName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InsuranceNetworkNameDao extends JpaRepository<InsuranceNetworkName, String> {

    @Query("SELECT ins FROM InsuranceNetworkName ins where ins.name LIKE :name and (ins.organizationId is null or ins.organizationId = :organizationId) ")
    List<InsuranceNetworkName> findAllByNameLikeAndOrganizationId(@Param("name") String name, @Param("organizationId") Long organizationId);

}
