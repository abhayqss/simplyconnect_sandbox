package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.PasswordSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordSettingsDao extends JpaRepository<PasswordSettings, Long>, JpaSpecificationExecutor<PasswordSettings> {
}
