package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationPasswordSettingsDao extends JpaRepository<OrganizationPasswordSettings, Long>, JpaSpecificationExecutor<OrganizationPasswordSettings> {

    List<OrganizationPasswordSettings> findAllByOrganizationId(Long organizationId);

    List<OrganizationPasswordSettings> findAllByOrganizationIdAndEnabled(Long organizationId, boolean enabled);

    OrganizationPasswordSettings getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(Long organizationId, PasswordSettingsType passwordSettingsType);
}
