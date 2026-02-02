package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;

import java.util.List;

public interface DatabasePasswordSettingsDao extends BaseDao<DatabasePasswordSettings> {
    List<DatabasePasswordSettings> getOrganizationPasswordSettings(Long organizationId);
    DatabasePasswordSettings getOrganizationSpecificSetting(Long organizationId, PasswordSettingsType passwordSettingsType);
    void updateDatabasePasswordSettings(List<DatabasePasswordSettings> databasePasswordSettings);
}
