package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.client.ClientHealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ClientHealthPlanDao extends JpaRepository<ClientHealthPlan, Long> {

    List<ClientHealthPlan> findAllByClient_id(Long clientId);

    List<ClientHealthPlan> findAllByClientIdIn(Collection<Long> ids);

}
