package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SystemSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSetupDao extends JpaRepository<SystemSetup, Long> {
    Boolean existsByLoginCompanyId(String loginCompanyId);
}
