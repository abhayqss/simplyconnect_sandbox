package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaResidentInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsanaResidentInsuranceDao extends JpaRepository<ConsanaResidentInsurance, Long> {

    Long countByResidentId(Long residentId);

}
