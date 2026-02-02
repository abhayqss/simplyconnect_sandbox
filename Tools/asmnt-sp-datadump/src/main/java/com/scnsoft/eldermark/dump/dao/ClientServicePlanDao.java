package com.scnsoft.eldermark.dump.dao;

import com.scnsoft.eldermark.dump.entity.serviceplan.ClientServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientServicePlanDao extends JpaRepository<ClientServicePlan, Long>, JpaSpecificationExecutor<ClientServicePlan> {

}
