package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.InterfaxConfiguration;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfaxConfigurationDao extends JpaRepository<InterfaxConfiguration, Long>, JpaSpecificationExecutor<InterfaxConfiguration> {
}
