package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.shared.carecoordination.DatabasePasswordSettingsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DatabasePasswordSettingsService {
    List<DatabasePasswordSettings> getOrganizationPasswordSettings(Long organizationId);
    List<DatabasePasswordSettings> createDefaultDatabasePasswordSettings(Long organizationId);
    void updateDatabasePasswordSettings(List<DatabasePasswordSettings> databasePasswordSettings);
    List<DatabasePasswordSettingsDto> getOrganizationPasswordSettingsDtoForEmployee(String loginName, String loginCompanyId);
    DatabasePasswordSettings getOrganizationSpecificSetting(Long organizationId, PasswordSettingsType passwordSettingsType);
    List<DatabasePasswordSettingsDto> getOrganizationPasswordSettingsDto(Long organizationId);
}
