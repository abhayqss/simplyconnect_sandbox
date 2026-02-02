package com.scnsoft.eldermark.service.password;

import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;

import java.util.List;

public interface OrganizationPasswordSettingsService {

    List<OrganizationPasswordSettings> getOrganizationPasswordSettings(Long organizationId);

    List<OrganizationPasswordSettings> getOrganizationPasswordSettings(Long organizationId, boolean enabled);

    List<OrganizationPasswordSettings> createDefaultOrganizationPasswordSettings(Long organizationId);
}
