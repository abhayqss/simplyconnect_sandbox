package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CareTeamRoleDao extends JpaRepository<CareTeamRole, Long>, JpaSpecificationExecutor<CareTeamRole> {

    Sort.Order ORDER_BY_POSITION = new Sort.Order(Sort.Direction.ASC, "position");

}
